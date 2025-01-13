package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vcs.ui.CommitMessage
import ee.carlrobert.codegpt.completions.CommitMessageCompletionParameters
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.settings.prompts.CommitMessageTemplate

class GenerateCommitMessageWithAdditionalInputAction : BaseCommitWorkflowAction() {

    override fun getTitle(changes: Array<Change>): String {
        return "Generate Message with Additional Input"
    }

    override fun performAction(
        project: Project,
        event: AnActionEvent,
        gitDiff: String
    ) {
        val userInput = Messages.showMultilineInputDialog(
            project,
            "Enter additional input for the commit message:",
            "Additional Input",
            "",
            Messages.getQuestionIcon(),
            null
        )
        val commitMessage = event.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) as? CommitMessage
        val document = commitMessage?.editorField?.editor?.document ?: return

        if (userInput != null) {
            val systemPrompt =
                project.getService(CommitMessageTemplate::class.java).getSystemPrompt()
            CompletionRequestService.getInstance().getCommitMessageAsync(
                CommitMessageCompletionParameters(
                    gitDiff,
                    """
                            $systemPrompt
                            
                            User input: $userInput
                        """.trimIndent()
                ),
                CommitMessageEventListener(project, document)
            )
        }
    }
}