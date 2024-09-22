package ee.carlrobert.codegpt.completions

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.settings.IncludedFilesSettings
import ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent
import java.util.stream.Collectors

object CompletionRequestUtil {
    val GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT =
        getResourceContent("/prompts/generate-commit-message.txt")
    val FIX_COMPILE_ERRORS_SYSTEM_PROMPT =
        getResourceContent("/prompts/fix-compile-errors.txt")
    val GENERATE_METHOD_NAMES_SYSTEM_PROMPT =
        getResourceContent("/prompts/method-name-generator.txt")
    val EDIT_CODE_SYSTEM_PROMPT =
        getResourceContent("/prompts/edit-code.txt")

    @JvmStatic
    fun getPromptWithContext(
        referencedFiles: List<ReferencedFile>,
        userPrompt: String?
    ): String {
        val includedFilesSettings = service<IncludedFilesSettings>().state
        val repeatableContext = referencedFiles.stream()
            .map { item: ReferencedFile ->
                includedFilesSettings.repeatableContext
                    .replace("{FILE_PATH}", item.filePath)
                    .replace(
                        "{FILE_CONTENT}", String.format(
                            "```%s%n%s%n```",
                            item.fileExtension,
                            item.fileContent.trim { it <= ' ' })
                    )
            }
            .collect(Collectors.joining("\n\n"))

        return includedFilesSettings.promptTemplate
            .replace("{REPEATABLE_CONTEXT}", repeatableContext)
            .replace("{QUESTION}", userPrompt!!)
    }
}