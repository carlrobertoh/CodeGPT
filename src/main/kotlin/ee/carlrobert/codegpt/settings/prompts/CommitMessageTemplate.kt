package ee.carlrobert.codegpt.settings.prompts

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level.PROJECT
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.settings.BranchNamePlaceholderStrategy
import ee.carlrobert.codegpt.settings.DatePlaceholderStrategy
import ee.carlrobert.codegpt.settings.Placeholder
import ee.carlrobert.codegpt.settings.Placeholder.BRANCH_NAME
import ee.carlrobert.codegpt.settings.Placeholder.DATE_ISO_8601
import ee.carlrobert.codegpt.settings.PlaceholderStrategy

@Service(PROJECT)
class CommitMessageTemplate private constructor(project: Project) {

    companion object {
        fun getHtmlDescription(): String {
            val placeholderDescriptions = listOf(BRANCH_NAME, DATE_ISO_8601).joinToString("\n") {
                "<li><strong>${it.name}</strong>: ${it.description}</li>"
            }

            return buildString {
                append("<p>Template for generating commit messages. Use the following placeholders to insert dynamic values:</p>\n")
                append("<ul>$placeholderDescriptions</ul>\n")
            }
        }
    }

    private val placeholderStrategyMapping: Map<Placeholder, PlaceholderStrategy> = mapOf(
        BRANCH_NAME to BranchNamePlaceholderStrategy(project),
        DATE_ISO_8601 to DatePlaceholderStrategy()
    )

    fun getSystemPrompt(): String =
        service<PromptsSettings>().state.coreActions.generateCommitMessage.instructions.let { template ->
            placeholderStrategyMapping.entries.fold(
                template ?: ""
            ) { acc, (placeholder, strategy) ->
                acc.replace("{${placeholder.name}}", strategy.getReplacementValue())
            }
        }
}