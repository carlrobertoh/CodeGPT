package ee.carlrobert.codegpt.codecompletions

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.codecompletions.psi.filePath
import ee.carlrobert.codegpt.codecompletions.psi.readText

const val MAX_PROMPT_TOKENS = 128

class InfillRequest private constructor(
    val prefix: String,
    val suffix: String,
    val fileDetails: FileDetails?,
    val vcsDetails: VcsDetails?,
    val context: InfillContext?
) {

    companion object {
        fun builder(prefix: String, suffix: String) = Builder(prefix, suffix)
    }

    data class VcsDetails(val stagedDiff: String? = null, val unstagedDiff: String? = null)
    data class FileDetails(val fileContent: String, val fileExtension: String? = null)

    class Builder {
        private val prefix: String
        private val suffix: String
        private var fileDetails: FileDetails? = null
        private var vcsDetails: VcsDetails? = null
        private var context: InfillContext? = null

        constructor(prefix: String, suffix: String) {
            this.prefix = prefix
            this.suffix = suffix
        }

        constructor(document: Document, caretOffset: Int) {
            prefix =
                document.getText(TextRange(0, caretOffset))
                    .truncateText(MAX_PROMPT_TOKENS, false)
            suffix =
                document.getText(
                    TextRange(
                        caretOffset,
                        document.textLength
                    )
                ).truncateText(MAX_PROMPT_TOKENS)
        }

        fun fileDetails(fileDetails: FileDetails) = apply { this.fileDetails = fileDetails }
        fun vcsDetails(vcsDetails: VcsDetails) = apply { this.vcsDetails = vcsDetails }
        fun context(context: InfillContext) = apply { this.context = context }

        fun build() =
            InfillRequest(prefix, suffix, fileDetails, vcsDetails, context)
    }
}

class InfillContext(
    val enclosingElement: ContextElement,
    // TODO: Add some kind of ranking, which contextElements are more important than others
    val contextElements: Set<ContextElement>
) {

    fun getRepoName(): String = enclosingElement.psiElement.project.name
}

class ContextElement(val psiElement: PsiElement) {
    var tokens: Int = -1

    fun filePath() = this.psiElement.filePath()
    fun text() = this.psiElement.readText()
}

fun String.truncateText(maxTokens: Int, fromStart: Boolean = true): String {
    return service<EncodingManager>().truncateText(this, maxTokens, fromStart)
}