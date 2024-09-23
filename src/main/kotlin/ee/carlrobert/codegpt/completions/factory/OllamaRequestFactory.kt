package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.completions.BaseRequestFactory
import ee.carlrobert.codegpt.completions.CallParameters
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.FIX_COMPILE_ERRORS_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.ConversationType
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.persona.PersonaSettings
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.llm.client.ollama.completion.request.OllamaChatCompletionMessage
import ee.carlrobert.llm.client.ollama.completion.request.OllamaChatCompletionRequest
import ee.carlrobert.llm.client.ollama.completion.request.OllamaParameters
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class OllamaRequestFactory : BaseRequestFactory() {

    override fun createChatRequest(callParameters: CallParameters): OllamaChatCompletionRequest {
        val configuration = service<ConfigurationSettings>().state
        val settings = service<OllamaSettings>().state
        return OllamaChatCompletionRequest.Builder(
            settings.model,
            buildOllamaMessages(callParameters)
        )
            .setStream(true)
            .setOptions(
                OllamaParameters.Builder()
                    .numPredict(configuration.maxTokens)
                    .temperature(configuration.temperature.toDouble())
                    .build()
            )
            .build()
    }

    override fun createBasicCompletionRequest(
        systemPrompt: String,
        userPrompt: String,
        stream: Boolean
    ): OllamaChatCompletionRequest {
        return OllamaChatCompletionRequest.Builder(
            service<OllamaSettings>().state.model,
            listOf(
                OllamaChatCompletionMessage("system", systemPrompt, null),
                OllamaChatCompletionMessage("user", userPrompt, null)
            )
        )
            .setStream(stream)
            .build()
    }

    private fun buildOllamaMessages(callParameters: CallParameters): List<OllamaChatCompletionMessage> {
        val message = callParameters.message
        val messages = mutableListOf<OllamaChatCompletionMessage>()

        when (callParameters.conversationType) {
            ConversationType.DEFAULT -> messages.add(
                OllamaChatCompletionMessage("system", PersonaSettings.getSystemPrompt(), null)
            )

            ConversationType.FIX_COMPILE_ERRORS -> messages.add(
                OllamaChatCompletionMessage("system", FIX_COMPILE_ERRORS_SYSTEM_PROMPT, null)
            )

            else -> {}
        }

        for (prevMessage in callParameters.conversation.messages) {
            if (callParameters.isRetry && prevMessage.id == message.id) break

            prevMessage.imageFilePath?.takeIf { it.isNotEmpty() }?.let { imagePath ->
                try {
                    val imageBytes = Files.readAllBytes(Path.of(imagePath))
                    val imageBase64 = Base64.getEncoder().encodeToString(imageBytes)
                    messages.add(
                        OllamaChatCompletionMessage(
                            "user",
                            prevMessage.prompt,
                            listOf(imageBase64)
                        )
                    )
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            } ?: messages.add(OllamaChatCompletionMessage("user", prevMessage.prompt, null))

            messages.add(OllamaChatCompletionMessage("assistant", prevMessage.response, null))
        }

        if (callParameters.imageMediaType != null && callParameters.imageData.isNotEmpty()) {
            val imageBase64 = Base64.getEncoder().encodeToString(callParameters.imageData)
            messages.add(OllamaChatCompletionMessage("user", message.prompt, listOf(imageBase64)))
        } else {
            messages.add(OllamaChatCompletionMessage("user", message.prompt, null))
        }

        return messages
    }
}