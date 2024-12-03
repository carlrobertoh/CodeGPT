package ee.carlrobert.codegpt.refactorings

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.SuggestedNameInfo
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import com.intellij.refactoring.rename.NameSuggestionProvider

class DefaultNameSuggestionProvider : NameSuggestionProvider {

    companion object {
        val KEY = Key.create<Boolean>("codegpt.refactorings.renameAllowed")
    }

    override fun getSuggestedNames(
        element: PsiElement,
        nameSuggestionContext: PsiElement?,
        result: MutableSet<String>
    ): SuggestedNameInfo {
        element.elementType?.let {
            if (PSIMethodMapping.contains(it)) {
                element.putUserData(KEY, true)
            }
        }

        return SuggestedNameInfo.NULL_INFO
    }

    private enum class PSIMethodMapping(val types: List<String>) {
        GO(listOf("FILE", "METHOD_DECLARATION|FUNCTION_DECLARATION")),
        JAVA(listOf("java.FILE", "METHOD")),
        PY(listOf("FILE", "Py:FUNCTION_DECLARATION")),
        JAVASCRIPT(listOf("JS:FUNCTION_DECLARATION", "JS:TYPESCRIPT_FUNCTION")),
        CS(listOf("FILE", "DUMMY_TYPE_DECLARATION", "DUMMY_BLOCK")),
        PHP(listOf("FILE", "Class method|function|Function")),
        KOTLIN(listOf("FUN")),
        DEFAULT(listOf("FILE", "METHOD_DECLARATION"));

        companion object {
            fun contains(type: IElementType): Boolean {
                return entries.any { it.types.contains(type.toString()) }
            }
        }
    }
}