package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiTypesUtil
import com.intellij.psi.util.findParentOfType

class JavaContextFinder : LanguageContextFinder {

    /**
     * Finds enclosing [PsiMethod] or [PsiClass] of [psiElement] and
     * determines source code files of all referenced classes or methods.
     */
    override fun findContextSourceFiles(psiElement: PsiElement, editor: Editor): Set<VirtualFile> {
        val enclosingElement = findEnclosingElement(psiElement)
        val relevantElements = findRelevantElements(enclosingElement)
        val psiTargets = relevantElements.map { findPsiTarget(it) }.flatten().distinct()
        return psiTargets.mapNotNull { findSourceFile(it, editor) }.toSet()
    }


    fun findEnclosingElement(psiElement: PsiElement): PsiElement {
        return psiElement.findParentOfType<PsiMethod>(false)
            ?: psiElement.findParentOfType<PsiClass>(false) ?: psiElement
    }

    private fun findRelevantElements(psiElement: Array<PsiElement>): Set<PsiElement> {
        return psiElement.map { findRelevantElements(it) }.flatten().distinctBy { it.text }.toSet()
    }

    /**
     * Finds relevant [PsiTypeElement]s and [PsiMethodCallExpression]s that are used inside of [psiElement].
     * If [psiElement] is a [PsiMethod] it also adds all class and instance fields.
     */
    fun findRelevantElements(psiElement: PsiElement): Set<PsiElement> {
        if (psiElement is PsiTypeElement || psiElement is PsiMethodCallExpression) {
            return setOf(psiElement)
        }
        var childElements = psiElement.children
        if (psiElement is PsiMethod && psiElement.parent is PsiClass) {
            childElements = childElements.plus((psiElement.parent as PsiClass).allFields)
        }
        return findRelevantElements(childElements).toSet()
    }

    /**
     * Finds [PsiTarget]s to references used inside of [psiElement].
     */
    private fun findPsiTarget(psiElement: PsiElement): Set<PsiTarget> {
        if (psiElement is PsiTypeElement) {
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
            return setOf(clazz)
        }
        if (psiElement is PsiMethodCallExpression) {
            val method = psiElement.resolveMethod()
            // TODO: could also look at methodcall argument types
            return if (method != null) setOf(method) else emptySet()
        }

        if (psiElement is PsiReferenceExpression) {
            val resolvedTarget = psiElement.resolve()
            return if (resolvedTarget is PsiTarget) setOf(resolvedTarget) else emptySet()
        }
        return emptySet()
    }

    /**
     * If [psiTarget] is a Java class defined in the user's project the corresponding source code
     * [VirtualFile] is returned, otherwise `null` is returned.
     */
    private fun findSourceFile(psiTarget: PsiTarget, editor: Editor): VirtualFile? {
        // TODO: If e.g. only a single method is targeted we could also just use the method's source code
        //  not its entire class source code
        val file = psiTarget.navigationElement.containingFile.virtualFile
            if (file.isInLocalFileSystem) {
                return file
            } else {
                // TODO: ignore library classes? -> if not, might have to download sources.jar to get source code
//                val module = ModuleUtilCore.findModuleForPsiElement(psiTypeElement)
//                val moduleRootManager = ModuleRootManager.getInstance(
//                    module!!
//                )
//                var orderEntries =
//                    ProjectFileIndex.getInstance(editor.project!!).getOrderEntriesForFile(file)
//                if (orderEntries.isNotEmpty()) {
//                    val orderEntry = orderEntries[0]
//                }
//                val url: URL =
//                    URL("jar:file:/home/user/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.14.1/jackson-databind-2.14.1.jar!/")
//                val jarConnection: JarURLConnection = url.openConnection() as JarURLConnection
//                println()
                return null
            }
    }
}
