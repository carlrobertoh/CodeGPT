package ee.carlrobert.codegpt.codecompletions.psi

import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElement


fun PsiElement.filePath(): String {
    return ApplicationManager.getApplication()
        .runReadAction<String> { this.containingFile.virtualFile.path }
}

fun PsiElement.readText(): String {
    return ApplicationManager.getApplication().runReadAction<String> { this.text }
}