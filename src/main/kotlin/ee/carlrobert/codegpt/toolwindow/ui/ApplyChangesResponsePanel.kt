package ee.carlrobert.codegpt.toolwindow.ui

import com.intellij.diff.DiffManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.events.ApplyResponseDirectlyEventDetails
import ee.carlrobert.codegpt.toolwindow.chat.ui.ApplyChangesBackgroundTask
import ee.carlrobert.codegpt.ui.OverlayUtil
import ee.carlrobert.codegpt.ui.UIUtil
import ee.carlrobert.codegpt.util.EditorDiffUtil.createDiffRequest
import ee.carlrobert.codegpt.util.EditorUtil.getSelectedEditor
import ee.carlrobert.codegpt.util.EditorUtil.updateEditorDocument
import ee.carlrobert.codegpt.util.MarkdownUtil.convertMdToHtml
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JPanel

class ApplyChangesResponsePanel(
    private val project: Project,
    eventDetails: ApplyResponseDirectlyEventDetails
) : JPanel(BorderLayout()) {

    private val container = JPanel(BorderLayout())

    init {
        container.add(
            UIUtil.createTextPane(convertMdToHtml(eventDetails.arguments.question), false),
            BorderLayout.NORTH
        )
        container.add(createApplyChangesButtonsPanel(eventDetails), BorderLayout.SOUTH)
        add(container, BorderLayout.CENTER)
    }

    private fun createApplyChangesButtonsPanel(eventDetails: ApplyResponseDirectlyEventDetails) =
        JPanel(FlowLayout(FlowLayout.LEADING, 0, 0)).apply {
            border = JBUI.Borders.empty(4, 0)
            add(
                createActionLink(
                    CodeGPTBundle.get("applyChangesResponsePanel.implementAndApply"),
                    eventDetails,
                    true
                )
            )
            add(Box.createHorizontalStrut(4))
            add(JBLabel("|"))
            add(Box.createHorizontalStrut(4))
            add(
                createActionLink(
                    CodeGPTBundle.get("applyChangesResponsePanel.implement"),
                    eventDetails,
                    false
                )
            )
        }

    private fun createSecondaryButtonsPanel(modifiedFileContent: String, mainEditor: Editor) =
        JPanel(FlowLayout(FlowLayout.LEADING, 0, 0)).apply {
            border = JBUI.Borders.empty(4, 0)
            add(ActionLink(CodeGPTBundle.get("applyChangesResponsePanel.applyDirectly")).apply {
                addActionListener {
                    updateEditorDocument(mainEditor, modifiedFileContent)
                }
            })
            add(Box.createHorizontalStrut(4))
            add(JBLabel("|"))
            add(Box.createHorizontalStrut(4))
            add(ActionLink(CodeGPTBundle.get("applyChangesResponsePanel.applyPartially")).apply {
                addActionListener {
                    val diffRequest = createDiffRequest(
                        project,
                        LightVirtualFile(mainEditor.virtualFile.name, modifiedFileContent),
                        mainEditor,
                        mainEditor.virtualFile
                    )
                    service<DiffManager>().showDiff(project, diffRequest)
                }
            })
        }

    private fun createActionLink(
        text: String,
        eventDetails: ApplyResponseDirectlyEventDetails,
        applyDirectly: Boolean
    ) = ActionLink(text).apply {
        addActionListener {
            if (getSelectedEditor(project) == null) {
                OverlayUtil.showWarningBalloon(
                    CodeGPTBundle.get("applyChangesResponsePanel.unableToLocateEditor"),
                    this.locationOnScreen
                )
                return@addActionListener
            }
            runBackgroundTask(eventDetails, applyDirectly)
        }
    }

    private fun runBackgroundTask(
        eventDetails: ApplyResponseDirectlyEventDetails,
        applyDirectly: Boolean
    ) {
        val mainEditor = getSelectedEditor(project)
            ?: throw IllegalStateException("Unable to find active editor")

        val progressPanel = ResponseBodyProgressPanel()
        progressPanel.updateProgressContainer(
            CodeGPTBundle.get("applyChangesResponsePanel.thinking"),
            null
        )

        container.removeAll()
        container.add(progressPanel, BorderLayout.NORTH)
        container.revalidate()
        container.repaint()
    }
}
