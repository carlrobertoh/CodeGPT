package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.completions.BaseRequestFactory
import ee.carlrobert.codegpt.completions.ChatCompletionParameters
import ee.carlrobert.codegpt.completions.factory.OpenAIRequestFactory.Companion.buildOpenAIMessages
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest
import ee.carlrobert.llm.completion.CompletionRequest

class AzureRequestFactory : BaseRequestFactory() {

    override fun createChatRequest(params: ChatCompletionParameters): OpenAIChatCompletionRequest {
        val configuration = service<ConfigurationSettings>().state
        val requestBuilder: OpenAIChatCompletionRequest.Builder =
            OpenAIChatCompletionRequest.Builder(buildOpenAIMessages(null, params))
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
