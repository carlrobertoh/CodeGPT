package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.changes.Change
import com.intellij.openapi.vcs.ui.CommitMessage
import ee.carlrobert.codegpt.completions.CommitMessageCompletionParameters
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.settings.prompts.CommitMessageTemplate

class GenerateCommitMessageAction : BaseCommitWorkflowAction() {

    override fun getTitle(changes: Array<Change>): String {
        return "Generate Message"
    }

    override fun performAction(
        project: Project,
        event: AnActionEvent,
        gitDiff: String
    ) {
        val commitMessage = event.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) as? CommitMessage
        val document = commitMessage?.editorField?.editor?.document ?: return

        CompletionRequestService.getInstance().getCommitMessageAsync(
            CommitMessageCompletionParameters(
                gitDiff,
                project.getService(CommitMessageTemplate::class.java).getSystemPrompt()
            ),

            CommitMessageEventListener(project, document)
        )
    }
}