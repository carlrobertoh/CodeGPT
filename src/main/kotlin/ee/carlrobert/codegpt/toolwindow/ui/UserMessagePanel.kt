package ee.carlrobert.codegpt.toolwindow.ui

import com.intellij.icons.AllIcons
import com.intellij.icons.AllIcons.Actions
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.ColorUtil
import com.intellij.ui.JBColor
import com.intellij.ui.RoundedIcon
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.components.BorderLayoutPanel
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.events.WebSearchEventDetails
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.toolwindow.chat.ui.ChatMessageResponseBody
import ee.carlrobert.codegpt.toolwindow.chat.ui.ImageAccordion
import ee.carlrobert.codegpt.toolwindow.chat.ui.SelectedFilesAccordion
import ee.carlrobert.codegpt.ui.IconActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Image
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.swing.*

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

    private fun getUserIcon(): Icon {
        try {
            val avatarBase64 = GeneralSettings.getCurrentState().avatarBase64
            return if (avatarBase64.isNullOrEmpty()) {
                Icons.User
            } else {
                val originalIcon = ImageIcon(Base64.getDecoder().decode(avatarBase64))
                val resizedImage = originalIcon.image.getScaledInstance(
                    Icons.Default.iconWidth,
                    Icons.Default.iconHeight,
                    Image.SCALE_SMOOTH
                )
                RoundedIcon(resizedImage, 1.0)
            }
        } catch (ex: Exception) {
            return Icons.User
        }
    }

    override fun createDisplayNameLabel(): JBLabel {
        return JBLabel(
            GeneralSettings.getCurrentState().displayName,
            getUserIcon(),
            SwingConstants.LEADING
        )
            .setAllowAutoWrapping(true)
            .withFont(JBFont.label().asBold())
            .apply {
                iconTextGap = 6
            }
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
            ChatMessageResponseBody(project, true, false, false, parentDisposable)
                .withResponse(message.prompt)
        )
    }

    private fun getAdditionalContextPanel(project: Project, message: Message): JPanel? {
        val documentationDetails = message.documentationDetails
        val referencedFilePaths = message.referencedFilePaths ?: emptyList()
        if (documentationDetails == null && referencedFilePaths.isEmpty() && message.personaName.isNullOrEmpty()) {
            return null
        }

        return BorderLayoutPanel().apply {
            isOpaque = false

            val additionalContextPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                isOpaque = false
            }
            addToTop(additionalContextPanel)

            message.personaName?.let {
                additionalContextPanel.add(
                    createAdditionalContextPanel(
                        CodeGPTBundle.get("userMessagePanel.persona.title"),
                        BorderLayoutPanel()
                            .addToTop(JBLabel(it, AllIcons.General.User, SwingUtilities.LEADING))
                            .withBorder(JBUI.Borders.emptyBottom(8))
                            .andTransparent()
                    )
                )
            }

            documentationDetails?.let {
                val listModel = DefaultListModel<WebSearchEventDetails>().apply {
                    addElement(WebSearchEventDetails(UUID.randomUUID(), it.name, it.url, it.url))
                }
                additionalContextPanel.add(
                    createAdditionalContextPanel(
                        CodeGPTBundle.get("userMessagePanel.documentation.title"),
                        WebpageList(listModel)
                    )
                )
            }

            if (referencedFilePaths.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val referencedFiles = referencedFilePaths.mapNotNull {
                        LocalFileSystem.getInstance().findFileByPath(it)
                    }
                    withContext(Dispatchers.Main) {
                        additionalContextPanel.add(SelectedFilesAccordion(project, referencedFiles))
                    }
                }
            }
        }
    }

    private fun createAdditionalContextPanel(title: String, component: JComponent): JPanel {
        return BorderLayoutPanel().apply {
            isOpaque = false
            addToTop(BorderLayoutPanel().apply {
                isOpaque = false
                border = JBUI.Borders.empty(8, 0)
                addToLeft(JBLabel(title).withFont(JBUI.Fonts.miniFont()))
            })
            addToCenter(BorderLayoutPanel().apply {
                isOpaque = false
                addToLeft(component)
            })
        }
    }
}