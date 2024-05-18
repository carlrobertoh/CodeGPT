package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.codeInsight.navigation.ImplementationSearcher
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.codecompletions.psi.CompletionContextService
import kotlin.math.max
import kotlin.math.min


class InfillRequestDetails(val prefix: String, val suffix: String, val context: InfillContext?) :
    ImplementationSearcher() {
    companion object {
        private const val MAX_OFFSET = 10_000
        private const val MAX_PROMPT_TOKENS = 128

        fun fromInlineCompletionRequest(request: InlineCompletionRequest): InfillRequestDetails {
            return fromDocumentWithMaxOffset(
                request.editor.document,
                request.editor.caretModel.offset,
                request.editor
            )
        }

        private fun fromDocumentWithMaxOffset(
            document: Document,
            caretOffset: Int,
            editor: Editor,
        ): InfillRequestDetails {
            val start = max(0, (caretOffset - MAX_OFFSET))
            val end = min(document.textLength, (caretOffset + MAX_OFFSET))
            return fromDocumentWithCustomRange(document, caretOffset, start, end, editor)
        }

        private fun fromDocumentWithCustomRange(
            document: Document,
            caretOffset: Int,
            start: Int,
            end: Int,
            editor: Editor,
        ): InfillRequestDetails {
            val prefix: String = truncateText(document, start, caretOffset, false)
            val suffix: String = truncateText(document, caretOffset, end, true)
            val contextFiles = service<CompletionContextService>().findContextFiles(editor, caretOffset)
            return InfillRequestDetails(prefix, suffix, InfillContext(editor.project!!.name, editor.virtualFile, contextFiles))
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

class InfillContext(
    val repoName: String,
    val file: VirtualFile,
    val contextFiles: Set<VirtualFile>?
)