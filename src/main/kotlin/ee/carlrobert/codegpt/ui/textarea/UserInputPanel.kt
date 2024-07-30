package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.project.Project
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
import java.awt.*
import javax.swing.JPanel

class UserInputPanel(
    private val project: Project,
    private val onSubmit: (String, Boolean) -> Unit,
    private val onStop: () -> Unit
) : JPanel(BorderLayout()) {

    private val textPane = CustomTextPane { handleSubmit() }
        .apply {
            addKeyListener(CustomTextPaneKeyAdapter(project, this) {
                webSearchIncluded = true
            })
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
    private val imageActionSupported = AtomicBooleanProperty(isImageActionSupported())
    private var webSearchIncluded: Boolean = false

    val text: String
        get() = textPane.text

    init {
        isOpaque = false
        add(textPane, BorderLayout.CENTER)
        add(getFooter(), BorderLayout.SOUTH)
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

    private fun handleSubmit() {
        val text = textPane.text
            // TODO
            .replace("@web", "")
            .trim()
        if (text.isNotEmpty()) {
            onSubmit(text, webSearchIncluded)
            textPane.text = ""
        }
    }

    private fun getFooter(): JPanel {
        val attachImageLink = AnActionLink(CodeGPTBundle.get("shared.image"), AttachImageAction())
            .apply {
                icon = AllIcons.FileTypes.Image
                font = JBUI.Fonts.smallFont()
            }
        val modelComboBox = ModelComboBoxAction(
            project,
            {
                imageActionSupported.set(isImageActionSupported())
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
                cell(attachImageLink).visibleIf(imageActionSupported)
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
}