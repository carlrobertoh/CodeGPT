package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.completions.BaseRequestFactory
import ee.carlrobert.codegpt.completions.CallParameters
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.FIX_COMPILE_ERRORS_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.ConversationType
import ee.carlrobert.codegpt.completions.llama.LlamaModel
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings.Companion.getState
import ee.carlrobert.codegpt.settings.persona.PersonaSettings.Companion.getSystemPrompt
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest

class LlamaRequestFactory : BaseRequestFactory() {

    override fun createChatCompletionRequest(callParameters: CallParameters): LlamaCompletionRequest {
        val settings = service<LlamaSettings>().state
        val promptTemplate = if (settings.isRunLocalServer) {
            if (settings.isUseCustomModel)
                settings.localModelPromptTemplate
            else
                LlamaModel.findByHuggingFaceModel(settings.huggingFaceModel).promptTemplate
        } else {
            settings.remoteModelPromptTemplate
        }

        val systemPrompt =
            if (callParameters.conversationType == ConversationType.FIX_COMPILE_ERRORS)
                FIX_COMPILE_ERRORS_SYSTEM_PROMPT
            else
                getSystemPrompt()

        val prompt = promptTemplate.buildPrompt(
            systemPrompt,
            callParameters.message.prompt,
            callParameters.conversation.messages
        )
        val configuration = getState()
        return LlamaCompletionRequest.Builder(prompt)
            .setN_predict(configuration.maxTokens)
            .setTemperature(configuration.temperature.toDouble())
            .setTop_k(settings.topK)
            .setTop_p(settings.topP)
            .setMin_p(settings.minP)
            .setRepeat_penalty(settings.repeatPenalty)
            .setStop(promptTemplate.stopTokens)
            .build()
    }

    override fun createBasicCompletionRequest(
        systemPrompt: String,
        userPrompt: String,
        stream: Boolean
    ): LlamaCompletionRequest {
        val settings = service<LlamaSettings>().state
        val promptTemplate = if (settings.isRunLocalServer) {
            if (settings.isUseCustomModel)
                settings.localModelPromptTemplate
            else
                LlamaModel.findByHuggingFaceModel(settings.huggingFaceModel).promptTemplate
        } else {
            settings.remoteModelPromptTemplate
        }
        val configuration = service<ConfigurationSettings>().state
        val finalPrompt =
            promptTemplate.buildPrompt(systemPrompt, userPrompt, listOf())
        return LlamaCompletionRequest.Builder(finalPrompt)
            .setN_predict(configuration.maxTokens)
            .setTemperature(configuration.temperature.toDouble())
            .setTop_k(settings.topK)
            .setTop_p(settings.topP)
            .setMin_p(settings.minP)
            .setStream(stream)
            .setRepeat_penalty(settings.repeatPenalty)
            .build()
    }
}
