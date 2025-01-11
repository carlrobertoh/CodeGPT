package ee.carlrobert.codegpt.actions

import com.intellij.openapi.project.Project
import com.intellij.vcs.commit.CommitWorkflowUi
import ee.carlrobert.codegpt.completions.CommitMessageCompletionParameters
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.settings.prompts.CommitMessageTemplate

class GenerateCommitMessageAction : BaseCommitWorkflowAction() {

    override fun getTitle(commitWorkflowUi: CommitWorkflowUi): String {
        return "Generate Message"
    }

    override fun performAction(
        project: Project,
        commitWorkflowUi: CommitWorkflowUi,
        gitDiff: String
    ) {
        CompletionRequestService.getInstance().getCommitMessageAsync(
            CommitMessageCompletionParameters(
                gitDiff,
                project.getService(CommitMessageTemplate::class.java).getSystemPrompt()
            ),
            CommitMessageEventListener(project, commitWorkflowUi)
        )
    }
}