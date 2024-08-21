package ee.carlrobert.codegpt.codecompletions

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.completions.llama.LlamaModel
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.settings.configuration.Placeholder.*
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettingsState
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest
import ee.carlrobert.llm.client.ollama.completion.request.OllamaCompletionRequest
import ee.carlrobert.llm.client.ollama.completion.request.OllamaParameters
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.nio.charset.StandardCharsets

object CodeCompletionRequestFactory {

    @JvmStatic
    fun buildCodeGPTRequest(details: InfillRequestDetails): OpenAITextCompletionRequest {
        val settings = service<CodeGPTServiceSettings>().state.codeCompletionSettings
        return OpenAITextCompletionRequest.Builder(details.prefix)
            .setSuffix(details.suffix)
            .setStream(true)
            .setModel(settings.model)
            .setMaxTokens(getMaxTokens(details.prefix, details.suffix))
            .setTemperature(0.4)
            .build()
    }

    @JvmStatic
    fun buildOpenAIRequest(details: InfillRequestDetails): OpenAITextCompletionRequest {
        return OpenAITextCompletionRequest.Builder(details.prefix)
            .setSuffix(details.suffix)
            .setStream(true)
            .setMaxTokens(getMaxTokens(details.prefix, details.suffix))
            .setTemperature(0.4)
            .build()
    }

    @JvmStatic
    fun buildCustomRequest(details: InfillRequestDetails): Request {
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
        details: InfillRequestDetails,
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
    fun buildLlamaRequest(details: InfillRequestDetails): LlamaCompletionRequest {
        val settings = LlamaSettings.getCurrentState()
        val promptTemplate = getLlamaInfillPromptTemplate(settings)
        val prompt = promptTemplate.buildPrompt(details)
        println("PROMPT: ")
        println(prompt)
        return LlamaCompletionRequest.Builder(prompt)
            .setN_predict(getMaxTokens(details.prefix, details.suffix))
            .setStream(true)
            .setTemperature(0.4)
            .setStop(promptTemplate.stopTokens)
            .build()
    }

    fun buildOllamaRequest(details: InfillRequestDetails): OllamaCompletionRequest {
        val settings = service<OllamaSettings>().state
        return OllamaCompletionRequest.Builder(
            settings.model,
            settings.fimTemplate.buildPrompt(details)
        )
            .setOptions(
                OllamaParameters.Builder()
                    .stop(settings.fimTemplate.stopTokens)
                    .numPredict(getMaxTokens(details.prefix, details.suffix))
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
        details: InfillRequestDetails
    ): Any {
        if (value !is String) return value

        return when (value) {
            FIM_PROMPT.code -> template.buildPrompt(details)
            PREFIX.code -> details.prefix
            SUFFIX.code -> details.suffix
            else -> {
                return value.takeIf { it.contains(PREFIX.code) || it.contains(SUFFIX.code) }
                    ?.replace(PREFIX.code, details.prefix)
                    ?.replace(SUFFIX.code, details.suffix) ?: value
            }
        }
    }

    private fun getMaxTokens(prefix: String, suffix: String): Int {
        if ((prefix.isNotEmpty() && isBoundaryCharacter(prefix[prefix.length - 1]))
            || (suffix.isNotEmpty() && isBoundaryCharacter(suffix[0]))
        ) {
            return 16
        }
        return 36
    }

    private fun isBoundaryCharacter(c: Char): Boolean = c in "()[]{}<>~!@#$%^&*-+=|\\;:'\",./?"
}