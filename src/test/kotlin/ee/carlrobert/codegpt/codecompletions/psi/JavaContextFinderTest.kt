package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.editor.VisualPosition
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent
import org.assertj.core.api.Assertions.assertThat

class JavaContextFinderTest : BasePlatformTestCase() {

    private val contextFinder = JavaContextFinder()

    fun testFindEnclosingContextMethod() {
        val file = myFixture.configureByText(
            "Util.java",
            getResourceContent("/codecompletions/psi/java.txt")
        )
        val psiElement =
            file.findElementAt(myFixture.editor.visualPositionToOffset(VisualPosition(15, 4)))
        val enclosingElement = contextFinder.findEnclosingContext(psiElement!!)
        assertThat(enclosingElement)
            .isInstanceOf(PsiMethod::class.java)
            .extracting("name")
            .isEqualTo("randomStrings")
    }

    fun testFindEnclosingContextClass() {
        val file = myFixture.configureByText(
            "Util.java",
            getResourceContent("/codecompletions/psi/java.txt")
        )
        val psiElement =
            file.findElementAt(myFixture.editor.visualPositionToOffset(VisualPosition(13, 2)))
        val contextFinder = contextFinder
        val enclosingElement = contextFinder.findEnclosingContext(psiElement!!)
        assertThat(enclosingElement)
            .isInstanceOf(PsiClass::class.java)
            .extracting("name")
            .isEqualTo("Util")
    }

    fun testFindRelevantElements() {
        val file = myFixture.configureByText(
            "Util.java",
            getResourceContent("/codecompletions/psi/java.txt")
        )
        val psiMethod =
            (file as PsiJavaFile).classes[0].findMethodsByName("randomStrings", false)[0]
        val relevantElements = contextFinder.findRelevantElements(psiMethod, psiMethod)
        assertThat(relevantElements)
            .hasSize(4)
            .extracting("text")
            .containsExactly(
                "String",
                "List<String>",
                "int",
                "IntStream.range(0, number).mapToObj(i -> Math.floor(100 * Math.random()) + \"\").toList()"
            )
    }

}