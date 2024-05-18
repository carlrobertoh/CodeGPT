package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager

@Service(Service.Level.PROJECT)
class CompletionContextService {

    companion object {
        private val CONTEXT_FINDERS = mapOf(
            "JAVA" to JavaContextFinder::class.java,
            "Python" to PythonContextFinder::class.java
        )
    }

    /**
     * Determines the [com.intellij.psi.PsiElement] at the given offset,
     * determines relevant context with the help of [LanguageContextFinder]s
     * and returns the context as a set of source code [VirtualFile]s.
     */
    fun findContextFiles(editor: Editor, offset: Int): Set<VirtualFile> {
        val psiFile = PsiManager.getInstance(editor.project!!).findFile(editor.virtualFile!!)!!
        val psiElement = psiFile.findElementAt(offset)
        if (psiElement == null) {
            return setOf();
        }
        val psiParserClass = CONTEXT_FINDERS[psiElement.language.id]
            ?: // No context finder for the language implemented yet
            return setOf()
        val psiParser = ApplicationManager.getApplication().getService(psiParserClass)
            ?: // A context finder for the language exists but not available in the used IDE
            return setOf()
        return psiParser.findContextSourceFiles(psiElement, editor).minus(editor.virtualFile)

    }
}
