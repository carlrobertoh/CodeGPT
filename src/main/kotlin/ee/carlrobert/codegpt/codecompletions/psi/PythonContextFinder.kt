package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.findParentOfType
import com.intellij.psi.util.prevLeaf
import com.jetbrains.python.psi.*
import com.jetbrains.python.psi.resolve.PyResolveContext
import com.jetbrains.python.psi.resolve.PyResolveUtil
import com.jetbrains.python.psi.types.TypeEvalContext

class PythonContextFinder : LanguageContextFinder {

    /**
     * Finds enclosing [PyFunction] or [PyClass] of [psiElement] and
     * determines source code files of all used [PyReferenceExpression]s for the context.
     */
    override fun findContextSourceFiles(psiElement: PsiElement, editor: Editor): Set<VirtualFile> {
        val enclosingElement = findEnclosingElement(psiElement)
        val referenceExpressions = findReferenceExpressions(enclosingElement, enclosingElement)
        return referenceExpressions.mapNotNull { findSourceFile(it, editor) }.toSet()
    }

    private fun findSignificantElement(psiElement: PsiElement): PsiElement? {
        var element = psiElement
        // In Python whitespaces have semantic meaning
        // its enclosing PyFunction/PyClass is not inevitably its parent node (same for comments)
        // therefore use previous leaf node to then find enclosing parent for
        if (element is PsiWhiteSpace || element is PsiComment) {
            element = element.prevLeaf { it !is PsiWhiteSpace && it !is PsiComment } ?: return null
        }
        return element
    }

    fun findEnclosingElement(psiElement: PsiElement): PsiElement {
        val significantElement = findSignificantElement(psiElement) ?: psiElement
        return significantElement.findParentOfType<PyFunction>(false)
            ?: significantElement.findParentOfType<PyClass>(false) ?: psiElement
    }

    private fun findReferenceExpressions(
        psiElement: Array<PsiElement>,
        originalElement: PsiElement
    ): Set<PyReferenceExpression> {
        return psiElement.map {
            findReferenceExpressions(it, originalElement)
        }.flatten().distinctBy { it.name }.toSet()
    }

    /**
     * Finds [PyReferenceExpression]s inside of [psiElement].
     * If [psiElement] is a [PyFunction] it also adds all [PyReferenceExpression] of any class/instance fields.
     */
    fun findReferenceExpressions(
        psiElement: PsiElement,
        enclosingElement: PsiElement
    ): Set<PyReferenceExpression> {
        if (psiElement is PyReferenceExpression) {
            return setOf(psiElement);
        }
        // If the cursor was not inside a PyFunction but only inside a PyClass, do not look into the functions
        if (psiElement is PyFunction && enclosingElement is PyClass) {
            return setOf();
        }
        var significationElements = psiElement.children

        if (psiElement is PyFunction) {
            val enclosingClass = psiElement.findParentOfType<PyClass>()
            if (enclosingClass != null) {
                significationElements = significationElements
                    .plus(enclosingClass.classAttributes.mapNotNull {
                        findTargetExpressionAssignment(it)
                    })
                    .plus(enclosingClass.instanceAttributes.mapNotNull {
                        findTargetExpressionAssignment(it)
                    })
            }
        }
        return findReferenceExpressions(significationElements, enclosingElement).toSet()
    }

    private fun findTargetExpressionAssignment(targetExpression: PyTargetExpression): PyExpression? {
        return targetExpression.findParentOfType<PyAssignmentStatement>()?.assignedValue
    }

    private fun findSourceFile(psiReference: PyReferenceExpression, editor: Editor): VirtualFile? {
        // see https://github.com/JetBrains/intellij-community/blob/ae5290861a1f41b93c48c475239a52faa94b97b0/python/python-psi-impl/src/com/jetbrains/python/codeInsight/PyTargetElementEvaluator.java#L51-L54
        // TODO: Slow operations are prohibited on EDT
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
