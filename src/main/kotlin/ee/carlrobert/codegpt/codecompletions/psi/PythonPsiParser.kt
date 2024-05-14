package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.jetbrains.python.PythonFileType

// TODO
class PythonPsiParser : LanguagePsiParser {

    override fun findContextSourceFiles(psiElement: PsiElement, editor: Editor): Set<VirtualFile> {
//            val parent = psiElement.findParentOfType<PyFunction>()
//                ?: psiElement.findParentOfType<PyStatementList>() ?: psiElement.findParentOfType<PyClass>() ?: psiElement
//            val call = psiElement.parent.children[1].children[1].children[1].children[0] as PyCallExpression
//            val typeEvalContext = parameters.getTypeEvalContext()
//            val resolveContext = PyResolveContext.defaultContext()
//            val callableTypes = call.multiResolveCallee(resolveContext)

//            val findPsiTypes = findPythonPsiTypes(parent)
//            return findPsiTypes.mapNotNull {
//                findSourceFile(it, editor)
//            }.toSet()
        return setOf()
    }

//        private fun findPythonPsiTypes(psiElement: Array<PyElement>): Set<PyType> {
//            return psiElement.map {
//                findPythonPsiTypes(it)
//            }.flatten().distinctBy { it.name }.toSet()
//        }
//
//        private fun findPythonPsiTypes(psiElement: PyElement): Set<PyType> {
//            if (psiElement is PyType) {
//                return setOf(psiElement);
//            }
//
//            var childElements = psiElement.children
////            if(psiElement is PsiMethod && psiElement.parent is PsiClass){
////                childElements += (psiElement.parent as PsiClass).allFields
////            }
//            return findPythonPsiTypes(childElements).toSet()
//        }

//        private fun findSourceFile(psiElement: PsiTypeElement, editor: Editor): VirtualFile? {
//            val psiClass = PsiTypesUtil.getPsiClass(psiElement.type)
//            if (psiClass != null) {
//                val file = psiClass.navigationElement.containingFile.virtualFile
//                val module = ModuleUtilCore.findModuleForPsiElement(psiElement)
//                val moduleRootManager = ModuleRootManager.getInstance(
//                    module!!
//                )
//
//                if (file.isInLocalFileSystem) {
//                    return file
//                } else {
////                    var orderEntries = ProjectFileIndex.getInstance(editor.project!!).getOrderEntriesForFile(file)
////                    if(orderEntries.isNotEmpty()){
////                        val orderEntry= orderEntries[0]
////                    }
////                    val url: URL = URL("jar:file:/home/user/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.14.1/jackson-databind-2.14.1.jar!/")
////                    val jarConnection: JarURLConnection = url.openConnection() as JarURLConnection
////                    val manifest: Manifest = jarConnection.getManifest()
////                    println()
//                    return null
//                }
//            }
//            return null
//
//    }
}
