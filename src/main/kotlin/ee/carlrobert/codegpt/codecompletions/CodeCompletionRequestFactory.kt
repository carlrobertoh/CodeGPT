package ee.carlrobert.codegpt.codecompletions

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.completions.llama.LlamaModel
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.settings.configuration.Placeholder.*
import ee.carlrobert.codegpt.settings.persona.PersonaSettings.Companion.getSystemPrompt
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettingsState
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.llm.client.codegpt.request.CodeCompletionRequest
import ee.carlrobert.codegpt.settings.service.watsonx.WatsonxSettings
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest
import ee.carlrobert.llm.client.ollama.completion.request.OllamaCompletionRequest
import ee.carlrobert.llm.client.ollama.completion.request.OllamaParameters
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest
import ee.carlrobert.llm.client.watsonx.completion.WatsonxCompletionRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.nio.charset.StandardCharsets

object CodeCompletionRequestFactory {

    @JvmStatic
    fun buildWatsonxRequest(details: InfillRequest): WatsonxCompletionRequest {
        val settings = WatsonxSettings.getCurrentState();
        val builder = WatsonxCompletionRequest.Builder(details.prefix)
        builder.setDecodingMethod(if (settings.isGreedyDecoding) "greedy" else "sample")
        builder.setModelId(settings.model)
        builder.setProjectId(settings.projectId)
        builder.setSpaceId(settings.spaceId)
        builder.setMaxNewTokens(settings.maxNewTokens)
        builder.setMinNewTokens(settings.minNewTokens)
        builder.setTemperature(settings.temperature)
        builder.setStopSequences(
            if (settings.stopSequences.isEmpty()) null else settings.stopSequences.split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray())
        builder.setTopP(settings.topP)
        builder.setTopK(settings.topK)
        builder.setIncludeStopSequence(settings.includeStopSequence)
        builder.setRandomSeed(settings.randomSeed)
        builder.setRepetitionPenalty(settings.repetitionPenalty)
        builder.setStream(true)
        return builder.build()
    }

    private const val MAX_TOKENS = 128

    @JvmStatic
    fun buildCodeGPTRequest(details: InfillRequest): CodeCompletionRequest {
        val settings = service<CodeGPTServiceSettings>().state.codeCompletionSettings
        return CodeCompletionRequest.Builder()
            .setModel(settings.model)
            .setPrefix(details.prefix)
            .setSuffix(details.suffix)
            .setFileExtension(details.fileDetails?.fileExtension)
            .setFileContent(details.fileDetails?.fileContent)
            .setStagedDiff(details.vcsDetails?.stagedDiff)
            .setUnstagedDiff(details.vcsDetails?.unstagedDiff)
            .build()
    }

    @JvmStatic
    fun buildOpenAIRequest(details: InfillRequest): OpenAITextCompletionRequest {
        val (prefix, suffix) = getCompletionContext(details)
        return OpenAITextCompletionRequest.Builder(prefix)
            .setSuffix(suffix)
            .setStream(true)
            .setMaxTokens(MAX_TOKENS)
            .setTemperature(0.4)
            .build()
    }

    @JvmStatic
    fun buildCustomRequest(details: InfillRequest): Request {
        val settings = service<CustomServiceSettings>().state.codeCompletionSettings
        val credential = getCredential(CredentialKey.CUSTOM_SERVICE_API_KEY)
        return buildCustomRequest(
            details,
            settings.url!!,
            settings.headers,
            settings.body,
            settings.infillTemplate,
            credential
        )
    }

    @JvmStatic
    fun buildCustomRequest(
        details: InfillRequest,
        url: String,
        headers: Map<String, String>,
        body: Map<String, Any>,
        infillTemplate: InfillPromptTemplate,
        credential: String?
    ): Request {
        val requestBuilder = Request.Builder().url(url)
        for (entry in headers.entries) {
            var value = entry.value
            if (credential != null && value.contains("\$CUSTOM_SERVICE_API_KEY")) {
                value = value.replace("\$CUSTOM_SERVICE_API_KEY", credential)
            }
            requestBuilder.addHeader(entry.key, value)
        }
        val transformedBody = body.entries.associate { (key, value) ->
            key to transformValue(value, infillTemplate, details)
        }

        try {
            val requestBody = ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(transformedBody)
                .toByteArray(StandardCharsets.UTF_8)
                .toRequestBody("application/json".toMediaType())
            return requestBuilder.post(requestBody).build()
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    @JvmStatic
    fun buildLlamaRequest(details: InfillRequest): LlamaCompletionRequest {
        val settings = LlamaSettings.getCurrentState()
        val promptTemplate = getLlamaInfillPromptTemplate(settings)
        val prompt = promptTemplate.buildPrompt(details)
        return LlamaCompletionRequest.Builder(prompt)
            .setN_predict(MAX_TOKENS)
            .setStream(true)
            .setTemperature(0.4)
            .setStop(promptTemplate.stopTokens)
            .build()
    }

    fun buildOllamaRequest(details: InfillRequest): OllamaCompletionRequest {
        val settings = service<OllamaSettings>().state
        return OllamaCompletionRequest.Builder(
            settings.model,
            settings.fimTemplate.buildPrompt(details)
        )
            .setStream(true)
            .setOptions(
                OllamaParameters.Builder()
                    .stop(settings.fimTemplate.stopTokens)
                    .numPredict(MAX_TOKENS)
                    .temperature(0.4)
                    .build()
            )
            .setRaw(true)
            .build()
    }

    private fun getLlamaInfillPromptTemplate(settings: LlamaSettingsState): InfillPromptTemplate {
        if (!settings.isRunLocalServer) {
            return settings.remoteModelInfillPromptTemplate
        }
        if (settings.isUseCustomModel) {
            return settings.localModelInfillPromptTemplate
        }
        return LlamaModel.findByHuggingFaceModel(settings.huggingFaceModel).infillPromptTemplate
    }

    private fun transformValue(
        value: Any,
        template: InfillPromptTemplate,
        details: InfillRequest
    ): Any {
        if (value !is String) return value

        val (prefix, suffix) = getCompletionContext(details)
        return when (value) {
            FIM_PROMPT.code -> template.buildPrompt(details)
            PREFIX.code -> prefix
            SUFFIX.code -> suffix
            else -> {
                return value.takeIf { it.contains(PREFIX.code) || it.contains(SUFFIX.code) }
                    ?.replace(PREFIX.code, prefix)
                    ?.replace(SUFFIX.code, suffix) ?: value
            }
        }
        return 36
    }

    private fun getCompletionContext(request: InfillRequest): Pair<String, String> {
        val encodingManager = EncodingManager.getInstance()
        val truncatedPrefix = encodingManager.truncateText(request.prefix, 128, false)
        val truncatedSuffix = encodingManager.truncateText(request.suffix, 128, true)
        val vcsDetails = request.vcsDetails ?: return truncatedPrefix to truncatedSuffix

        val stagedDiff = if (vcsDetails.stagedDiff != null)
            encodingManager.truncateText(vcsDetails.stagedDiff, 200, true)
        else
            ""
        val unstagedDiff = if (vcsDetails.unstagedDiff != null)
            encodingManager.truncateText(vcsDetails.unstagedDiff, 200, true)
        else
            ""
        val prompt: String = if (vcsDetails.stagedDiff != null)
            """
            ${"/*\n${stagedDiff + unstagedDiff}\n\n*/"}
            $truncatedPrefix
            """.trimIndent()
        else
            truncatedPrefix

        return prompt to truncatedSuffix
    }
}