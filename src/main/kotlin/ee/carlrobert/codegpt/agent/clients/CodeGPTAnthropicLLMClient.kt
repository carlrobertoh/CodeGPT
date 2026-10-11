package ee.carlrobert.codegpt.agent.clients

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.http.client.KoogHttpClient
import ai.koog.prompt.Prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicClientSettings
import ai.koog.prompt.executor.clients.anthropic.AnthropicLLMClient
import ai.koog.prompt.executor.clients.anthropic.AnthropicParams
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.MessagePart
import ai.koog.prompt.params.LLMParams
import ai.koog.prompt.streaming.StreamFrame
import ee.carlrobert.codegpt.settings.models.Fable_5_1
import ee.carlrobert.codegpt.settings.models.Haiku_5_5
import ee.carlrobert.codegpt.settings.models.Opus_5_5
import ee.carlrobert.codegpt.settings.models.Sonnet_5_5
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

/**
 * Koog's Anthropic client sends request fields that Claude 5.5-generation models reject with a 400
 * (sampling params, `thinking.budget_tokens`, forced `tool_choice` on Fable/Opus/Sonnet), has no
 * adaptive thinking or effort settings, and drops thinking-block signatures while streaming.
 * Omitting `thinking` runs these models with adaptive thinking.
 *
 * Reasoning settings are read on every request, so changes apply to running sessions.
 *
 * @param reasoningEffort `output_config.effort` for adaptive-thinking models; null keeps the model default.
 * @param showReasoningSummary request readable reasoning summaries instead of omitted thinking text.
 */
class CodeGPTAnthropicLLMClient(
    apiKey: String,
    settings: AnthropicClientSettings,
    httpClientFactory: KoogHttpClient.Factory,
    private val reasoningEffort: () -> String? = { null },
    private val showReasoningSummary: () -> Boolean = { false },
) : AnthropicLLMClient(
    apiKey = apiKey,
    settings = settings,
    httpClientFactory = ThinkingCapturingHttpClient.Factory(httpClientFactory)
) {

    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): Message.Assistant {
        return super.execute(adapt(prompt, model, NON_STREAMING_MAX_TOKENS), model, tools)
    }

    override fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): Flow<StreamFrame> {
        val capture = ThinkingBlockCapture()
        val streamingMaxTokens = model.maxOutputTokens?.toInt() ?: NON_STREAMING_MAX_TOKENS
        return super.executeStreaming(adapt(prompt, model, streamingMaxTokens), model, tools)
            .flowOn(capture)
            .map { frame ->
                if (frame is StreamFrame.ReasoningComplete) capture.complete(frame) else frame
            }
    }

    private fun adapt(prompt: Prompt, model: LLModel, defaultMaxTokens: Int): Prompt {
        val adapted = prompt.withMessages { it.withReplayableReasoning() }
        if (!usesAdaptiveThinking(model)) {
            return adapted
        }

        val params = when (val params = prompt.params) {
            is AnthropicParams -> params
            else -> AnthropicParams(
                temperature = params.temperature,
                maxTokens = params.maxTokens,
                numberOfChoices = params.numberOfChoices,
                speculation = params.speculation,
                schema = params.schema,
                toolChoice = params.toolChoice,
                user = params.user,
                additionalProperties = params.additionalProperties
            )
        }
        val toolChoice = params.toolChoice
        val forcedToolChoice =
            toolChoice is LLMParams.ToolChoice.Required || toolChoice is LLMParams.ToolChoice.Named
        return adapted.withParams(
            params.copy(
                temperature = null,
                topP = null,
                topK = null,
                thinking = null,
                maxTokens = params.maxTokens ?: defaultMaxTokens.coerceAtMost(
                    (model.maxOutputTokens ?: defaultMaxTokens.toLong()).toInt()
                ),
                toolChoice = if (forcedToolChoice && model.id in autoToolChoiceOnlyModelIds) {
                    null
                } else {
                    toolChoice
                },
                // Koog only serializes these when it doesn't set the same top-level field itself.
                additionalProperties = (params.additionalProperties ?: emptyMap()) +
                        reasoningProperties(),
            )
        )
    }

    private fun reasoningProperties(): Map<String, JsonElement> = buildMap {
        reasoningEffort()?.takeIf { it.isNotBlank() }?.let { effort ->
            put("output_config", buildJsonObject { put("effort", effort) })
        }
        if (showReasoningSummary()) {
            put("thinking", buildJsonObject {
                put("type", "adaptive")
                put("display", "summarized")
            })
        }
    }

    companion object {
        // Without streaming a long response risks dropped idle connections, so keep it bounded.
        private const val NON_STREAMING_MAX_TOKENS = 16_000

        private val adaptiveThinkingModelIds =
            setOf(Fable_5_1.id, Opus_5_5.id, Sonnet_5_5.id, Haiku_5_5.id)
        private val autoToolChoiceOnlyModelIds = setOf(Fable_5_1.id, Opus_5_5.id, Sonnet_5_5.id)

        // Effort each model uses when the request doesn't set one.
        private val defaultEfforts = mapOf(
            Fable_5_1.id to "high",
            Opus_5_5.id to "medium",
            Sonnet_5_5.id to "high",
            Haiku_5_5.id to "medium",
        )

        fun usesAdaptiveThinking(model: LLModel): Boolean = model.id in adaptiveThinkingModelIds

        fun defaultEffort(model: LLModel): String? = defaultEfforts[model.id]

        // Replayed thinking blocks need their signatures. Blocks without one can't be replayed, and
        // removing only some of them breaks the chain the API verifies, so drop them all.
        private fun List<Message>.withReplayableReasoning(): List<Message> {
            val reasoningParts = filterIsInstance<Message.Assistant>()
                .flatMap { it.parts }
                .filterIsInstance<MessagePart.Reasoning>()
            if (reasoningParts.all { !it.encrypted.isNullOrEmpty() }) {
                return this
            }

            return mapNotNull { message ->
                if (message !is Message.Assistant || message.parts.none { it is MessagePart.Reasoning }) {
                    return@mapNotNull message
                }
                val parts = message.parts.filterNot { it is MessagePart.Reasoning }
                if (parts.isEmpty()) {
                    null
                } else {
                    Message.Assistant(
                        parts = parts,
                        metaInfo = message.metaInfo,
                        finishReason = message.finishReason,
                        rawResponse = message.rawResponse,
                        id = message.id,
                    )
                }
            }
        }
    }
}

/**
 * Collects the thinking text and signature of each streamed thinking block, which Koog's stream
 * parser doesn't keep, so the completed reasoning frames can be replayed to the API unchanged.
 */
private class ThinkingBlockCapture : AbstractCoroutineContextElement(ThinkingBlockCapture) {

    companion object Key : CoroutineContext.Key<ThinkingBlockCapture>

    private class Block(val text: StringBuilder, var signature: String?)

    private val blocks = ConcurrentHashMap<Int, Block>()

    fun observe(event: String) {
        if ("thinking" !in event && "signature" !in event) {
            return
        }
        val json = runCatching { Json.parseToJsonElement(event).jsonObject }.getOrNull() ?: return
        val index = json["index"]?.jsonPrimitive?.intOrNull ?: return
        when (json.string("type")) {
            "content_block_start" -> {
                val block = json["content_block"]?.jsonObject ?: return
                if (block.string("type") == "thinking") {
                    blocks[index] = Block(
                        StringBuilder(block.string("thinking").orEmpty()),
                        block.string("signature")
                    )
                }
            }

            "content_block_delta" -> {
                val block = blocks[index] ?: return
                val delta = json["delta"]?.jsonObject ?: return
                when (delta.string("type")) {
                    "thinking_delta" -> block.text.append(delta.string("thinking").orEmpty())
                    "signature_delta" -> block.signature = delta.string("signature")
                }
            }
        }
    }

    fun complete(frame: StreamFrame.ReasoningComplete): StreamFrame.ReasoningComplete {
        val block = frame.index?.let { blocks[it] } ?: return frame
        return frame.copy(
            content = listOf(block.text.toString()),
            encrypted = block.signature?.takeIf { it.isNotEmpty() } ?: frame.encrypted
        )
    }

    private fun Map<String, JsonElement>.string(key: String): String? =
        (this[key] as? JsonPrimitive)?.contentOrNull
}

/**
 * Lets [ThinkingBlockCapture] see the raw SSE events of the request it is attached to.
 */
private class ThinkingCapturingHttpClient(
    private val delegate: KoogHttpClient
) : KoogHttpClient by delegate {

    class Factory(private val delegate: KoogHttpClient.Factory) : KoogHttpClient.Factory {
        override fun create(
            clientName: String,
            baseUrl: String,
            headers: Map<String, String>,
            queryParameters: Map<String, String>,
            requestTimeoutMillis: Long,
            connectTimeoutMillis: Long,
            socketTimeoutMillis: Long,
            json: Json
        ): KoogHttpClient {
            return ThinkingCapturingHttpClient(
                delegate.create(
                    clientName,
                    baseUrl,
                    headers,
                    queryParameters,
                    requestTimeoutMillis,
                    connectTimeoutMillis,
                    socketTimeoutMillis,
                    json
                )
            )
        }
    }

    override fun <T : Any, R : Any, O : Any> sse(
        path: String,
        requestBody: T,
        requestBodyType: KClass<T>,
        dataFilter: (String?) -> Boolean,
        decodeStreamingResponse: (String) -> R,
        processStreamingChunk: (R) -> O?,
        parameters: Map<String, String>,
        headers: Map<String, String>
    ): Flow<O> = flow {
        val capture = currentCoroutineContext()[ThinkingBlockCapture]
        emitAll(
            delegate.sse(
                path = path,
                requestBody = requestBody,
                requestBodyType = requestBodyType,
                dataFilter = dataFilter,
                decodeStreamingResponse = { event ->
                    capture?.observe(event)
                    decodeStreamingResponse(event)
                },
                processStreamingChunk = processStreamingChunk,
                parameters = parameters,
                headers = headers
            )
        )
    }
}
