package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.navigation.ImplementationSearcher
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.codecompletions.psi.CompletionContextService


class InfillRequestDetails(val prefix: String, val suffix: String, val context: InfillContext?) :
    ImplementationSearcher() {
    companion object {
        private const val MAX_OFFSET = 10_000
        private const val MAX_PROMPT_TOKENS = 128

        fun fromInlineRequestDetails(
            editor: Editor,
            caretOffset: Int,
            prefix: String,
            suffix: String
        ): InfillRequestDetails {
            val truncatedPrefix = prefix.takeLast(MAX_OFFSET)
            val truncatedSuffix = suffix.take(MAX_OFFSET)
            val contextFiles =
                service<CompletionContextService>().findContextFiles(editor, caretOffset)
            return InfillRequestDetails(
                truncateText(truncatedPrefix, false),
                truncateText(truncatedSuffix, true),
                InfillContext(editor.project!!.name, editor.virtualFile, contextFiles)
            )
        }

        private fun truncateText(
            text: String,
            fromStart: Boolean
        ): String {
            return EncodingManager.getInstance().truncateText(
                text,
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