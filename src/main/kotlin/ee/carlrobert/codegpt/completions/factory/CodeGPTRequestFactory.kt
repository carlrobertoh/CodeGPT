package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.completions.BaseRequestFactory
import ee.carlrobert.codegpt.completions.ChatCompletionRequestParameters
import ee.carlrobert.codegpt.completions.factory.OpenAIRequestFactory.Companion.buildBasicO1Request
import ee.carlrobert.codegpt.completions.factory.OpenAIRequestFactory.Companion.buildOpenAIMessages
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest
import ee.carlrobert.llm.client.openai.completion.request.RequestDocumentationDetails

class CodeGPTRequestFactory : BaseRequestFactory() {

    override fun createChatRequest(params: ChatCompletionRequestParameters): OpenAIChatCompletionRequest {
        val (callParameters) = params
        val model = service<CodeGPTServiceSettings>().state.chatCompletionSettings.model
        val configuration = service<ConfigurationSettings>().state
        val requestBuilder: OpenAIChatCompletionRequest.Builder =
            OpenAIChatCompletionRequest.Builder(buildOpenAIMessages(model, callParameters))
                .setModel(model)
        if ("o1-mini" == model || "o1-preview" == model) {
            requestBuilder
                .setMaxCompletionTokens(configuration.maxTokens)
                .setStream(false)
                .setMaxTokens(null)
                .setTemperature(null)
        } else {
            requestBuilder
                .setStream(true)
                .setMaxTokens(configuration.maxTokens)
                .setTemperature(configuration.temperature.toDouble())
        }

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
        maxTokens: Int,
        stream: Boolean
    ): OpenAIChatCompletionRequest {
        val model = service<CodeGPTServiceSettings>().state.chatCompletionSettings.model
        if (model == "o1-mini" || model == "o1-preview") {
            return buildBasicO1Request(model, userPrompt, systemPrompt, maxTokens)
        }
        return OpenAIRequestFactory.createBasicCompletionRequest(
            systemPrompt,
            userPrompt,
            model,
            stream
        )
    }
}
