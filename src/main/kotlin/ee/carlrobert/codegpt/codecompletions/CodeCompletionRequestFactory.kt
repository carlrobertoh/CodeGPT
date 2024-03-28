package ee.carlrobert.codegpt.codecompletions

import ee.carlrobert.codegpt.completions.llama.LlamaModel
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest

object CodeCompletionRequestFactory {
    private const val MAX_TOKENS = 128

    fun buildOpenAIRequest(details: InfillRequestDetails): OpenAITextCompletionRequest {
        return OpenAITextCompletionRequest.Builder(details.prefix)
            .setSuffix(details.suffix)
            .setStream(true)
            .setMaxTokens(MAX_TOKENS)
            .setTemperature(0.4)
            .build()
    }

    fun buildLlamaRequest(details: InfillRequestDetails): LlamaCompletionRequest {
        val promptTemplate = getLlamaInfillPromptTemplate()
        val prompt = promptTemplate.buildPrompt(details.prefix, details.suffix)
        return LlamaCompletionRequest.Builder(prompt)
            .setN_predict(MAX_TOKENS)
            .setStream(true)
            .setTemperature(0.4)
            .setStop(promptTemplate.stopTokens)
            .build()
    }

    private fun getLlamaInfillPromptTemplate(): InfillPromptTemplate {
        val settings = LlamaSettings.getCurrentState()
        if (!settings.isRunLocalServer) {
            return settings.remoteModelInfillPromptTemplate
        }
        if (settings.isUseCustomModel) {
            return settings.localModelInfillPromptTemplate
        }
        return LlamaModel.findByHuggingFaceModel(settings.huggingFaceModel).infillPromptTemplate
    }
}