package ee.carlrobert.codegpt.toolwindow.chat.editor.actions

import com.intellij.diff.DiffManager
import com.intellij.icons.AllIcons.Actions
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.actions.ActionType
import ee.carlrobert.codegpt.actions.TrackableAction
import ee.carlrobert.codegpt.completions.CompletionClientProvider
import ee.carlrobert.codegpt.ui.OverlayUtil
import ee.carlrobert.codegpt.util.EditorDiffUtil.createDiffRequest
import ee.carlrobert.codegpt.util.EditorUtil
import ee.carlrobert.codegpt.util.EditorUtil.getSelectedEditor
import ee.carlrobert.llm.client.codegpt.request.AutoApplyRequest
import ee.carlrobert.llm.client.codegpt.response.CodeGPTException
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JPanel

class AutoApplyAction(
    private val project: Project,
    private val toolwindowEditor: Editor,
    private val headerPanel: JPanel,
) : TrackableAction(
    CodeGPTBundle.get("toolwindow.chat.editor.action.autoApply.title"),
    CodeGPTBundle.get("toolwindow.chat.editor.action.autoApply.description"),
    Actions.Lightning,
    ActionType.AUTO_APPLY
) {
    override fun handleAction(event: AnActionEvent) {
        val mainEditor = getSelectedEditor(project)
            ?: throw IllegalStateException("Unable to find active editor")
        val request = AutoApplyRequest().apply {
            suggestedChanges = toolwindowEditor.document.text
            fileContent = mainEditor.document.text
        }

        headerPanel.getComponent(1).isVisible = false

        val acceptLink = createDisabledActionLink("Accept")
        val rejectLink = createDisabledActionLink("Reject")

        val actionsPanel = JPanel(FlowLayout(FlowLayout.TRAILING, 8, 0)).apply {
            border = JBUI.Borders.empty(4, 0)
            add(acceptLink)
            add(JBLabel("|"))
            add(rejectLink)
        }
        headerPanel.add(actionsPanel)

        ProgressManager.getInstance().run(
            ApplyChangesBackgroundTask(
                project,
                request,
                { modifiedFileContent ->
                    acceptLink.setupLink(mainEditor, actionsPanel) {
                        EditorUtil.updateEditorDocument(mainEditor, modifiedFileContent)
                    }
                    rejectLink.setupLink(mainEditor, actionsPanel)
                    showDiff(mainEditor, modifiedFileContent)
                },
                {
                    OverlayUtil.showNotification(it, NotificationType.ERROR)
                    resetState(mainEditor, actionsPanel)
                })
        )
    }

    private fun JButton.setupLink(
        mainEditor: Editor,
        actionsPanel: JPanel,
        onAction: (() -> Unit)? = null
    ) {
        isEnabled = true
        addActionListener {
            resetState(mainEditor, actionsPanel)
            onAction?.invoke()
        }
    }

    private fun showDiff(mainEditor: Editor, modifiedFileContent: String) {
        val diffRequest = createDiffRequest(
            project,
            LightVirtualFile(mainEditor.virtualFile.name, modifiedFileContent),
            mainEditor,
            mainEditor.virtualFile
        )
        runInEdt {
            service<DiffManager>().showDiff(project, diffRequest)
        }
    }

    private fun createDisabledActionLink(text: String): ActionLink {
        return ActionLink(text).apply {
            isEnabled = false
            autoHideOnDisable = false
        }
    }

    private fun resetState(mainEditor: Editor, actionsPanel: JPanel) {
        headerPanel.remove(actionsPanel)
        headerPanel.getComponent(1).isVisible = true
        FileEditorManager.getInstance(project).openFile(mainEditor.virtualFile, true)
    }

    class ApplyChangesBackgroundTask(
        project: Project,
        private val request: AutoApplyRequest,
        private val onSuccess: (modifiedFileContent: String) -> Unit,
        private val onFailure: (errorMessage: String) -> Unit,
    ) : Task.Backgroundable(project, "Apply changes", true) {

        override fun run(indicator: ProgressIndicator) {
            indicator.isIndeterminate = false
            indicator.fraction = 1.0
            indicator.text = "CodeGPT: Applying changes"

            try {
                val modifiedFileContent = CompletionClientProvider.getCodeGPTClient()
                    .applySuggestedChanges(request)
                    .modifiedFileContent
                onSuccess(modifiedFileContent)
            } catch (ex: CodeGPTException) {
                onFailure(ex.detail)
            }
        }
    }
}
