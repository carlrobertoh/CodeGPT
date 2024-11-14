package ee.carlrobert.codegpt.codecompletions

import com.intellij.notification.NotificationType
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import ee.carlrobert.codegpt.CodeGPTKeys.IS_FETCHING_COMPLETION
import ee.carlrobert.codegpt.CodeGPTKeys.REMAINING_EDITOR_COMPLETION
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.ui.OverlayUtil.showNotification
import ee.carlrobert.codegpt.util.StringUtil
import ee.carlrobert.llm.client.openai.completion.ErrorDetails
import ee.carlrobert.llm.completion.CompletionEventListener
import okhttp3.sse.EventSource

abstract class CodeCompletionEventListener(
    private val editor: Editor
) : CompletionEventListener<String> {

    companion object {
        private val logger = thisLogger()
    }

    private var isFirstLine = true
    private val currentLineBuffer = StringBuilder()
    private val incomingTextBuffer = StringBuilder()

    open fun onLineReceived(completionLine: String) {}

    override fun onOpen() {
        setLoading(true)
        REMAINING_EDITOR_COMPLETION.set(editor, "")
    }

    override fun onMessage(message: String, eventSource: EventSource) {
        incomingTextBuffer.append(message)

        while (incomingTextBuffer.contains("\n")) {
            val lineEndIndex = incomingTextBuffer.indexOf("\n")
            val line = incomingTextBuffer.substring(0, lineEndIndex) + '\n'
            processCompletionLine(line)
            incomingTextBuffer.delete(0, lineEndIndex + 1)
        }
    }

    private fun processCompletionLine(line: String) {
        currentLineBuffer.append(line)

        if (currentLineBuffer.trim().isNotEmpty()) {
            val completionText = if (isFirstLine) {
                line.adjustWhitespaces().also {
                    isFirstLine = false
                    onLineReceived(it)
                }
            } else {
                currentLineBuffer.toString()
            }

            appendRemainingCompletion(completionText)
            currentLineBuffer.clear()
        }
    }

    override fun onComplete(messageBuilder: StringBuilder) {
        handleCompleted(messageBuilder)
    }

    override fun onCancelled(messageBuilder: StringBuilder) {
        handleCompleted(messageBuilder)
    }

    override fun onError(error: ErrorDetails, ex: Throwable) {
        val isCodeGPTService = GeneralSettings.getSelectedService() == ServiceType.CODEGPT
        if (isCodeGPTService && "RATE_LIMIT_EXCEEDED" == error.code) {
            service<CodeGPTServiceSettings>().state
                .codeCompletionSettings
                .codeCompletionsEnabled = false
        }

        if (ex.message == null || (ex.message != null && ex.message != "Canceled")) {
            showNotification(error.message, NotificationType.ERROR)
            logger.error(error.message, ex)
        }
        setLoading(false)
    }

    private fun String.adjustWhitespaces(): String {
        val adjustedLine = runReadAction {
            val lineNumber = editor.document.getLineNumber(editor.caretModel.offset)
            val editorLine = editor.document.getText(
                TextRange(
                    editor.document.getLineStartOffset(lineNumber),
                    editor.document.getLineEndOffset(lineNumber)
                )
            )

            StringUtil.adjustWhitespace(this, editorLine)
        }

        return if (adjustedLine.length != this.length) adjustedLine else this
    }

    private fun handleCompleted(messageBuilder: StringBuilder) {
        setLoading(false)

        if (incomingTextBuffer.isNotEmpty()) {
            appendRemainingCompletion(incomingTextBuffer.toString())
        }

        if (isFirstLine) {
            val completionLine = messageBuilder.toString().adjustWhitespaces()
            REMAINING_EDITOR_COMPLETION.set(editor, completionLine)
            onLineReceived(completionLine)
        }
    }

    private fun appendRemainingCompletion(text: String) {
        val previousRemainingText = REMAINING_EDITOR_COMPLETION.get(editor) ?: ""
        REMAINING_EDITOR_COMPLETION.set(editor, previousRemainingText + text)
    }

    private fun setLoading(loading: Boolean) {
        IS_FETCHING_COMPLETION.set(editor, loading)
        editor.project?.messageBus
            ?.syncPublisher(CodeCompletionProgressNotifier.CODE_COMPLETION_PROGRESS_TOPIC)
            ?.loading(loading)
    }
}
