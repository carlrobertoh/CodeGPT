package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.completions.BaseRequestFactory
import ee.carlrobert.codegpt.completions.ChatCompletionParameters
import ee.carlrobert.codegpt.completions.ConversationType
import ee.carlrobert.codegpt.completions.llama.LlamaModel
import ee.carlrobert.codegpt.completions.llama.PromptTemplate
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.prompts.PromptsSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest

class LlamaRequestFactory : BaseRequestFactory() {

    override fun createChatRequest(params: ChatCompletionParameters): LlamaCompletionRequest {
        val promptTemplate = getPromptTemplate()
        val systemPrompt =
            if (params.conversationType == ConversationType.FIX_COMPILE_ERRORS) {
                service<PromptsSettings>().state.coreActions.fixCompileErrors.instructions
            } else {
                service<PromptsSettings>().state.personas.selectedPersona.let {
                    if (it.disabled) null else it.instructions
                }
            }

        val prompt = promptTemplate.buildPrompt(
            systemPrompt,
            getPromptWithFilesContext(params),
            params.conversation.messages
        )

        return buildLlamaRequest(prompt, promptTemplate.stopTokens, true)
    }

    override fun createBasicCompletionRequest(
        systemPrompt: String,
        userPrompt: String,
        maxTokens: Int,
        stream: Boolean
    ): LlamaCompletionRequest {
        val promptTemplate = getPromptTemplate()
        val finalPrompt =
            promptTemplate.buildPrompt(systemPrompt, userPrompt, listOf())

        return buildLlamaRequest(finalPrompt, emptyList(), stream)
    }

    private fun getPromptTemplate(): PromptTemplate {
        val settings = service<LlamaSettings>().state
        return if (settings.isRunLocalServer) {
            if (settings.isUseCustomModel)
                settings.localModelPromptTemplate
            else
                LlamaModel.findByHuggingFaceModel(settings.huggingFaceModel).promptTemplate
        } else {
            settings.remoteModelPromptTemplate
        }
    }

    private fun buildLlamaRequest(
        prompt: String,
        stopTokens: List<String>,
        stream: Boolean = false
    ): LlamaCompletionRequest {
        val configSettings = service<ConfigurationSettings>().state
        val llamaSettings = service<LlamaSettings>().state
        return LlamaCompletionRequest.Builder(prompt)
            .setN_predict(configSettings.maxTokens)
            .setTemperature(configSettings.temperature.toDouble())
            .setTop_k(llamaSettings.topK)
            .setTop_p(llamaSettings.topP)
            .setMin_p(llamaSettings.minP)
            .setRepeat_penalty(llamaSettings.repeatPenalty)
            .setStop(stopTokens)
            .setStream(stream)
            .build()
    }
}
