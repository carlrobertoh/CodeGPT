package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.util.findParentOfType
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.PyReferenceExpression
import com.jetbrains.python.psi.resolve.PyResolveContext
import com.jetbrains.python.psi.resolve.PyResolveUtil
import com.jetbrains.python.psi.types.TypeEvalContext

class PythonContextFinder : LanguageContextFinder {

    /**
     * Finds enclosing [PyFunction] or [PyClass] of [psiElement] and
     * determines source code files of all used [PyReferenceExpression]s for the context.
     */
    override fun findContextSourceFiles(psiElement: PsiElement, editor: Editor): Set<VirtualFile> {
        var significantElement = psiElement
        // In Python whitespaces have semantic meaning
        // its enclosing PyFunction is not inevitably its parent node, but its previous sibling
        while (significantElement is PsiWhiteSpace) {
            significantElement = significantElement.prevSibling
        }
        if (significantElement !is PyFunction && significantElement !is PyClass) {
            significantElement = psiElement.findParentOfType<PyFunction>()
                ?: psiElement.findParentOfType<PyClass>() ?: psiElement
        }
        val findPsiTypes = findPythonPsiTypes(significantElement, psiElement)
        return findPsiTypes.mapNotNull {
            findSourceFile(it, editor)
        }.toSet()
    }

    private fun findPythonPsiTypes(psiElement: Array<PsiElement>, originalElement: PsiElement): Set<PyReferenceExpression> {
        return psiElement.map {
            findPythonPsiTypes(it, originalElement)
        }.flatten().distinctBy { it.name }.toSet()
    }

    private fun findPythonPsiTypes(psiElement: PsiElement, originalElement: PsiElement): Set<PyReferenceExpression> {
        if (psiElement is PyReferenceExpression) {
            return setOf(psiElement);
        }
        // If the cursor was only inside a class, do not look into the functions
        if (psiElement is PyFunction && originalElement is PyClass) {
            return setOf();
        }
        return findPythonPsiTypes(psiElement.children, originalElement).toSet()
    }

    private fun findSourceFile(psiReference: PyReferenceExpression, editor: Editor): VirtualFile? {
        val declaration = PyResolveUtil.resolveDeclaration(
            psiReference.reference,
            PyResolveContext.defaultContext(
                TypeEvalContext.codeAnalysis(
                    editor.project!!,
                    psiReference.containingFile
                )
            )
        )
        if (declaration != null) {
            val file = declaration.navigationElement.containingFile.virtualFile
            if (file.isInLocalFileSystem) {
                return file
            } else {
                // TODO: what to do with library files?
                return null
            }
        }
        return null

    }
}
