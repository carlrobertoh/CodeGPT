package ee.carlrobert.codegpt.agent.clients

import ai.koog.agents.core.tools.ToolDescriptor
import ai.koog.prompt.dsl.ModerationResult
import ai.koog.prompt.executor.clients.LLMClient
import ai.koog.prompt.executor.ollama.client.ContextWindowStrategy
import ai.koog.prompt.executor.ollama.client.OllamaClient
import ai.koog.prompt.executor.ollama.client.OllamaParams
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.prompt.Prompt
import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.codecompletions.InfillRequest
import ee.carlrobert.codegpt.settings.models.ModelSettings
import ee.carlrobert.codegpt.settings.service.FeatureType
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettingsState
import ee.carlrobert.codegpt.util.JsonMapper
import io.ktor.client.HttpClient
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.Flow
import ai.koog.prompt.streaming.StreamFrame

class CodeGPTOllamaClient private constructor(
    private val delegate: OllamaClient,
    private val settings: OllamaSettingsState,
    private val baseUrl: String,
    private val apiKey: String?,
    private val baseClient: HttpClient
) : LLMClient(),
    CodeCompletionCapable {

    constructor(
        settings: OllamaSettingsState,
        baseUrl: String,
        apiKey: String?,
        baseClient: HttpClient = HttpClientProvider.createHttpClient()
    ) : this(
        delegate = OllamaClient(
            httpClientFactory = HttpClientProvider.resolveKoogHttpClientFactory(baseClient),
            baseUrl = baseUrl.trim().trimEnd('/'),
            headers = apiKey?.takeIf { it.isNotBlank() }?.let {
                mapOf(HttpHeaders.Authorization to "Bearer $it")
            } ?: emptyMap(),
            contextWindowStrategy = if (settings.contextSize > 0) {
                ContextWindowStrategy.Companion.Fixed(settings.contextSize.toLong())
            } else {
                ContextWindowStrategy.Companion.None
            }
        ),
        settings = settings,
        baseUrl = baseUrl,
        apiKey = apiKey,
        baseClient = baseClient
    )

    override fun llmProvider() = delegate.llmProvider()

    override suspend fun execute(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): Message.Assistant = delegate.execute(applyOllamaParams(prompt), ensureOllamaModel(model), tools)

    override fun executeStreaming(
        prompt: Prompt,
        model: LLModel,
        tools: List<ToolDescriptor>
    ): Flow<StreamFrame> = delegate.executeStreaming(applyOllamaParams(prompt), ensureOllamaModel(model), tools)

    override suspend fun moderate(prompt: Prompt, model: LLModel): ModerationResult =
        delegate.moderate(applyOllamaParams(prompt), ensureOllamaModel(model))

    override suspend fun models(): List<LLModel> = delegate.models()

    override suspend fun embed(text: String, model: LLModel): List<Double> =
        delegate.embed(text, ensureOllamaModel(model))

    override suspend fun embed(inputs: List<String>, model: LLModel): List<List<Double>> =
        delegate.embed(inputs, ensureOllamaModel(model))

    private fun ensureOllamaModel(model: LLModel): LLModel {
        return if (model.provider == LLMProvider.Ollama) {
            model
        } else {
            model.copy(provider = LLMProvider.Ollama)
        }
    }

    private fun applyOllamaParams(prompt: Prompt): Prompt {
        val currentParams = prompt.params
        val think = if (settings.thinkingEnabled) true else null
        val ollamaParams = OllamaParams(
            temperature = currentParams.temperature,
            maxTokens = currentParams.maxTokens,
            numberOfChoices = currentParams.numberOfChoices,
            speculation = currentParams.speculation,
            schema = currentParams.schema,
            toolChoice = currentParams.toolChoice,
            user = currentParams.user,
            additionalProperties = currentParams.additionalProperties,
            think = think
        )
        return prompt.withParams(ollamaParams)
    }

    override suspend fun getCodeCompletion(infillRequest: InfillRequest): String {
        val model = service<ModelSettings>().getModelSelectionForFeature(FeatureType.CODE_COMPLETION).modelId
        val stopTokens = buildList {
            if (infillRequest.stopTokens.isNotEmpty()) {
                addAll(infillRequest.stopTokens)
            }
        }.toMutableList()
        val prompt = if (settings.fimOverride) {
            settings.fimTemplate.stopTokens?.let { stopTokens.addAll(it) }
            settings.fimTemplate.buildPrompt(infillRequest)
        } else {
            infillRequest.prefix
        }

        val body = JsonMapper.mapper.writeValueAsString(
            mapOf(
                "model" to model,
                "prompt" to prompt,
                "suffix" to if (settings.fimOverride) null else infillRequest.suffix,
                "stream" to false,
                "raw" to true,
                "options" to mapOf(
                    "stop" to stopTokens.ifEmpty { null },
                    "num_predict" to MAX_COMPLETION_TOKENS,
                    "temperature" to 0.4
                ).filterValues { it != null }
            ).filterValues { it != null }
        )

        val headers = buildMap {
            apiKey?.takeIf { it.isNotBlank() }?.let {
                put(HttpHeaders.Authorization, "Bearer $it")
            }
        }

        return parseOllamaCompletion(
            postCompletionJson(
                client = baseClient,
                url = "$baseUrl/api/generate",
                headers = headers,
                body = body
            )
        )
    }

    override fun close() {
        delegate.close()
    }
}
