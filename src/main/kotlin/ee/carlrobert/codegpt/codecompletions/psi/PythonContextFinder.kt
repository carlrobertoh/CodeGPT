package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
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
    override fun findContextSourceFiles(psiElement: PsiElement): Set<VirtualFile> {
        val enclosingElement = findEnclosingElement(psiElement)
        val referenceExpressions = findRelevantElements(enclosingElement, enclosingElement)
        val declarations =
            referenceExpressions.map { findDeclarations(it, psiElement.containingFile.project) }.flatten().distinct()
        return declarations.mapNotNull { findSourceFile(it) }.toSet()
    }

    private fun findDeclarations(
        pyReference: PyReferenceExpression,
        project: Project
    ): Set<PsiElement> {
        // see https://github.com/JetBrains/intellij-community/blob/ae5290861a1f41b93c48c475239a52faa94b97b0/python/python-psi-impl/src/com/jetbrains/python/codeInsight/PyTargetElementEvaluator.java#L51-L54
        val declaration = PyResolveUtil.resolveDeclaration(
            pyReference.reference,
            PyResolveContext.defaultContext(
                TypeEvalContext.codeAnalysis(
                    project,
                    pyReference.containingFile
                )
            )
        )
        return if (declaration != null) setOf(declaration) else emptySet()
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

    private fun findRelevantElements(
        psiElement: Array<PsiElement>,
        originalElement: PsiElement
    ): Set<PyReferenceExpression> {
        return psiElement.map {
            findRelevantElements(it, originalElement)
        }.flatten().distinctBy { it.name }.toSet()
    }

    /**
     * Finds [PyReferenceExpression]s inside of [psiElement].
     * If [psiElement] is a [PyFunction] it also adds all [PyReferenceExpression] of any class/instance fields.
     */
    fun findRelevantElements(
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
            // add class and instance fields of enclosing class
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
        return findRelevantElements(significationElements, enclosingElement).toSet()
    }

    private fun findTargetExpressionAssignment(targetExpression: PyTargetExpression): PyExpression? {
        return targetExpression.findParentOfType<PyAssignmentStatement>()?.assignedValue
    }

    private fun findSourceFile(psiElement: PsiElement): VirtualFile? {
        val file = psiElement.navigationElement.containingFile.virtualFile
        return if (file.isInLocalFileSystem) {
            file
        } else {
            // TODO: what to do with library files?
            null
        }

    }
}
