package ee.carlrobert.codegpt.completions.factory

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.completions.*
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.EDIT_CODE_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.FIX_COMPILE_ERRORS_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.GENERATE_METHOD_NAMES_SYSTEM_PROMPT
import ee.carlrobert.codegpt.conversations.ConversationsState
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings.Companion.getState
import ee.carlrobert.codegpt.settings.persona.PersonaSettings.Companion.getSystemPrompt
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.util.file.FileUtil.getImageMediaType
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel
import ee.carlrobert.llm.client.openai.completion.request.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class OpenAIRequestFactory : CompletionRequestFactory {

    override fun createChatRequest(params: ChatCompletionRequestParameters): OpenAIChatCompletionRequest {
        val (callParameters) = params
        val model = service<OpenAISettings>().state.model
        val configuration = service<ConfigurationSettings>().state
        val requestBuilder: OpenAIChatCompletionRequest.Builder =
            OpenAIChatCompletionRequest.Builder(
                buildOpenAIMessages(model, callParameters, callParameters.referencedFiles)
            )
                .setModel(model)
        if ("o1-mini" == model || "o1-preview" == model) {
            requestBuilder
                .setMaxCompletionTokens(configuration.maxTokens)
                .setStream(false)
                .setMaxTokens(null)
                .setTemperature(null)
                .setPresencePenalty(null)
                .setFrequencyPenalty(null)
        } else {
            requestBuilder
                .setStream(true)
                .setMaxTokens(configuration.maxTokens)
                .setTemperature(configuration.temperature.toDouble())
        }

        return requestBuilder.build()
    }

    override fun createEditCodeRequest(params: EditCodeRequestParameters): OpenAIChatCompletionRequest {
        val model = service<OpenAISettings>().state.model
        val prompt = "Code to modify:\n${params.selectedText}\n\nInstructions: ${params.prompt}"
        if (model == "o1-mini" || model == "o1-preview") {
            return buildBasicO1Request(model, prompt, EDIT_CODE_SYSTEM_PROMPT)
        }
        return createBasicCompletionRequest(EDIT_CODE_SYSTEM_PROMPT, prompt, model, true)
    }

    override fun createCommitMessageRequest(params: CommitMessageRequestParameters): OpenAIChatCompletionRequest {
        val model = service<OpenAISettings>().state.model
        val (gitDiff, systemPrompt) = params
        if (model == "o1-mini" || model == "o1-preview") {
            return buildBasicO1Request(model, gitDiff, systemPrompt)
        }
        return createBasicCompletionRequest(systemPrompt, gitDiff, model, true)
    }

    override fun createLookupRequest(params: LookupRequestCallParameters): OpenAIChatCompletionRequest {
        val model = service<OpenAISettings>().state.model
        val (prompt) = params
        if (model == "o1-mini" || model == "o1-preview") {
            return buildBasicO1Request(model, prompt, GENERATE_METHOD_NAMES_SYSTEM_PROMPT)
        }
        return createBasicCompletionRequest(GENERATE_METHOD_NAMES_SYSTEM_PROMPT, prompt, model)
    }

    companion object {
        fun buildBasicO1Request(
            model: String,
            prompt: String,
            systemPrompt: String = "",
            maxCompletionTokens: Int = 4096
        ): OpenAIChatCompletionRequest {
            val messages = if (systemPrompt.isEmpty()) {
                listOf(OpenAIChatCompletionStandardMessage("user", prompt))
            } else {
                listOf(
                    OpenAIChatCompletionStandardMessage("user", systemPrompt),
                    OpenAIChatCompletionStandardMessage("user", prompt)
                )
            }
            return OpenAIChatCompletionRequest.Builder(messages)
                .setModel(model)
                .setMaxCompletionTokens(maxCompletionTokens)
                .setStream(false)
                .setTemperature(null)
                .setFrequencyPenalty(null)
                .setPresencePenalty(null)
                .setMaxTokens(null)
                .build()
        }

        fun buildOpenAIMessages(
            model: String?,
            callParameters: CallParameters,
            referencedFiles: List<ReferencedFile>? = mutableListOf()
        ): List<OpenAIChatCompletionMessage> {
            val messages = buildOpenAIChatMessages(model, callParameters, referencedFiles)

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

        private fun buildOpenAIChatMessages(
            model: String?,
            callParameters: CallParameters,
            referencedFiles: List<ReferencedFile>? = mutableListOf()
        ): MutableList<OpenAIChatCompletionMessage> {
            val message = callParameters.message
            val messages = mutableListOf<OpenAIChatCompletionMessage>()
            val role = if ("o1-mini" == model || "o1-preview" == model) "user" else "system"

            if (callParameters.conversationType == ConversationType.DEFAULT) {
                val sessionPersonaDetails = callParameters.message.personaDetails
                if (callParameters.message.personaDetails == null) {
                    messages.add(
                        OpenAIChatCompletionStandardMessage(role, getSystemPrompt())
                    )
                } else {
                    messages.add(
                        OpenAIChatCompletionStandardMessage(
                            role,
                            sessionPersonaDetails.instructions
                        )
                    )
                }
            }
            if (callParameters.conversationType == ConversationType.FIX_COMPILE_ERRORS) {
                messages.add(
                    OpenAIChatCompletionStandardMessage(role, FIX_COMPILE_ERRORS_SYSTEM_PROMPT)
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
                val prompt = if (referencedFiles.isNullOrEmpty()) {
                    message.prompt
                } else {
                    CompletionRequestUtil.getPromptWithContext(referencedFiles, message.prompt)
                }
                messages.add(OpenAIChatCompletionStandardMessage("user", prompt))
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
