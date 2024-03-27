package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.util.file.FileUtil
import kotlin.math.max
import kotlin.math.min

class InfillRequestDetails(val prefix: String, val suffix: String, val fileExtension: String) {
    companion object {
        private const val MAX_OFFSET = 10_000
        private const val MAX_PROMPT_TOKENS = 128

        fun fromInlineCompletionRequest(request: InlineCompletionRequest): InfillRequestDetails {
            return fromDocumentWithMaxOffset(
                request.editor.document,
                request.editor.caretModel.offset,
                FileUtil.getFileExtension(request.file.name)
            )
        }

        private fun fromDocumentWithMaxOffset(
            document: Document,
            caretOffset: Int,
            fileExtension: String
        ): InfillRequestDetails {
            val start = max(0, (caretOffset - MAX_OFFSET))
            val end = min(document.textLength, (caretOffset + MAX_OFFSET))
            return fromDocumentWithCustomRange(document, caretOffset, start, end, fileExtension)
        }

        private fun fromDocumentWithCustomRange(
            document: Document,
            caretOffset: Int,
            start: Int,
            end: Int,
            fileExtension: String
        ): InfillRequestDetails {
            val prefix: String = truncateText(document, start, caretOffset, false)
            val suffix: String = truncateText(document, caretOffset, end, true)
            return InfillRequestDetails(prefix, suffix, fileExtension)
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