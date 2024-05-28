package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.navigation.ImplementationSearcher
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.startOffset
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.codecompletions.psi.readText


class InfillRequestDetails(val prefix: String, val suffix: String, val context: InfillContext?) :
    ImplementationSearcher() {
    companion object {
        private const val MAX_OFFSET = 10_000
        private const val MAX_PROMPT_TOKENS = 128

        fun withoutContext(
            prefix: String,
            suffix: String
        ): InfillRequestDetails {
            val truncatedPrefix = prefix.takeLast(MAX_OFFSET)
            val truncatedSuffix = suffix.take(MAX_OFFSET)
            return InfillRequestDetails(
                truncateText(truncatedPrefix, false),
                truncateText(truncatedSuffix, true),
                null
            )
        }

        fun withContext(
            infillContext: InfillContext,
            caretOffsetInFile: Int,
        ): InfillRequestDetails {
            val caretInEnclosingElement =
                caretOffsetInFile - infillContext.enclosingElement.startOffset
            val entireText = infillContext.enclosingElement.readText()
            val prefix = entireText.take(caretInEnclosingElement)
            val suffix =
                if (entireText.length < caretInEnclosingElement) "" else entireText.takeLast(
                    entireText.length - caretInEnclosingElement
                )
            // TODO: truncate if too long
            return InfillRequestDetails(
                truncateText(prefix, false),
                truncateText(suffix, true),
                infillContext
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
    val enclosingElement: PsiElement,
    val contextElements: Set<PsiElement>
) {

    fun getRepoName(): String = enclosingElement.project.name
}
