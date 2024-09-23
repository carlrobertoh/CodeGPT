package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.completions.BaseRequestFactory
import ee.carlrobert.codegpt.completions.CallParameters
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.FIX_COMPILE_ERRORS_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.ConversationType
import ee.carlrobert.codegpt.completions.TotalUsageExceededException
import ee.carlrobert.codegpt.conversations.ConversationsState
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.persona.PersonaSettings
import ee.carlrobert.codegpt.settings.service.google.GoogleSettings
import ee.carlrobert.codegpt.util.file.FileUtil
import ee.carlrobert.llm.client.google.completion.GoogleCompletionContent
import ee.carlrobert.llm.client.google.completion.GoogleCompletionRequest
import ee.carlrobert.llm.client.google.completion.GoogleContentPart
import ee.carlrobert.llm.client.google.completion.GoogleGenerationConfig
import ee.carlrobert.llm.client.google.models.GoogleModel
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class GoogleRequestFactory : BaseRequestFactory() {

    override fun createChatRequest(callParameters: CallParameters): GoogleCompletionRequest {
        val configuration = service<ConfigurationSettings>().state
        val messages = buildGoogleMessages(service<GoogleSettings>().state.model, callParameters)
        return GoogleCompletionRequest.Builder(messages)
            .generationConfig(
                GoogleGenerationConfig.Builder()
                    .maxOutputTokens(configuration.maxTokens)
                    .temperature(configuration.temperature.toDouble()).build()
            )
            .build()
    }

    override fun createBasicCompletionRequest(
        systemPrompt: String,
        userPrompt: String,
        stream: Boolean
    ): GoogleCompletionRequest {
        val configuration = service<ConfigurationSettings>().state
        return GoogleCompletionRequest.Builder(
            listOf(
                GoogleCompletionContent("user", listOf(systemPrompt)),
                GoogleCompletionContent("model", listOf("Understood.")),
                GoogleCompletionContent("user", listOf(userPrompt))
            )
        )
            .generationConfig(
                GoogleGenerationConfig.Builder()
                    .maxOutputTokens(configuration.maxTokens)
                    .temperature(configuration.temperature.toDouble()).build()
            )
            .build()
    }

    private fun buildGoogleMessages(
        model: String?,
        callParameters: CallParameters
    ): List<GoogleCompletionContent> {
        val messages = buildGoogleMessages(callParameters)

        if (model == null) {
            return messages
        }

        val encodingManager = service<EncodingManager>()
        val totalUsage = messages.parallelStream()
            .mapToInt { message ->
                encodingManager.countMessageTokens(
                    message.role,
                    message.parts.joinToString(",") { it.text ?: "" }
                )
            }
            .sum() + service<ConfigurationSettings>().state.maxTokens

        return GoogleModel.findByCode(model)?.let { googleModel ->
            if (totalUsage <= googleModel.maxTokens) {
                messages
            } else {
                tryReducingGoogleMessagesOrThrow(
                    messages,
                    callParameters.conversation.isDiscardTokenLimit,
                    totalUsage,
                    googleModel.maxTokens
                )
            }
        } ?: messages
    }

    private fun buildGoogleMessages(callParameters: CallParameters): List<GoogleCompletionContent> {
        val message = callParameters.message
        val messages = mutableListOf<GoogleCompletionContent>()

        when (callParameters.conversationType) {
            ConversationType.DEFAULT -> {
                messages.add(
                    GoogleCompletionContent(
                        "user",
                        listOf(PersonaSettings.getSystemPrompt())
                    )
                )
                messages.add(GoogleCompletionContent("model", listOf("Understood.")))
            }

            ConversationType.FIX_COMPILE_ERRORS -> {
                messages.add(
                    GoogleCompletionContent("user", listOf(FIX_COMPILE_ERRORS_SYSTEM_PROMPT))
                )
                messages.add(GoogleCompletionContent("model", listOf("Understood.")))
            }

            else -> {}
        }

        for (prevMessage in callParameters.conversation.messages) {
            if (callParameters.isRetry && prevMessage.id == message.id) {
                break
            }

            prevMessage.imageFilePath?.takeIf { it.isNotEmpty() }?.let { imagePath ->
                try {
                    val imageData = Files.readAllBytes(Path.of(imagePath))
                    val imageMediaType =
                        FileUtil.getImageMediaType(Path.of(imagePath).fileName.toString())
                    messages.add(
                        GoogleCompletionContent(
                            listOf(
                                GoogleContentPart(
                                    null,
                                    GoogleContentPart.Blob(imageMediaType, imageData)
                                ),
                                GoogleContentPart(prevMessage.prompt)
                            ), "user"
                        )
                    )
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            } ?: messages.add(GoogleCompletionContent("user", listOf(prevMessage.prompt)))

            messages.add(GoogleCompletionContent("model", listOf(prevMessage.response)))
        }

        if (callParameters.imageMediaType != null && callParameters.imageData.isNotEmpty()) {
            messages.add(
                GoogleCompletionContent(
                    listOf(
                        GoogleContentPart(
                            null,
                            GoogleContentPart.Blob(
                                callParameters.imageMediaType,
                                callParameters.imageData
                            )
                        ),
                        GoogleContentPart(message.prompt)
                    ), "user"
                )
            )
        } else {
            messages.add(GoogleCompletionContent("user", listOf(message.prompt)))
        }

        return messages
    }

    private fun tryReducingGoogleMessagesOrThrow(
        messages: List<GoogleCompletionContent>,
        discardTokenLimit: Boolean,
        totalUsage: Int,
        modelMaxTokens: Int
    ): List<GoogleCompletionContent> {
        if (!service<ConversationsState>().state!!.discardAllTokenLimits) {
            if (!discardTokenLimit) {
                throw TotalUsageExceededException()
            }
        }

        val encodingManager = EncodingManager.getInstance()
        var currentUsage = totalUsage

        // skip the system prompt
        val updatedMessages = messages.mapIndexed { index, message ->
            if (index == 0 || currentUsage <= modelMaxTokens) {
                message
            } else {
                currentUsage -= encodingManager.countMessageTokens(
                    message.role,
                    message.parts.joinToString(",") { it.text }
                )
                null
            }
        }

        return updatedMessages.filterNotNull()
    }
}
