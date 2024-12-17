package ee.carlrobert.codegpt.toolwindow.ui

import com.intellij.icons.AllIcons
import com.intellij.icons.AllIcons.Actions
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.ui.ColorUtil
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.components.BorderLayoutPanel
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.events.WebSearchEventDetails
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.toolwindow.chat.ui.ChatMessageResponseBody
import ee.carlrobert.codegpt.toolwindow.chat.ui.ImageAccordion
import ee.carlrobert.codegpt.toolwindow.chat.ui.SelectedFilesAccordion
import ee.carlrobert.codegpt.ui.IconActionButton
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.DefaultListModel
import javax.swing.JPanel
import javax.swing.SwingConstants


class UserMessagePanel(
    private val project: Project,
    private val message: Message,
    private val parentDisposable: Disposable
) : BaseMessagePanel() {

    init {
        border = JBUI.Borders.customLine(JBColor.border(), 1, 0, 1, 0)
        background = ColorUtil.brighter(getBackground(), 2)

        setupAdditionalContext()
        setupImageIfPresent()
        setupResponseBody()
    }

    override fun createDisplayNameLabel(): JBLabel {
        return JBLabel(
            GeneralSettings.getCurrentState().displayName,
            Icons.User,
            SwingConstants.LEADING
        )
            .setAllowAutoWrapping(true)
            .withFont(JBFont.label().asBold())
    }

    fun addReloadAction(onReload: Runnable) {
        addIconActionButton(
            IconActionButton(
                object : AnAction(
                    CodeGPTBundle.get("shared.reload"),
                    CodeGPTBundle.get("shared.reloadDescription"),
                    Actions.Refresh
                ) {
                    override fun actionPerformed(e: AnActionEvent) {
                        onReload.run()
                    }
                },
                "RELOAD"
            )
        )
    }

    fun addDeleteAction(onDelete: Runnable) {
        addIconActionButton(
            IconActionButton(
                object : AnAction(
                    CodeGPTBundle.get("shared.delete"),
                    CodeGPTBundle.get("shared.deleteDescription"),
                    Actions.GC
                ) {
                    override fun actionPerformed(e: AnActionEvent) {
                        onDelete.run()
                    }
                },
                "DELETE"
            )
        )
    }

    fun displayImage(imageFilePath: String) {
        try {
            val path = Paths.get(imageFilePath)
            body.addToTop(ImageAccordion(path.fileName.toString(), Files.readAllBytes(path)))
        } catch (e: IOException) {
            body.addToTop(
                JBLabel(
                    "<html><small>Unable to load image $imageFilePath</small></html>",
                    AllIcons.General.Error,
                    SwingConstants.LEFT
                )
            )
        }
    }

    private fun setupAdditionalContext() {
        val additionalContextPanel = getAdditionalContextPanel(project, message)
        if (additionalContextPanel != null) {
            body.addToTop(additionalContextPanel)
        }
    }

    private fun setupImageIfPresent() {
        message.imageFilePath?.let { imageFilePath ->
            if (imageFilePath.isNotEmpty()) {
                displayImage(imageFilePath)
            }
        }
    }

    private fun setupResponseBody() {
        addContent(
            ChatMessageResponseBody(project, false, true, false, false, parentDisposable)
                .withResponse(message.prompt)
        )
    }

    private fun getAdditionalContextPanel(project: Project?, message: Message): JPanel? {
        val addedDocumentation = CodeGPTKeys.ADDED_DOCUMENTATION[project]
        val referencedFilePaths = message.referencedFilePaths ?: emptyList()
        if (addedDocumentation == null && referencedFilePaths.isEmpty()) {
            return null
        }

        return BorderLayoutPanel().apply {
            isOpaque = false

            if (addedDocumentation != null) {
                val listModel = DefaultListModel<WebSearchEventDetails>()
                listModel.addElement(
                    WebSearchEventDetails(
                        UUID.randomUUID(), addedDocumentation.name,
                        addedDocumentation.url, addedDocumentation.url
                    )
                )
                addToTop(createWebpageListPanel(WebpageList(listModel)))
            }

            if (referencedFilePaths.isNotEmpty()) {
                addToTop(SelectedFilesAccordion(project!!, referencedFilePaths))
            }
        }
    }

    private fun createWebpageListPanel(webpageList: WebpageList): JPanel {
        return BorderLayoutPanel().apply {
            isOpaque = false
            addToTop(BorderLayoutPanel().apply {
                isOpaque = false
                border = JBUI.Borders.empty(8, 0)
                addToLeft(
                    JBLabel(CodeGPTBundle.get("userMessagePanel.documentation.title"))
                        .withFont(JBUI.Fonts.miniFont())
                )
            })
            addToCenter(BorderLayoutPanel().apply {
                isOpaque = false
                addToLeft(webpageList)
            })
        }
    }
}