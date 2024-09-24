package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.completions.BaseRequestFactory
import ee.carlrobert.codegpt.completions.CallParameters
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.persona.PersonaSettings
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings
import ee.carlrobert.llm.client.anthropic.completion.*
import ee.carlrobert.llm.completion.CompletionRequest

class ClaudeRequestFactory : BaseRequestFactory() {

    override fun createChatRequest(callParameters: CallParameters): ClaudeCompletionRequest {
        return ClaudeCompletionRequest().apply {
            model = service<AnthropicSettings>().state.model
            maxTokens = service<ConfigurationSettings>().state.maxTokens
            isStream = true
            system = PersonaSettings.getSystemPrompt()

            messages = callParameters.conversation.messages
                .filter { it.response != null && it.response.isNotEmpty() }
                .flatMap { prevMessage ->
                    sequenceOf(
                        ClaudeCompletionStandardMessage("user", prevMessage.prompt),
                        ClaudeCompletionStandardMessage("assistant", prevMessage.response)
                    )
                }

            when {
                callParameters.imageMediaType != null && callParameters.imageData.isNotEmpty() -> {
                    messages.add(
                        ClaudeCompletionDetailedMessage(
                            "user",
                            listOf(
                                ClaudeMessageImageContent(
                                    ClaudeBase64Source(
                                        callParameters.imageMediaType,
                                        callParameters.imageData
                                    )
                                ),
                                ClaudeMessageTextContent(callParameters.message.prompt)
                            )
                        )
                    )
                }

                else -> {
                    messages.add(
                        ClaudeCompletionStandardMessage("user", callParameters.message.prompt)
                    )
                }
            }
        }
    }

    override fun createBasicCompletionRequest(
        systemPrompt: String,
        userPrompt: String,
        stream: Boolean
    ): CompletionRequest {
        return ClaudeCompletionRequest().apply {
            system = systemPrompt
            isStream = stream
            maxTokens = service<ConfigurationSettings>().state.maxTokens
            model = service<AnthropicSettings>().state.model
            messages =
                listOf<ClaudeCompletionMessage>(ClaudeCompletionStandardMessage("user", userPrompt))
        }
    }
}
