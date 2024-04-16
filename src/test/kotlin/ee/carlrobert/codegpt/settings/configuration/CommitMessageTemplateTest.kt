package ee.carlrobert.codegpt.settings.configuration

import com.intellij.openapi.components.service
import git4idea.commands.GitCommand
import org.assertj.core.api.Assertions.assertThat
import testsupport.VcsTestCase
import java.time.LocalDate

class CommitMessageTemplateTest : VcsTestCase() {

    fun testGetReplacedSystemPrompt() {
        git(GitCommand.INIT)
        git(GitCommand.CHECKOUT, listOf("-b", "feature/my-cool-feature"))
        waitUntilChangesApplied()
        service<ConfigurationSettings>().state.commitMessagePrompt = buildString {
            append("Branch: {BRANCH_NAME}\n")
            append("Date: {DATE_ISO_8601}")
        }

        val systemPrompt = project.service<CommitMessageTemplate>().getSystemPrompt()

        assertThat(systemPrompt).isEqualTo(
            buildString {
                append("Branch: feature/my-cool-feature\n")
                append("Date: ${LocalDate.now()}")
            }
        )
    }
}

