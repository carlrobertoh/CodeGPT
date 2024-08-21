package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.TextRange
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.codecompletions.psi.CompletionContextService
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.treesitter.CodeCompletionParserFactory
import ee.carlrobert.codegpt.ui.OverlayUtil.showNotification
import ee.carlrobert.llm.client.openai.completion.ErrorDetails
import ee.carlrobert.llm.completion.CompletionEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.sse.EventSource
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CodeGPTInlineCompletionProvider : DebouncedInlineCompletionProvider() {
    companion object {
        private val logger = thisLogger()
    }

    private val currentCall = AtomicReference<EventSource>(null)

    override val id: InlineCompletionProviderID
        get() = InlineCompletionProviderID("CodeGPTInlineCompletionProvider")

    override suspend fun getSuggestionDebounced(request: InlineCompletionRequest): InlineCompletionSuggestion {
        val editor = request.editor
        val project = editor.project
        if (project == null) {
            logger.error("Could not find project")
            return InlineCompletionSuggestion.empty()
        }

        return InlineCompletionSuggestion.Default(channelFlow {
            val caretOffset = withContext(Dispatchers.EDT) { editor.caretModel.offset }
            val infillContext =
                if (service<ConfigurationSettings>().state.autocompletionContextAwareEnabled)
                    service<CompletionContextService>().findContext(editor, caretOffset)
                else null
            val infillRequest = if (infillContext == null) {
                val (prefix, suffix) = withContext(Dispatchers.EDT) {
                    val prefix =
                        request.document.getText(TextRange(0, caretOffset))
                    val suffix =
                        request.document.getText(
                            TextRange(
                                caretOffset,
                                request.document.textLength
                            )
                        )
                    Pair(prefix, suffix)
                }
                InfillRequestDetails.withoutContext(prefix, suffix)
            } else {
                // TODO: truncate contextElements if too long?
                InfillRequestDetails.withContext(
                    infillContext,
                    caretOffset
                )
            }

            currentCall.set(
                project.service<CodeCompletionService>().getCodeCompletionAsync(
                    infillRequest,
                    CodeCompletionEventListener {
                        val settings = service<ConfigurationSettings>().state
                        try {
                            var inlineText = it.toString()
                            if (settings.autocompletionPostProcessingEnabled) {
                                inlineText = CodeCompletionParserFactory
                                    .getParserForFileExtension(request.file.virtualFile.extension)
                                    .parse(
                                        // TODO: ?
                                        infillRequest.prefix,
                                        infillRequest.suffix,
                                        inlineText
                                    )
                            }

                            editor.putUserData(CodeGPTKeys.PREVIOUS_INLAY_TEXT, inlineText)
                            launch {
                                try {
                                    trySend(InlineCompletionGrayTextElement(inlineText))
                                } catch (e: Exception) {
                                    logger.error("Failed to send inline completion suggestion", e)
                                }
                            }
                        } catch (t: Throwable) {
                            logger.error(t)
                            settings.autocompletionPostProcessingEnabled = false
                        }
                    }
                )
            )
            awaitClose { cancelCurrentCall() }
        })
    }

    override suspend fun getDebounceDelay(request: InlineCompletionRequest): Duration {
        return 600.toDuration(DurationUnit.MILLISECONDS)
    }

    override fun isEnabled(event: InlineCompletionEvent): Boolean {
        val selectedService = GeneralSettings.getSelectedService()
        val codeCompletionsEnabled = when (selectedService) {
            ServiceType.CODEGPT -> service<CodeGPTServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled
            ServiceType.OPENAI -> OpenAISettings.getCurrentState().isCodeCompletionsEnabled
            ServiceType.CUSTOM_OPENAI -> service<CustomServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled
            ServiceType.LLAMA_CPP -> LlamaSettings.isCodeCompletionsPossible()
            ServiceType.OLLAMA -> service<OllamaSettings>().state.codeCompletionsEnabled
            ServiceType.ANTHROPIC,
            ServiceType.AZURE,
            ServiceType.GOOGLE,
            null -> false
        }
        return event is InlineCompletionEvent.DocumentChange && codeCompletionsEnabled
    }

    private fun cancelCurrentCall() {
        currentCall.getAndSet(null)?.cancel()
    }

    class CodeCompletionEventListener(
        private val completed: (StringBuilder) -> Unit
    ) : CompletionEventListener<String> {

        override fun onComplete(messageBuilder: StringBuilder) {
            completed(messageBuilder)
        }

        override fun onCancelled(messageBuilder: StringBuilder) {
            completed(messageBuilder)
        }

        override fun onError(error: ErrorDetails, ex: Throwable) {
            if (ex.message == null || (ex.message != null && ex.message != "Canceled")) {
                showNotification(error.message, NotificationType.ERROR)
                logger.error(error.message, ex)
            }
        }
    }
}