package ee.carlrobert.codegpt.codecompletions

import com.intellij.psi.PsiElement
import ee.carlrobert.codegpt.codecompletions.psi.filePath
import ee.carlrobert.codegpt.codecompletions.psi.readText

class InfillRequest private constructor(
    val prefix: String,
    val suffix: String,
    val fileDetails: FileDetails?,
    val vcsDetails: VcsDetails?,
    val context: InfillContext?
) {

    data class VcsDetails(val stagedDiff: String? = null, val unstagedDiff: String? = null)
    data class FileDetails(val fileContent: String, val fileExtension: String? = null)

    class Builder(private val prefix: String, private val suffix: String) {
        private var fileDetails: FileDetails? = null
        private var vcsDetails: VcsDetails? = null
        private var context: InfillContext? = null

        fun fileDetails(fileDetails: FileDetails) = apply { this.fileDetails = fileDetails }
        fun vcsDetails(vcsDetails: VcsDetails) = apply { this.vcsDetails = vcsDetails }
        fun context(context: InfillContext) = apply { this.context = context }

        fun build() =
            InfillRequest(prefix, suffix, fileDetails, vcsDetails, context)
    }

    companion object {
        fun builder(prefix: String, suffix: String) = Builder(prefix, suffix)
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
