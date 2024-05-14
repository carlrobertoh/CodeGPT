package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiTypesUtil
import com.intellij.psi.util.findParentOfType

class JavaPsiParser : LanguagePsiParser {

    /**
     * Finds enclosing [PsiMethod] or [PsiClass] of [psiElement] and
     * determines source code files of all used [PsiTypeElement]s for the context.
     */
    override fun findContextSourceFiles(psiElement: PsiElement, editor: Editor): Set<VirtualFile> {
        val parent = psiElement.findParentOfType<PsiMethod>()
            ?: psiElement.findParentOfType<PsiClass>() ?: psiElement
        val findPsiTypes = findPsyTypes(parent)
        return findPsiTypes.mapNotNull {
            findSourceFile(it, editor)
        }.toSet()
    }

    private fun findPsyTypes(psiElement: Array<PsiElement>): Set<PsiTypeElement> {
        return psiElement.map {
            findPsyTypes(it)
        }.flatten().distinctBy { it.type }.toSet()
    }

    /**
     * Finds [PsiTypeElement]s inside of [psiElement].
     * If [psiElement] is a [PsiMethod] it also adds all [PsiTypeElement] of any class fields.
     */
    private fun findPsyTypes(psiElement: PsiElement): Set<PsiTypeElement> {
        if (psiElement is PsiTypeElement
            // For wrapper classes with generics like List<T> only the generic type classes are returned
            && (psiElement.type !is PsiClassReferenceType || (psiElement.type as PsiClassReferenceType).parameters.isEmpty())
        ) {
            return setOf(psiElement);
        }

        var childElements = psiElement.children
        if (psiElement is PsiMethod && psiElement.parent is PsiClass) {
            childElements += (psiElement.parent as PsiClass).allFields
        }
        return findPsyTypes(childElements).toSet()
    }

    /**
     * If [psiTypeElement] is a Java class defined in the user's project the corresponding source code
     * [VirtualFile] is returned, otherwise `null` is returned.
     */
    private fun findSourceFile(psiTypeElement: PsiTypeElement, editor: Editor): VirtualFile? {
        val psiClass = PsiTypesUtil.getPsiClass(psiTypeElement.type)
        if (psiClass != null) {
            val file = psiClass.navigationElement.containingFile.virtualFile
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
        return null
    }
}
