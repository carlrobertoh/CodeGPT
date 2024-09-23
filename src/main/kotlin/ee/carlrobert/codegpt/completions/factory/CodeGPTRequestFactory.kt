package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.completions.BaseRequestFactory
import ee.carlrobert.codegpt.completions.CallParameters
import ee.carlrobert.codegpt.completions.factory.OpenAIRequestFactory.Companion.buildOpenAIMessages
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest
import ee.carlrobert.llm.client.openai.completion.request.RequestDocumentationDetails

class CodeGPTRequestFactory : BaseRequestFactory() {

    override fun createChatRequest(callParameters: CallParameters): OpenAIChatCompletionRequest {
        val model = service<CodeGPTServiceSettings>().state.chatCompletionSettings.model
        val configuration = service<ConfigurationSettings>().state
        val requestBuilder: OpenAIChatCompletionRequest.Builder =
            OpenAIChatCompletionRequest.Builder(buildOpenAIMessages(model, callParameters))
                .setModel(model)
                .setMaxTokens(configuration.maxTokens)
                .setStream(true)
                .setTemperature(configuration.temperature.toDouble())
        if (callParameters.message.isWebSearchIncluded) {
            requestBuilder.setWebSearchIncluded(true)
        }
        val documentationDetails = callParameters.message.documentationDetails
        if (documentationDetails != null) {
            val requestDocumentationDetails = RequestDocumentationDetails()
            requestDocumentationDetails.name = documentationDetails.name
            requestDocumentationDetails.url = documentationDetails.url
            requestBuilder.setDocumentationDetails(requestDocumentationDetails)
        }
        return requestBuilder.build()
    }

    override fun createBasicCompletionRequest(
        systemPrompt: String,
        userPrompt: String,
        stream: Boolean
    ): OpenAIChatCompletionRequest {
        return OpenAIRequestFactory.createBasicCompletionRequest(
            systemPrompt,
            userPrompt,
            service<CodeGPTServiceSettings>().state.chatCompletionSettings.model,
            stream
        )
    }
}
