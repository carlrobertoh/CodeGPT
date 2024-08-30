package ee.carlrobert.codegpt.codecompletions

import ai.grazie.nlp.utils.takeLastWhitespaces
import ai.grazie.nlp.utils.takeWhitespaces
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import ee.carlrobert.codegpt.CodeGPTKeys.IS_FETCHING_COMPLETION
import ee.carlrobert.codegpt.ui.OverlayUtil.showNotification
import ee.carlrobert.llm.client.openai.completion.ErrorDetails
import ee.carlrobert.llm.completion.CompletionEventListener
import okhttp3.sse.EventSource
import java.util.concurrent.atomic.AtomicBoolean

private const val MAX_LINES_TO_REQUEST = 4
private const val MAX_LINES_TO_DISPLAY = 2

abstract class CodeCompletionCompletionEventListener(
    private val editor: Editor,
    private val infillRequest: InfillRequest? = null,
) : CompletionEventListener<String> {

    companion object {
        private val logger = thisLogger()
    }

    private val stringBuilder = StringBuilder()
    private val isCancelled = AtomicBoolean(false)
    private val isSending = AtomicBoolean(true)

    open fun onComplete(fullMessage: String) {}
    open fun onMessage(message: String) {}

    override fun onOpen() {
        setLoading(true)
    }

    override fun onMessage(message: String, eventSource: EventSource) {
        if (isCancelled.get()) return

        val processedMessage = if (infillRequest != null && stringBuilder.isEmpty()) {
            message.tryTrimStart(infillRequest.prefix.lines())
        } else {
            message
        }

        val newLineCount = (stringBuilder.toString() + processedMessage).count { it == '\n' }
        if (newLineCount >= MAX_LINES_TO_REQUEST) {
            cancelStreaming(processedMessage, eventSource)
            return
        }

        stringBuilder.append(processedMessage)

        if (newLineCount <= MAX_LINES_TO_DISPLAY && isSending.get()) {
            if (newLineCount == MAX_LINES_TO_DISPLAY && processedMessage.contains('\n')) {
                isSending.set(false)
                onMessage(processedMessage.substring(0, processedMessage.lastIndexOf('\n')))
            } else {
                onMessage(processedMessage)
            }
        }
    }

    override fun onComplete(messageBuilder: StringBuilder) {
        setLoading(false)
        onComplete(stringBuilder.trimEnd().toString())
    }

    override fun onCancelled(messageBuilder: StringBuilder) {
        setLoading(false)
        onComplete(stringBuilder.trimEnd().toString())
    }

    override fun onError(error: ErrorDetails, ex: Throwable) {
        if (ex.message == null || (ex.message != null && ex.message != "Canceled")) {
            showNotification(error.message, NotificationType.ERROR)
            logger.error(error.message, ex)
        }
    }

    private fun setLoading(loading: Boolean) {
        IS_FETCHING_COMPLETION.set(editor, loading)
        editor.project?.messageBus
            ?.syncPublisher(CodeCompletionProgressNotifier.CODE_COMPLETION_PROGRESS_TOPIC)
            ?.loading(loading)
    }

    private fun cancelStreaming(processedMessage: String, eventSource: EventSource) {
        stringBuilder.append(processedMessage.substring(0, processedMessage.lastIndexOf('\n')))
        isCancelled.set(true)
        isSending.set(false)
        eventSource.cancel()
    }

    private fun String.tryTrimStart(lines: List<String>): String {
        val whiteSpaces = this.takeWhitespaces()
        if (lines.size >= 2
            && whiteSpaces.isNotEmpty()
            && lines[lines.size - 1].trim().isEmpty()
        ) {
            return this.trimStart()
        }

        if (lines.isNotEmpty()) {
            val lastLine = lines[lines.size - 1]
            if (lastLine.takeLastWhitespaces().isNotEmpty()) {
                return this.trimStart()
            }
        }
        return this
    }
}