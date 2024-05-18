package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

interface LanguageContextFinder {
    /**
     * Determines [PsiElement]s relevant to the context of [psiElement] and returns their source code files.
     */
    fun findContextSourceFiles(psiElement: PsiElement, editor: Editor): Set<VirtualFile>
}