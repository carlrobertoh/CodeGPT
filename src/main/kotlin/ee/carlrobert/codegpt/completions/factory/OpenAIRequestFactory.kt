package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.completions.CallParameters
import ee.carlrobert.codegpt.completions.CompletionRequestFactory
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.EDIT_CODE_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.FIX_COMPILE_ERRORS_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.GENERATE_METHOD_NAMES_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.ConversationType
import ee.carlrobert.codegpt.completions.TotalUsageExceededException
import ee.carlrobert.codegpt.conversations.ConversationsState
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings.Companion.getState
import ee.carlrobert.codegpt.settings.persona.PersonaSettings.Companion.getSystemPrompt
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.util.file.FileUtil.getImageMediaType
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel
import ee.carlrobert.llm.client.openai.completion.request.*
import ee.carlrobert.llm.completion.CompletionRequest
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class OpenAIRequestFactory : CompletionRequestFactory {

    override fun createChatRequest(callParameters: CallParameters): OpenAIChatCompletionRequest {
        val model = service<OpenAISettings>().state.model
        val configuration = service<ConfigurationSettings>().state
        val requestBuilder: OpenAIChatCompletionRequest.Builder =
            OpenAIChatCompletionRequest.Builder(buildOpenAIMessages(model, callParameters))
                .setModel(model)
                .setMaxTokens(configuration.maxTokens)
                .setStream(true)
                .setTemperature(configuration.temperature.toDouble())
        return requestBuilder.build()
    }

    override fun createEditCodeRequest(input: String): OpenAIChatCompletionRequest {
        return buildEditCodeRequest(input, service<OpenAISettings>().state.model)
    }

    override fun createCommitMessageRequest(
        systemPrompt: String,
        gitDiff: String
    ): CompletionRequest {
        return createBasicCompletionRequest(
            systemPrompt,
            gitDiff,
            service<OpenAISettings>().state.model,
            true
        )
    }

    override fun createLookupRequest(prompt: String): CompletionRequest {
        return createBasicCompletionRequest(
            GENERATE_METHOD_NAMES_SYSTEM_PROMPT,
            prompt,
            service<OpenAISettings>().state.model
        )
    }

    companion object {
        fun buildEditCodeRequest(
            input: String,
            model: String? = null
        ): OpenAIChatCompletionRequest {
            return createBasicCompletionRequest(EDIT_CODE_SYSTEM_PROMPT, input, model, true)
        }

        fun buildOpenAIMessages(
            model: String?,
            callParameters: CallParameters
        ): List<OpenAIChatCompletionMessage> {
            val messages = buildOpenAIMessages(callParameters)

            if (model == null) {
                return messages
            }

            val encodingManager = EncodingManager.getInstance()
            val totalUsage = messages.parallelStream()
                .mapToInt { message: OpenAIChatCompletionMessage? ->
                    encodingManager.countMessageTokens(
                        message
                    )
                }
                .sum() + getState().maxTokens
            val modelMaxTokens: Int
            try {
                modelMaxTokens = OpenAIChatCompletionModel.findByCode(model).maxTokens

                if (totalUsage <= modelMaxTokens) {
                    return messages
                }
            } catch (ex: NoSuchElementException) {
                return messages
            }
            return tryReducingMessagesOrThrow(
                messages,
                callParameters.conversation.isDiscardTokenLimit,
                totalUsage,
                modelMaxTokens
            )
        }

        private fun buildOpenAIMessages(
            callParameters: CallParameters
        ): MutableList<OpenAIChatCompletionMessage> {
            val message = callParameters.message
            val messages = mutableListOf<OpenAIChatCompletionMessage>()
            if (callParameters.conversationType == ConversationType.DEFAULT) {
                val sessionPersonaDetails = callParameters.message.personaDetails
                if (callParameters.message.personaDetails == null) {
                    messages.add(
                        OpenAIChatCompletionStandardMessage("system", getSystemPrompt())
                    )
                } else {
                    messages.add(
                        OpenAIChatCompletionStandardMessage(
                            "system",
                            sessionPersonaDetails.instructions
                        )
                    )
                }
            }
            if (callParameters.conversationType == ConversationType.FIX_COMPILE_ERRORS) {
                messages.add(
                    OpenAIChatCompletionStandardMessage("system", FIX_COMPILE_ERRORS_SYSTEM_PROMPT)
                )
            }

            for (prevMessage in callParameters.conversation.messages) {
                if (callParameters.isRetry && prevMessage.id == message.id) {
                    break
                }
                val prevMessageImageFilePath = prevMessage.imageFilePath
                if (!prevMessageImageFilePath.isNullOrEmpty()) {
                    try {
                        val imageFilePath = Path.of(prevMessageImageFilePath)
                        val imageData = Files.readAllBytes(imageFilePath)
                        val imageMediaType = getImageMediaType(imageFilePath.fileName.toString())
                        messages.add(
                            OpenAIChatCompletionDetailedMessage(
                                "user",
                                listOf(
                                    OpenAIMessageImageURLContent(
                                        OpenAIImageUrl(
                                            imageMediaType,
                                            imageData
                                        )
                                    ),
                                    OpenAIMessageTextContent(prevMessage.prompt)
                                )
                            )
                        )
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                } else {
                    messages.add(OpenAIChatCompletionStandardMessage("user", prevMessage.prompt))
                }
                messages.add(
                    OpenAIChatCompletionStandardMessage("assistant", prevMessage.response)
                )
            }

            if (callParameters.imageMediaType != null && callParameters.imageData.isNotEmpty()) {
                messages.add(
                    OpenAIChatCompletionDetailedMessage(
                        "user",
                        listOf(
                            OpenAIMessageImageURLContent(
                                OpenAIImageUrl(
                                    callParameters.imageMediaType,
                                    callParameters.imageData
                                )
                            ),
                            OpenAIMessageTextContent(message.prompt)
                        )
                    )
                )
            } else {
                messages.add(OpenAIChatCompletionStandardMessage("user", message.prompt))
            }
            return messages
        }

        private fun tryReducingMessagesOrThrow(
            messages: MutableList<OpenAIChatCompletionMessage>,
            discardTokenLimit: Boolean,
            totalInputUsage: Int,
            modelMaxTokens: Int
        ): List<OpenAIChatCompletionMessage> {
            val result: MutableList<OpenAIChatCompletionMessage?> = messages.toMutableList()
            var totalUsage = totalInputUsage
            if (!ConversationsState.getInstance().discardAllTokenLimits) {
                if (!discardTokenLimit) {
                    throw TotalUsageExceededException()
                }
            }
            val encodingManager = EncodingManager.getInstance()
            // skip the system prompt
            for (i in 1 until result.size - 1) {
                if (totalUsage <= modelMaxTokens) {
                    break
                }

                val message = result[i]
                if (message is OpenAIChatCompletionStandardMessage) {
                    totalUsage -= encodingManager.countMessageTokens(message)
                    result[i] = null
                }
            }

            return result.filterNotNull()
        }

        fun createBasicCompletionRequest(
            systemPrompt: String,
            userPrompt: String,
            model: String? = null,
            isStream: Boolean = false
        ): OpenAIChatCompletionRequest {
            return OpenAIChatCompletionRequest.Builder(
                listOf(
                    OpenAIChatCompletionStandardMessage("system", systemPrompt),
                    OpenAIChatCompletionStandardMessage("user", userPrompt)
                )
            )
                .setModel(model)
                .setStream(isStream)
                .build()
        }
    }
}
