package ee.carlrobert.codegpt.agent.strategy

import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.entity.AIAgentGraphStrategy
import ai.koog.agents.core.agent.session.AIAgentLLMWriteSession
import ai.koog.agents.core.dsl.builder.AIAgentEdgeBuilderIntermediate
import ai.koog.agents.core.dsl.builder.EdgeTransformationDslMarker
import ai.koog.agents.core.dsl.builder.node
import ai.koog.agents.core.dsl.builder.strategy
import ai.koog.agents.core.dsl.extension.*
import ai.koog.agents.core.environment.ReceivedToolResult
import ai.koog.agents.features.tokenizer.feature.tokenizer
import ai.koog.prompt.Prompt
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.message.Message
import ai.koog.prompt.streaming.StreamFrame
import ai.koog.prompt.streaming.toMessageResponse
import ai.koog.prompt.tokenizer.PromptTokenizer
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.agent.AgentEvents
import ee.carlrobert.codegpt.agent.MessageWithContext
import ee.carlrobert.codegpt.agent.credits.extractCreditsSnapshot
import ee.carlrobert.codegpt.agent.normalizeToolArgumentsJson
import ee.carlrobert.codegpt.completions.CompletionRequestUtil
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.toolwindow.agent.AgentCreditsEvent
import ee.carlrobert.codegpt.ui.textarea.TagProcessorFactory
import ee.carlrobert.codegpt.ui.textarea.header.tag.TagDetails
import java.util.*
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import ee.carlrobert.codegpt.conversations.message.Message as ChatMessage

internal interface AgentRunStrategyProvider {
    fun build(
        project: Project,
        executor: PromptExecutor,
        pendingMessageQueue: ArrayDeque<MessageWithContext>,
        historyCompressionConfig: HistoryCompressionConfig,
        events: AgentEvents,
        sessionId: String,
        provider: ServiceType,
        stream: Boolean
    ): AIAgentGraphStrategy<MessageWithContext, String>
}

data class HistoryCompressionConfig(
    val isLimitExceeded: (Prompt, PromptTokenizer) -> Boolean,
    val compressionStrategy: HistoryCompressionStrategy
)

internal const val SINGLE_RUN_NODE_CALL_LLM = "call_llm"

internal class SingleRunStrategyProvider : AgentRunStrategyProvider {
    @OptIn(ExperimentalAtomicApi::class)
    override fun build(
        project: Project,
        executor: PromptExecutor,
        pendingMessageQueue: ArrayDeque<MessageWithContext>,
        historyCompressionConfig: HistoryCompressionConfig,
        events: AgentEvents,
        sessionId: String,
        provider: ServiceType,
        stream: Boolean
    ): AIAgentGraphStrategy<MessageWithContext, String> =
        strategy<MessageWithContext, String>("single_run") {
            val planCreated = AtomicBoolean(false)
            val nodeCallLLM by node<MessageWithContext, ai.koog.prompt.message.Message.Assistant>(
                SINGLE_RUN_NODE_CALL_LLM
            ) { message ->
                llm.writeSession {
                    if (message.tags.isNotEmpty()) {
                        val context = buildTagContext(project, message.tags)
                        if (context.isNotBlank()) {
                            appendPrompt { user(context) }
                        }
                    }

                    appendPrompt { user(message.text) }

                    requestAndPublish(
                        executor,
                        config,
                        tokenizer(),
                        events,
                        sessionId,
                        provider,
                        stream
                    )
                }
            }

            val nodeExecuteTool by nodeExecuteTools(parallel = true)

            val nodeSendToolResult by node<ReceivedToolResults, ai.koog.prompt.message.Message.Assistant>("send_tool_results") { toolResultsWrapper ->
                val results = toolResultsWrapper.toolResults
                llm.writeSession {
                    appendPrompt {
                        results.forEach { toolResult(it.toMessagePart()) }
                    }

                    val messages = prompt.messages
                    val toolCalls = messages.filterIsInstance<ai.koog.prompt.message.Message.Assistant>()
                        .flatMap { it.parts }
                        .filterIsInstance<ai.koog.prompt.message.MessagePart.Tool.Call>()
                    val toolCallMessages = toolCalls.size
                    val todoWriteToolUsed = toolCalls.any { it.tool == "TodoWrite" }

                    if (!planCreated.load() && toolCallMessages >= 3 && !todoWriteToolUsed) {
                        appendPrompt {
                            user("It seems that you haven't created a todo list yet. If the task on hand requires multiple steps then create a todo list to track your changes.")
                        }
                        planCreated.store(true)
                    }

                    if (pendingMessageQueue.isNotEmpty()) {
                        pendingMessageQueue.forEach {
                            appendPrompt { user(it.text) }
                        }
                        events.onQueuedMessagesResolved()
                        pendingMessageQueue.clear()
                    }

                    requestAndPublish(
                        executor,
                        config,
                        tokenizer(),
                        events,
                        sessionId,
                        provider,
                        stream
                    )
                }
            }
            val nodeShowCompressionLoading by node<ReceivedToolResults, ReceivedToolResults>(
                "show_compression_loading"
            ) {
                events.onHistoryCompressionStateChanged(true)
                it
            }
            val nodeCompressHistory by nodeLLMCompressHistory<ReceivedToolResults>(strategy = historyCompressionConfig.compressionStrategy)
            val nodeResetCompressionLoading by node<ReceivedToolResults, ReceivedToolResults>(
                "reset_compression_loading"
            ) {
                events.onHistoryCompressionStateChanged(false)
                it
            }
            val nodeSendCompressedHistory by node<ReceivedToolResults, ai.koog.prompt.message.Message.Assistant>(
                "send_compressed_history"
            ) {
                llm.writeSession {
                    requestAndPublish(
                        executor,
                        config,
                        tokenizer(),
                        events,
                        sessionId,
                        provider,
                        stream
                    )
                }
            }

            edge(nodeStart forwardTo nodeCallLLM)
            edge(nodeCallLLM forwardTo nodeExecuteTool onToolCalls { true })
            edge(nodeCallLLM forwardTo nodeFinish onSingleAssistantResponse { true })
            edge(nodeExecuteTool forwardTo nodeSendToolResult onCondition {
                llm.readSession {
                    !historyCompressionConfig.isLimitExceeded(prompt, tokenizer())
                }
            })
            edge(nodeExecuteTool forwardTo nodeShowCompressionLoading onCondition {
                llm.readSession {
                    historyCompressionConfig.isLimitExceeded(prompt, tokenizer())
                }
            })
            edge(nodeShowCompressionLoading forwardTo nodeCompressHistory)
            edge(nodeCompressHistory forwardTo nodeResetCompressionLoading)
            edge(nodeResetCompressionLoading forwardTo nodeSendCompressedHistory)
            edge(nodeSendToolResult forwardTo nodeFinish onSingleAssistantResponse { true })
            edge(nodeSendToolResult forwardTo nodeExecuteTool onToolCalls { true })
            edge(nodeSendCompressedHistory forwardTo nodeFinish onSingleAssistantResponse { true })
            edge(nodeSendCompressedHistory forwardTo nodeExecuteTool onToolCalls { true })
        }
}

private suspend fun AIAgentLLMWriteSession.requestResponses(
    stream: Boolean,
    executor: PromptExecutor,
    config: AIAgentConfig,
    appendResponse: (ai.koog.prompt.message.Message.Assistant) -> Unit
): ai.koog.prompt.message.Message.Assistant {
    val response = if (stream) {
        val streamFrames = mutableListOf<StreamFrame>()
        requestLLMStreaming().collect { streamFrames.add(it) }
        streamFrames.toMessageResponse()
    } else {
        val preparedPrompt = config.missingToolsConversionStrategy.convertPrompt(prompt, tools)
        executor.execute(preparedPrompt, model, tools) as ai.koog.prompt.message.Message.Assistant
    }
    appendResponse(response)
    return response
}

private suspend fun AIAgentLLMWriteSession.requestAndPublish(
    executor: PromptExecutor,
    config: AIAgentConfig,
    tokenizer: PromptTokenizer,
    events: AgentEvents,
    sessionId: String,
    provider: ServiceType,
    stream: Boolean
): ai.koog.prompt.message.Message.Assistant {
    val response = requestResponses(stream, executor, config) { appendPrompt { message(it) } }
    publishUsageAndCredits(prompt, tokenizer, events, sessionId, provider, listOf(response))
    return response
}

internal fun appendableResponses(
    responses: List<ai.koog.prompt.message.Message.Assistant>,
    provider: LLMProvider
): List<ai.koog.prompt.message.Message.Assistant> {
    return responses
}

internal fun extractAssistantOutput(response: ai.koog.prompt.message.Message.Assistant): String {
    return ee.carlrobert.codegpt.completions.CompletionTextExtractor.extract(listOf(response))
}

private fun publishUsageAndCredits(
    prompt: Prompt,
    tokenizer: PromptTokenizer,
    events: AgentEvents,
    sessionId: String,
    provider: ServiceType,
    responses: List<ai.koog.prompt.message.Message.Assistant>,
) {
    events.onTokenUsageAvailable(promptTokenCount(prompt, tokenizer, responses).toLong())
    publishCreditsIfAvailable(sessionId, provider, responses, events)
}

private fun promptTokenCount(
    prompt: Prompt,
    tokenizer: PromptTokenizer,
    responses: List<ai.koog.prompt.message.Message.Assistant>
): Int {
    return if (prompt.latestTokenUsage == 0) {
        tokenizer.tokenCountFor(prompt)
    } else {
        prompt.latestTokenUsage
    }
}

private fun buildTagContext(project: Project, tags: List<TagDetails>): String {
    val contextBuilder = StringBuilder()
    val tempMessage = ChatMessage("")
    tags.forEach { tagDetails ->
        val processor = TagProcessorFactory.getProcessor(project, tagDetails)
        processor.process(tempMessage, contextBuilder)
    }

    val fileContext = buildReferencedFilesContext(tempMessage.referencedFilePaths.orEmpty())
    val tagText = contextBuilder.toString().trim()
    return buildString {
        var hasContent = false
        if (tagText.isNotBlank()) {
            append(tagText)
            hasContent = true
        }
        if (fileContext.isNotBlank()) {
            if (hasContent) {
                append("\n\n")
            }
            append(fileContext)
        }
    }.trim()
}

private fun buildReferencedFilesContext(referencedFilePaths: List<String>): String {
    if (referencedFilePaths.isEmpty()) return ""
    val vfs = LocalFileSystem.getInstance()
    val referencedFiles = referencedFilePaths
        .asSequence()
        .mapNotNull { path -> vfs.findFileByPath(path) }
        .distinctBy { it.path }
        .map { ReferencedFile.from(it) }
        .toList()
    if (referencedFiles.isEmpty()) return ""
    return CompletionRequestUtil.buildContextBlock(referencedFiles, null)
}

private fun publishCreditsIfAvailable(
    sessionId: String,
    provider: ServiceType,
    responses: List<ai.koog.prompt.message.Message.Assistant>,
    events: AgentEvents,
) {
    if (provider != ServiceType.PROXYAI) return
    val credits = extractCreditsSnapshot(responses) ?: return
    val event = AgentCreditsEvent(
        sessionId = sessionId,
        remaining = credits.remaining,
        monthlyRemaining = credits.monthlyRemaining,
        consumed = credits.total
    )
    events.onCreditsAvailable(event)
}

@EdgeTransformationDslMarker
private infix fun <IncomingOutput, OutgoingInput> AIAgentEdgeBuilderIntermediate<IncomingOutput, ai.koog.prompt.message.Message.Assistant, OutgoingInput>.onSingleAssistantResponse(
    block: suspend (String) -> Boolean
): AIAgentEdgeBuilderIntermediate<IncomingOutput, String, OutgoingInput> {
    return onIsInstance(ai.koog.prompt.message.Message.Assistant::class)
        .transformed { response ->
            if (response.parts.any { it is ai.koog.prompt.message.MessagePart.Tool.Call }) {
                null
            } else {
                extractAssistantOutput(response)
            }
        }
        .onCondition { it != null }
        .transformed { it!! }
        .onCondition { message -> block(message) }
}
