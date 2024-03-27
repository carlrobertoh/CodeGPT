package ee.carlrobert.codegpt.codecompletions

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import ee.carlrobert.codegpt.EncodingManager
import kotlin.math.max
import kotlin.math.min

class InfillRequestDetails(val prefix: String, val suffix: String) {
    companion object {
        private const val MAX_OFFSET = 10_000
        private const val MAX_PROMPT_TOKENS = 128

        fun fromCurrentEditorCaret(editor: Editor): InfillRequestDetails {
            return fromDocumentWithMaxOffset(editor.document, editor.caretModel.offset)
        }

        fun fromDocumentWithMaxOffset(
            document: Document,
            caretOffset: Int
        ): InfillRequestDetails {
            val start = max(0, (caretOffset - MAX_OFFSET))
            val end = min(document.textLength, (caretOffset + MAX_OFFSET))
            return fromDocumentWithCustomRange(document, caretOffset, start, end)
        }

        private fun fromDocumentWithCustomRange(
            document: Document,
            caretOffset: Int,
            start: Int,
            end: Int
        ): InfillRequestDetails {
            val prefix: String = truncateText(document, start, caretOffset, false)
            val suffix: String = truncateText(document, caretOffset, end, true)
            return InfillRequestDetails(prefix, suffix)
        }

        private fun truncateText(
            document: Document,
            start: Int,
            end: Int,
            fromStart: Boolean
        ): String {
            return EncodingManager.getInstance().truncateText(
                document.getText(TextRange(start, end)),
                MAX_PROMPT_TOKENS,
                fromStart
            )
        }
    }
}