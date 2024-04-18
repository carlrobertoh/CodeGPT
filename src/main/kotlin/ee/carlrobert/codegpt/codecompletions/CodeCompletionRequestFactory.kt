package ee.carlrobert.codegpt.codecompletions

import ee.carlrobert.codegpt.completions.llama.LlamaModel
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettingsState
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest

object CodeCompletionRequestFactory {
    fun buildOpenAIRequest(details: InfillRequestDetails): OpenAITextCompletionRequest {
        return OpenAITextCompletionRequest.Builder(details.prefix)
            .setSuffix(details.suffix)
            .setStream(true)
            .setMaxTokens(OpenAISettings.getCurrentState().codeCompletionMaxTokens)
            .setTemperature(0.4)
            .build()
    }

    fun buildLlamaRequest(details: InfillRequestDetails): LlamaCompletionRequest {
        val settings = LlamaSettings.getCurrentState()
        val promptTemplate = getLlamaInfillPromptTemplate(settings)
        val prompt = promptTemplate.buildPrompt(details.prefix, details.suffix)
        return LlamaCompletionRequest.Builder(prompt)
            .setN_predict(settings.codeCompletionMaxTokens)
            .setStream(true)
            .setTemperature(0.4)
            .setStop(promptTemplate.stopTokens)
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
}