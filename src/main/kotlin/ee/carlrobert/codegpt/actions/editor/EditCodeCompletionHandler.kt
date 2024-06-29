package ee.carlrobert.codegpt.actions.editor

import com.intellij.notification.NotificationType
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.*
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.ui.JBColor
import ee.carlrobert.codegpt.ui.ObservableProperties
import ee.carlrobert.codegpt.ui.OverlayUtil
import ee.carlrobert.llm.client.openai.completion.ErrorDetails
import ee.carlrobert.llm.completion.CompletionEventListener
import okhttp3.sse.EventSource

class EditCodeCompletionHandler(
    private val editor: Editor,
    private val observableProperties: ObservableProperties,
    private val selectionTextRange: TextRange
) : CompletionEventListener<String> {

    private var replacedLength = 0
    private var currentHighlighter: RangeHighlighter? = null

    override fun onMessage(message: String, eventSource: EventSource) {
        handleDiff(message)
    }

    override fun onComplete(messageBuilder: StringBuilder) {
        cleanupAndFormat()
        observableProperties.loading.set(false)
    }

    override fun onError(error: ErrorDetails, ex: Throwable) {
        OverlayUtil.showNotification(
            "Something went wrong while requesting completion. Please try again.",
            NotificationType.ERROR
        )
    }

    private fun highlightCurrentRow(editor: Editor) {
        currentHighlighter?.let { editor.markupModel.removeHighlighter(it) }

        val document = editor.document
        val lineNumber = document.getLineNumber(editor.caretModel.offset)
        currentHighlighter = editor.markupModel.addRangeHighlighter(
            document.getLineStartOffset(lineNumber),
            document.getLineEndOffset(lineNumber),
            HighlighterLayer.SELECTION - 1,
            TextAttributes().apply {
                effectType = EffectType.BOXED
                effectColor =
                    JBColor.namedColor("PsiViewer.referenceHighlightColor", 0x6A7B15)
                errorStripeColor = effectColor
            },
            HighlighterTargetArea.EXACT_RANGE
        )
    }


    private fun handleDiff(message: String) {
        runWriteCommandAction(editor.project) {
            val document = editor.document
            val startOffset = selectionTextRange.startOffset
            val endOffset = selectionTextRange.endOffset

            val remainingOriginalLength = endOffset - (startOffset + replacedLength)
            if (remainingOriginalLength > 0) {
                document.replaceString(
                    startOffset + replacedLength,
                    startOffset + replacedLength + minOf(
                        message.length,
                        remainingOriginalLength
                    ),
                    message
                )
            } else {
                document.insertString(startOffset + replacedLength, message)
            }

            replacedLength += message.length
            editor.caretModel.moveToOffset(startOffset + replacedLength)
            highlightCurrentRow(editor)
        }
    }

    private fun cleanupHighlighter() {
        currentHighlighter?.let { editor.markupModel.removeHighlighter(it) }
        currentHighlighter = null
    }

    private fun cleanupAndFormat() {
        val project = editor.project ?: return
        runWriteCommandAction(project) {
            val document = editor.document
            val psiDocumentManager = project.service<PsiDocumentManager>()
            val psiFile = psiDocumentManager.getPsiFile(document)
                ?: return@runWriteCommandAction
            val startOffset = selectionTextRange.startOffset
            val endOffset = selectionTextRange.endOffset
            val newEndOffset = startOffset + replacedLength

            if (newEndOffset < endOffset) {
                document.deleteString(newEndOffset, endOffset)
            }

            psiDocumentManager.commitDocument(document)
            project.service<CodeStyleManager>().reformatText(
                psiFile,
                listOf(TextRange(startOffset, newEndOffset))
            )
            editor.caretModel.moveToOffset(newEndOffset)
            psiDocumentManager.doPostponedOperationsAndUnblockDocument(document)
            cleanupHighlighter()
        }
    }
}