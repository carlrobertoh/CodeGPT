package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.roots.JdkUtils
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTypesUtil
import ee.carlrobert.codegpt.codecompletions.ContextElement
import ee.carlrobert.codegpt.codecompletions.InfillContext
import kotlinx.collections.immutable.toImmutableSet


class JavaContextFinder : LanguageContextFinder {

    /**
     * Finds enclosing [PsiMethod] or [PsiClass] of [psiElement] and
     * determines source code files of all referenced classes or methods.
     */
    override fun findContext(psiElement: PsiElement): InfillContext {
        val enclosingElement = findEnclosingElement(psiElement)
        val relevantElements = findRelevantElements(enclosingElement, enclosingElement)
        val psiTargets = relevantElements.map { findPsiTarget(it) }.flatten().distinct()
        val sourceElements = psiTargets.mapNotNull { findSourceElement(it) }
        return InfillContext(
            ContextElement(enclosingElement),
            sourceElements.map { ContextElement(it) }.toSet()
        )
    }

    private fun findEnclosingElement(psiElement: PsiElement): PsiElement =
        findEnclosingContext(psiElement)
            ?: PsiTreeUtil.prevCodeLeaf(psiElement)?.let { findEnclosingContext(it) } ?: psiElement

    fun findEnclosingContext(psiElement: PsiElement) =
        PsiTreeUtil.findFirstContext(psiElement, true) { it is PsiMethod || it is PsiClass }


    private fun findRelevantElements(
        psiElement: Collection<PsiElement>,
        rootElement: PsiElement
    ): Set<PsiElement> = psiElement.map { findRelevantElements(it, rootElement) }.flatten()
        .distinctBy { it.text }.toSet()

    /**
     * Finds relevant [PsiTypeElement]s and [PsiMethodCallExpression]s that are used inside of [psiElement].
     * If [psiElement] is a [PsiMethod] inside of a [PsiClass] it also adds all class and instance fields.
     */
    fun findRelevantElements(psiElement: PsiElement, rootElement: PsiElement): Set<PsiElement> {
        val resultSet = mutableSetOf<PsiElement>()
        psiElement.accept(object : PsiRecursiveElementWalkingVisitor() {
            override fun visitElement(element: PsiElement) {
                when (element) {
                    is PsiTypeElement, is PsiMethodCallExpression -> resultSet.add(element)
                    is PsiMethod -> {
                        if (rootElement is PsiClass) {
                            // If the cursor was not inside a PsiMethod but inside a PsiClass, do not look into the method
                            return
                        }
                        val enclosingContext = findEnclosingContext(element)
                        if (enclosingContext is PsiClass) {
                            // add class and instance fields of enclosing class
                            // TODO: class fields declarations have to be present in the infillPrompt
                            //  (same file as enclosingElement) as well
                            resultSet.addAll(
                                findRelevantElements(
                                    (element.parent as PsiClass).allFields.toSet(),
                                    rootElement
                                )
                            )
                        }
                        super.visitElement(element)
                    }

                    else -> super.visitElement(element)
                }
            }
        })
        return resultSet.distinctBy { it.text }.toImmutableSet()
    }

    /**
     * Finds [PsiTarget]s to references used inside of [psiElement].
     */
    private fun findPsiTarget(psiElement: PsiElement): Set<PsiTarget> {
        return when (psiElement) {
            is PsiTypeElement -> {
                val type = psiElement.type
                val clazz = PsiTypesUtil.getPsiClass(type) ?: return emptySet()
                // Include generic types, e.g. String for List<String>
                if (type is PsiClassReferenceType && type.parameters.isNotEmpty()) {
                    return setOf(clazz).plus(
                        type.parameters
                            .filterIsInstance<PsiClassReferenceType>()
                            .mapNotNull { it.resolve() }
                    )
                }
                setOf(clazz)
            }

            is PsiMethodCallExpression -> psiElement.resolveMethod()?.let { setOf(it) }
                ?: emptySet()

            is PsiReferenceExpression -> {
                val resolvedTarget = psiElement.resolve()
                if (resolvedTarget is PsiTarget) setOf(resolvedTarget) else emptySet()
            }

            else -> emptySet()
        }
    }

    private fun findSourceElement(psiTarget: PsiTarget): PsiElement? {
        return if (psiTarget.canNavigateToSource()
            && JdkUtils.getJdkForElement(psiTarget.navigationElement) == null
        ) {
            psiTarget.navigationElement
        } else {
            null
        }
    }
}
