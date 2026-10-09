package ee.carlrobert.codegpt.agent

import ai.koog.agents.core.agent.config.AIAgentConfig
import ai.koog.agents.core.agent.context.GraphAgentContextData
import ai.koog.agents.core.agent.context.RollbackStrategy
import ai.koog.agents.core.agent.context.store
import ai.koog.agents.core.agent.entity.AIAgentStorageKey
import ai.koog.agents.core.annotation.InternalAgentsApi
import ai.koog.agents.core.feature.AIAgentGraphFeature
import ai.koog.agents.core.feature.config.FeatureConfig
import ai.koog.agents.core.feature.pipeline.AIAgentGraphPipeline
import ai.koog.prompt.message.Message
import ai.koog.serialization.JSONElement
import ai.koog.serialization.JSONObject
import ai.koog.serialization.typeToken
import ee.carlrobert.codegpt.agent.strategy.SINGLE_RUN_NODE_CALL_LLM
import java.util.concurrent.ConcurrentHashMap

internal data class PendingRunContinuation(
    val messageHistory: List<Message>,
    val input: MessageWithContext
)

internal class PendingRunContinuationConfig : FeatureConfig() {
    lateinit var sessionId: String
    lateinit var pendingContinuations: ConcurrentHashMap<String, PendingRunContinuation>
}

@OptIn(InternalAgentsApi::class)
internal object PendingRunContinuationFeature :
    AIAgentGraphFeature<PendingRunContinuationConfig, Unit> {

    override val key: AIAgentStorageKey<Unit> = AIAgentStorageKey<Unit>("proxyai-pending-run-continuation", typeToken<Unit>())

    override fun createInitialConfig(agentConfig: AIAgentConfig): PendingRunContinuationConfig {
        return PendingRunContinuationConfig()
    }

    override fun install(config: PendingRunContinuationConfig, pipeline: AIAgentGraphPipeline) {
        pipeline.interceptStrategyStarting(this) { event ->
            val continuation = config.pendingContinuations.remove(config.sessionId) ?: return@interceptStrategyStarting
            val serializedInput = serializeInput(event.context.config, continuation.input)
            val (params, model, toolNames) = event.context.llm.readSession { session ->
                Triple(session.prompt.params, session.model, session.tools.map { tool -> tool.name })
            }
            event.context.store(
                GraphAgentContextData(
                    messageHistory = continuation.messageHistory,
                    llmParams = params,
                    llmModel = model,
                    tools = toolNames,
                    storage = JSONObject(emptyMap()),
                    agentIterations = 0,
                    nodePath = "${event.context.agentId}/single_run/$SINGLE_RUN_NODE_CALL_LLM",
                    lastOutput = serializedInput,
                    rollbackStrategy = RollbackStrategy.MessageHistoryOnly
                )
            )
        }
    }

    private fun serializeInput(config: AIAgentConfig, input: MessageWithContext): JSONElement {
        return config.serializer.encodeToJSONElement(input, typeToken<MessageWithContext>())
    }
}
