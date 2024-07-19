package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.components.AnActionLink
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.actions.AttachImageAction
import ee.carlrobert.codegpt.conversations.ConversationService
import ee.carlrobert.codegpt.conversations.ConversationsState
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.ModelComboBoxAction
import ee.carlrobert.codegpt.ui.IconActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.nio.file.Paths
import javax.swing.JPanel
import javax.swing.text.StyleContext
import javax.swing.text.StyledDocument

class UserInputPanel(
    private val project: Project,
    private val onSubmit: (String) -> Unit,
    private val onStop: () -> Unit
) : JPanel(BorderLayout()) {

    private val suggestionsPopupManager = SuggestionsPopupManager(project) {
        handleFileSelection(it)
    }
    private val textPane = CustomTextPane { handleSubmit() }.apply {
        addKeyListener(CustomTextPaneKeyAdapter())
    }
    private val submitButton = IconActionButton(
        object : AnAction(
            CodeGPTBundle.get("smartTextPane.submitButton.title"),
            CodeGPTBundle.get("smartTextPane.submitButton.description"),
            Icons.Send
        ) {
            override fun actionPerformed(e: AnActionEvent) {
                handleSubmit()
            }
        }
    )
    private val stopButton = IconActionButton(
        object : AnAction(
            CodeGPTBundle.get("smartTextPane.stopButton.title"),
            CodeGPTBundle.get("smartTextPane.stopButton.description"),
            AllIcons.Actions.Suspend
        ) {
            override fun actionPerformed(e: AnActionEvent) {
                onStop()
            }
        }
    ).apply { isEnabled = false }
    private val attachImageLink = AnActionLink(CodeGPTBundle.get("shared.image"), AttachImageAction())
        .apply {
            icon = AllIcons.General.Add
            font = JBUI.Fonts.smallFont()
        }

    val text: String
        get() = textPane.text

    init {
        isOpaque = false
        add(textPane, BorderLayout.CENTER)
        add(getFooter(), BorderLayout.SOUTH)
    }

    private fun getFooter(): JPanel {
        val modelComboBox = ModelComboBoxAction(
            project,
            {
                attachImageLink.isEnabled = isImageActionSupported()
                // TODO: Implement a proper session management
                if (service<ConversationsState>().state?.currentConversation?.messages?.isNotEmpty() == true) {
                    service<ConversationService>().startConversation()
                    project.service<ChatToolWindowContentManager>().createNewTabPanel()
                }
            },
            service<GeneralSettings>().state.selectedService
        ).createCustomComponent(ActionPlaces.UNKNOWN)

        return panel {
            twoColumnsRow({
                cell(modelComboBox).gap(RightGap.SMALL)
                cell(attachImageLink)
            }, {
                panel {
                    row {
                        cell(submitButton).gap(RightGap.SMALL)
                        cell(stopButton)
                    }
                }.align(AlignX.RIGHT)
            })
        }
    }

    private fun isImageActionSupported(): Boolean {
        return service<GeneralSettings>().state.selectedService.isImageActionSupported
    }

    fun setSubmitEnabled(enabled: Boolean) {
        submitButton.isEnabled = enabled
        stopButton.isEnabled = !enabled
    }

    override fun requestFocus() {
        textPane.requestFocus()
        textPane.requestFocusInWindow()
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = background
        g2.fillRoundRect(0, 0, width - 1, height - 1, 16, 16)
        super.paintComponent(g)
        g2.dispose()
    }

    override fun paintBorder(g: Graphics) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = JBUI.CurrentTheme.ActionButton.focusedBorder()
        if (textPane.isFocusOwner) {
            g2.stroke = BasicStroke(1.5F)
        }
        g2.drawRoundRect(0, 0, width - 1, height - 1, 16, 16)
        g2.dispose()
    }

    override fun getInsets(): Insets = JBUI.insets(4)

    private fun updateSuggestions() {
        CoroutineScope(Dispatchers.Default).launch {
            val lastAtIndex = textPane.text.lastIndexOf('@')
            if (lastAtIndex != -1) {
                val searchText = textPane.text.substring(lastAtIndex + 1)
                if (searchText.isNotEmpty()) {
                    val filePaths = project.service<FileSearchService>().searchFiles(searchText)
                    suggestionsPopupManager.updateSuggestions(filePaths)
                }
            } else {
                suggestionsPopupManager.hidePopup()
            }
        }
    }

    private fun handleSubmit() {
        val text = textPane.text.trim()
        if (text.isNotEmpty()) {
            onSubmit(text)
            textPane.text = ""
        }
    }

    private fun handleFileSelection(filePath: String) {
        val selectedFile = service<VirtualFileManager>().findFileByNioPath(Paths.get(filePath))
        selectedFile?.let { file ->
            textPane.highlightText(file.name)
            project.service<FileSearchService>().addFileToSession(file)
        }
        suggestionsPopupManager.hidePopup()
    }

    inner class CustomTextPaneKeyAdapter : KeyAdapter() {
        private val defaultStyle =
            StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE)

        override fun keyReleased(e: KeyEvent) {
            if (text.isEmpty()) {
                project.service<FileSearchService>().removeFilesFromSession()
            }

            // todo
            if (!text.contains('@')) {
                suggestionsPopupManager.hidePopup()
                return
            }

            when (e.keyCode) {
                KeyEvent.VK_UP, KeyEvent.VK_DOWN -> {
                    suggestionsPopupManager.requestFocus()
                    suggestionsPopupManager.selectNext()
                    e.consume()
                }

                else -> {
                    if (suggestionsPopupManager.isPopupVisible()) {
                        updateSuggestions()
                    }
                }
            }
        }

        override fun keyTyped(e: KeyEvent) {
            val popupVisible = suggestionsPopupManager.isPopupVisible()
            if (e.keyChar == '@' && !popupVisible) {
                suggestionsPopupManager.showPopup(textPane)
                return
            } else if (e.keyChar == '\t') {
                suggestionsPopupManager.requestFocus()
                suggestionsPopupManager.selectNext()
                return
            } else if (popupVisible) {
                updateSuggestions()
            }

            val doc = textPane.document as StyledDocument
            if (textPane.caretPosition >= 0) {
                doc.setCharacterAttributes(textPane.caretPosition, 1, defaultStyle, true)
            }
        }
    }
}