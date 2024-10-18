package ee.carlrobert.codegpt.toolwindow.chat.ui

import com.intellij.notification.NotificationType
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.completions.CompletionClientProvider
import ee.carlrobert.codegpt.ui.OverlayUtil
import ee.carlrobert.llm.client.codegpt.request.AutoApplyRequest
import ee.carlrobert.llm.client.codegpt.response.CodeGPTException

class ApplyChangesBackgroundTask(
    project: Project,
    private val request: AutoApplyRequest,
    private val onSuccess: (modifiedFileContent: String) -> Unit,
) : Task.Backgroundable(project, "Applying suggestion", true) {

    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = false
        indicator.fraction = 1.0
        indicator.text = "CodeGPT: Implementing changes"

        try {
            val modifiedFileContent = CompletionClientProvider.getCodeGPTClient()
                .applySuggestedChanges(request)
                .modifiedFileContent
            onSuccess(modifiedFileContent)
        } catch (ex: CodeGPTException) {
            OverlayUtil.showNotification(ex.detail, NotificationType.ERROR)
        }
    }
}
