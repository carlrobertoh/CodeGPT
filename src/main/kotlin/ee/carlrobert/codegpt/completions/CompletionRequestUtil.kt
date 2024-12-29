package ee.carlrobert.codegpt.completions

import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.settings.IncludedFilesSettings
import java.util.stream.Collectors

object CompletionRequestUtil {
    @JvmStatic
    fun getPromptWithContext(
        referencedFiles: List<ReferencedFile>,
        userPrompt: String?
    ): String {
        val includedFilesSettings = service<IncludedFilesSettings>().state
        val repeatableContext = referencedFiles.stream()
            .map { item: ReferencedFile ->
                includedFilesSettings.repeatableContext
                    .replace("{FILE_PATH}", item.filePath())
                    .replace(
                        "{FILE_CONTENT}", String.format(
                            "```%s%n%s%n```",
                            item.fileExtension,
                            item.fileContent().trim { it <= ' ' })
                    )
            }
            .collect(Collectors.joining("\n\n"))

        return includedFilesSettings.promptTemplate
            .replace("{REPEATABLE_CONTEXT}", repeatableContext)
            .replace("{QUESTION}", userPrompt!!)
    }
}