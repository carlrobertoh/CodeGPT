package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import ee.carlrobert.codegpt.CodeGPTKeys.IS_FETCHING_COMPLETION
import ee.carlrobert.codegpt.CodeGPTKeys.REMAINING_EDITOR_COMPLETION
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.util.StringUtil.extractUntilNewline
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import okhttp3.sse.EventSource
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DebouncedCodeCompletionProvider : DebouncedInlineCompletionProvider() {

    companion object {
        private val logger = thisLogger()
    }

    private val currentCallRef = AtomicReference<EventSource?>(null)

    override val id: InlineCompletionProviderID
        get() = InlineCompletionProviderID("CodeGPTInlineCompletionProvider")

    override val insertHandler: InlineCompletionInsertHandler
        get() = CodeCompletionInsertHandler()

    override val providerPresentation: InlineCompletionProviderPresentation
        get() = CodeCompletionProviderPresentation()

    override suspend fun getSuggestionDebounced(request: InlineCompletionRequest): InlineCompletionSuggestion {
        val editor = request.editor
        val remainingCompletion = REMAINING_EDITOR_COMPLETION.get(editor)
        if (request.event is InlineCompletionEvent.DirectCall
            && remainingCompletion != null
            && remainingCompletion.isNotEmpty()
        ) {
            return sendNextSuggestion(remainingCompletion.extractUntilNewline(), request)
        }

        val project = editor.project
        if (project == null) {
            logger.error("Could not find project")
            return InlineCompletionSuggestion.Default(emptyFlow())
        }

        if (LookupManager.getActiveLookup(editor) != null) {
            return InlineCompletionSuggestion.Default(emptyFlow())
        }

        return InlineCompletionSuggestion.Default(channelFlow {
            REMAINING_EDITOR_COMPLETION.set(request.editor, "")
            IS_FETCHING_COMPLETION.set(request.editor, true)

            request.editor.project?.messageBus
                ?.syncPublisher(CodeCompletionProgressNotifier.CODE_COMPLETION_PROGRESS_TOPIC)
                ?.loading(true)

            val infillRequest = InfillRequestUtil.buildInfillRequest(request)
            val call = project.service<CodeCompletionService>()
                .getCodeCompletionAsync(
                    infillRequest,
                    getEventListener(request.editor, infillRequest)
                )
            currentCallRef.set(call)
            awaitClose { currentCallRef.getAndSet(null)?.cancel() }
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

        if (!codeCompletionsEnabled) {
            return false
        }

        if (LookupManager.getActiveLookup(event.toRequest()?.editor) != null) {
            return false
        }

        val containsActiveCompletion =
            REMAINING_EDITOR_COMPLETION.get(event.toRequest()?.editor)?.isNotEmpty() ?: false

        return event is InlineCompletionEvent.DocumentChange || containsActiveCompletion
    }

    private fun ProducerScope<InlineCompletionElement>.getEventListener(
        editor: Editor,
        infillRequest: InfillRequest
    ) = object : CodeCompletionEventListener(editor) {

        override fun onLineReceived(completionLine: String) {
            runInEdt {
                var editorLineSuffix = editor.getLineSuffixAfterCaret()
                if (editorLineSuffix.isBlank()) {
                    trySend(
                        CodeCompletionTextElement(
                            completionLine,
                            infillRequest.caretOffset,
                            TextRange.from(infillRequest.caretOffset, completionLine.length),
                        )
                    )
                } else {
                    var caretShift = 0

                    // TODO: Handle other scenarios
                    val processedCompletion =
                        if (completionLine.startsWith(editorLineSuffix.first())) {
                            caretShift++
                            editorLineSuffix = editorLineSuffix.substring(1)
                            completionLine.substring(1)
                        } else {
                            completionLine
                        }

                    val completionWithRemovedSuffix =
                        processedCompletion.removeSuffix(editorLineSuffix)

                    trySend(
                        CodeCompletionTextElement(
                            completionWithRemovedSuffix,
                            infillRequest.caretOffset + caretShift,
                            TextRange.from(
                                infillRequest.caretOffset + caretShift,
                                completionWithRemovedSuffix.length
                            ),
                            caretShift,
                            completionLine
                        )
                    )
                }
            }
        }
    }

    private fun Editor.getLineSuffixAfterCaret(): String {
        val lineEndOffset = document.getLineEndOffset(document.getLineNumber(caretModel.offset))
        return document.getText(
            TextRange(
                caretModel.offset,
                min(lineEndOffset + 1, document.textLength)
            )
        )
    }

    private fun sendNextSuggestion(
        nextCompletion: String,
        request: InlineCompletionRequest
    ): InlineCompletionSuggestion {

        return InlineCompletionSuggestion.Default(channelFlow {
            launch {
                trySend(
                    CodeCompletionTextElement(
                        nextCompletion,
                        request.startOffset,
                        TextRange.from(request.startOffset, nextCompletion.length),
                    )
                )
            }
        })
    }
}
