package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.completions.BaseRequestFactory
import ee.carlrobert.codegpt.completions.ChatCompletionRequestParameters
import ee.carlrobert.codegpt.completions.factory.OpenAIRequestFactory.Companion.buildOpenAIMessages
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest
import ee.carlrobert.llm.completion.CompletionRequest

class AzureRequestFactory : BaseRequestFactory() {

    override fun createChatRequest(params: ChatCompletionRequestParameters): OpenAIChatCompletionRequest {
        val configuration = service<ConfigurationSettings>().state
        val requestBuilder: OpenAIChatCompletionRequest.Builder =
            OpenAIChatCompletionRequest.Builder(buildOpenAIMessages(null, params.callParameters))
                .setMaxTokens(configuration.maxTokens)
                .setStream(true)
                .setTemperature(configuration.temperature.toDouble())
        return requestBuilder.build()
    }

    override fun createBasicCompletionRequest(
        systemPrompt: String,
        userPrompt: String,
        maxTokens: Int,
        stream: Boolean
    ): CompletionRequest {
        return OpenAIRequestFactory.createBasicCompletionRequest(
            systemPrompt,
            userPrompt,
            isStream = stream
        )
    }
}
