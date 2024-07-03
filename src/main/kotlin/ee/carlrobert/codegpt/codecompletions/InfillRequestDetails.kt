package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.navigation.ImplementationSearcher
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.startOffset
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.codecompletions.psi.filePath
import ee.carlrobert.codegpt.codecompletions.psi.readText


class InfillRequestDetails(val prefix: String, val suffix: String, val context: InfillContext?) :
    ImplementationSearcher() {
    companion object {
        private const val MAX_OFFSET = 10_000
        private const val MAX_PROMPT_TOKENS = 128
        private const val MAX_INFILL_PROMPT_TOKENS = 1_000

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
                caretOffsetInFile - infillContext.enclosingElement.psiElement.startOffset
            val entireText = infillContext.enclosingElement.psiElement.readText()
            val prefix = truncateText(entireText.take(caretInEnclosingElement), false)
            val suffix = truncateText(
                if (entireText.length < caretInEnclosingElement) "" else entireText.takeLast(
                    entireText.length - caretInEnclosingElement
                ), true
            )
            return InfillRequestDetails(
                prefix,
                suffix,
                truncateContext(prefix + suffix, infillContext)
            )
        }

        private fun truncateContext(prompt: String, infillContext: InfillContext): InfillContext {
            var promptTokens = EncodingManager.getInstance().countTokens(prompt)
            val truncatedContextElements = infillContext.contextElements.takeWhile {
                promptTokens += it.tokens
                promptTokens <= MAX_INFILL_PROMPT_TOKENS
            }.toSet()
            return InfillContext(infillContext.enclosingElement, truncatedContextElements)
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
    val enclosingElement: ContextElement,
    // TODO: Add some kind of ranking, which contextElements are more important than others
    val contextElements: Set<ContextElement>
) {

    fun getRepoName(): String = enclosingElement.psiElement.project.name
}


class ContextElement {
    val psiElement: PsiElement
    var tokens: Int

    constructor(psiElement: PsiElement) {
        this.psiElement = psiElement
        this.tokens = -1
    }

    fun filePath() = this.psiElement.filePath()
    fun text() = this.psiElement.readText()
}
