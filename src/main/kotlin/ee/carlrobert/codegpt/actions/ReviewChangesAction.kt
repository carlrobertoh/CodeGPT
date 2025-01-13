package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.VcsDataKeys
import com.intellij.openapi.vcs.changes.Change
import ee.carlrobert.codegpt.completions.ConversationType
import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager

class ReviewChangesAction : BaseCommitWorkflowAction() {

    override fun getTitle(changes: Array<Change>): String {
        return if (changes.size > 1) "Review Changes"
        else "Review Change"
    }

    override fun performAction(
        project: Project,
        event: AnActionEvent,
        gitDiff: String
    ) {
        val selectedChanges = event.getData(VcsDataKeys.SELECTED_CHANGES) ?: return
        project.service<ChatToolWindowContentManager>().sendMessageInNewTab(
            Message(
                buildString {
                    appendLine("Review the following changes:")
                    appendLine("```diff")
                    appendLine(gitDiff)
                    appendLine("```")
                }
            ).apply {
                referencedFilePaths = selectedChanges.mapNotNull { it.virtualFile?.path }
            }, ConversationType.REVIEW_CHANGES
        )
    }
}