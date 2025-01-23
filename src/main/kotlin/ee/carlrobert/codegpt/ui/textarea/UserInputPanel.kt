package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.AnActionLink
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.actions.AttachImageAction
import ee.carlrobert.codegpt.conversations.Conversation
import ee.carlrobert.codegpt.conversations.ConversationService
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.ModelComboBoxAction
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.TotalTokensPanel
import ee.carlrobert.codegpt.ui.IconActionButton
import ee.carlrobert.codegpt.ui.textarea.header.GitCommitTagDetails
import ee.carlrobert.codegpt.ui.textarea.header.HeaderTagDetails
import ee.carlrobert.codegpt.ui.textarea.header.SelectionTagDetails
import ee.carlrobert.codegpt.ui.textarea.header.UserInputHeaderPanel
import ee.carlrobert.codegpt.ui.textarea.suggestion.SuggestionsPopupManager
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel
import git4idea.GitCommit
import java.awt.*
import java.awt.geom.Area
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import javax.swing.JPanel

class UserInputPanel(
    private val project: Project,
    private val conversation: Conversation,
    private val totalTokensPanel: TotalTokensPanel,
    parentDisposable: Disposable,
    private val onSubmit: (String, List<HeaderTagDetails>) -> Unit,
    private val onStop: () -> Unit
) : JPanel(BorderLayout()) {

    companion object {
        private const val CORNER_RADIUS = 16
    }

    private val suggestionsPopupManager = SuggestionsPopupManager(project, this)
    private val userInputHeaderPanel = UserInputHeaderPanel(
        project,
        suggestionsPopupManager
    )
    private val promptTextField =
        PromptTextField(project, suggestionsPopupManager, ::updateUserTokens) {
            handleSubmit(it, userInputHeaderPanel.getSelectedTags())
        }
    private val submitButton = IconActionButton(
        object : AnAction(
            CodeGPTBundle.get("smartTextPane.submitButton.title"),
            CodeGPTBundle.get("smartTextPane.submitButton.description"),
            IconUtil.scale(Icons.Send, null, 0.85f)
        ) {
            override fun actionPerformed(e: AnActionEvent) {
                handleSubmit(promptTextField.text, userInputHeaderPanel.getSelectedTags())
            }
        },
        "SUBMIT"
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
        },
        "STOP"
    ).apply { isEnabled = false }
    private val attachImageLink = AnActionLink(CodeGPTBundle.get("shared.image"), AttachImageAction())
        .apply {
            icon = AllIcons.General.Add
            font = JBUI.Fonts.smallFont()
        }

    val text: String
        get() = promptTextField.text

    init {
        background = service<EditorColorsManager>().globalScheme.defaultBackground
        add(userInputHeaderPanel, BorderLayout.NORTH)
        add(promptTextField, BorderLayout.CENTER)
        add(getFooter(), BorderLayout.SOUTH)

        Disposer.register(parentDisposable, promptTextField)
    }

    fun getSelectedTags(): List<HeaderTagDetails> {
        return userInputHeaderPanel.getSelectedTags()
    }

    fun setSubmitEnabled(enabled: Boolean) {
        submitButton.isEnabled = enabled
        stopButton.isEnabled = !enabled
    }

    fun addSelection(editorFile: VirtualFile, selectionModel: SelectionModel) {
        addTag(SelectionTagDetails(editorFile, selectionModel))
        promptTextField.requestFocusInWindow()
        selectionModel.removeSelection()
    }

    fun addCommitReferences(gitCommits: List<GitCommit>) {
        runInEdt {
            if (promptTextField.text.isEmpty()) {
                promptTextField.text = if (gitCommits.size == 1) {
                    "Explain the commit `${gitCommits[0].id.toShortString()}`"
                } else {
                    "Explain the commits ${gitCommits.joinToString(", ") { "`${it.id.toShortString()}`" }}"
                }
            }

            gitCommits.forEach {
                addTag(GitCommitTagDetails(it))
            }
            promptTextField.requestFocusInWindow()
            promptTextField.editor?.caretModel?.moveToOffset(promptTextField.text.length)
        }
    }

    fun addTag(tagDetails: HeaderTagDetails) {
        userInputHeaderPanel.addTag(tagDetails)
        val text = promptTextField.text
        if (text.isNotEmpty() && text.last() == '@') {
            promptTextField.text = text.substring(0, text.length - 1)
        }
    }

    override fun requestFocus() {
        invokeLater {
            promptTextField.requestFocusInWindow()
        }
    }

    override fun paintComponent(g: Graphics) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        val area = Area(Rectangle2D.Float(0f, 0f, width.toFloat(), height.toFloat()))
        val roundedRect = RoundRectangle2D.Float(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            CORNER_RADIUS.toFloat(),
            CORNER_RADIUS.toFloat()
        )
        area.intersect(Area(roundedRect))

        g2.clip = area

        g2.color = background
        g2.fill(area)

        super.paintComponent(g2)
        g2.dispose()
    }

    override fun paintBorder(g: Graphics) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = JBUI.CurrentTheme.Focus.defaultButtonColor()
        if (promptTextField.isFocusOwner) {
            g2.stroke = BasicStroke(1.5F)
        }
        g2.drawRoundRect(0, 0, width - 1, height - 1, CORNER_RADIUS, CORNER_RADIUS)
        g2.dispose()
    }

    override fun getInsets(): Insets = JBUI.insets(4)

    private fun handleSubmit(text: String, appliedTags: List<HeaderTagDetails> = emptyList()) {
        if (text.isNotEmpty() && submitButton.isEnabled) {
            onSubmit(text, appliedTags)
            promptTextField.clear()
        }
    }

    private fun updateUserTokens(text: String) {
        totalTokensPanel.updateUserPromptTokens(text)
    }

    private fun getFooter(): JPanel {
        val modelComboBox = ModelComboBoxAction(
            project,
            {
                attachImageLink.isEnabled = isImageActionSupported()
                // TODO: Implement a proper session management
                val conversationService = service<ConversationService>()
                if (conversation.messages.isNotEmpty()) {
                    conversationService.startConversation()
                    project.service<ChatToolWindowContentManager>().createNewTabPanel()
                } else {
                    conversation.model = conversationService.getModelForSelectedService(it)
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
        }.andTransparent()
    }

    private fun isImageActionSupported(): Boolean {
        return when (service<GeneralSettings>().state.selectedService) {
            ServiceType.CUSTOM_OPENAI,
            ServiceType.ANTHROPIC,
            ServiceType.AZURE,
            ServiceType.OLLAMA -> true

            ServiceType.CODEGPT -> {
                listOf(
                    "gpt-4o",
                    "gpt-4o-mini",
                    "gemini-pro-1.5",
                    "claude-3-opus",
                    "claude-3.5-sonnet"
                ).contains(
                    service<CodeGPTServiceSettings>()
                        .state
                        .chatCompletionSettings
                        .model
                )
            }

            ServiceType.OPENAI -> {
                listOf(
                    OpenAIChatCompletionModel.GPT_4_VISION_PREVIEW.code,
                    OpenAIChatCompletionModel.GPT_4_O.code,
                    OpenAIChatCompletionModel.GPT_4_O_MINI.code
                ).contains(service<OpenAISettings>().state.model)
            }

            else -> false
        }
    }
}

class WrapLayout(align: Int, hgap: Int, vgap: Int) : FlowLayout(align, hgap, vgap) {

    override fun preferredLayoutSize(target: Container): Dimension {
        return layoutSize(target, true)
    }

    override fun minimumLayoutSize(target: Container): Dimension {
        return layoutSize(target, false)
    }

    private fun layoutSize(target: Container, preferred: Boolean): Dimension {
        synchronized(target.treeLock) {
            val targetWidth = target.width
            var width = targetWidth
            if (targetWidth == 0) {
                width = Int.MAX_VALUE
            }

            val insets = target.insets
            val horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2)
            val maxWidth = width - horizontalInsetsAndGap

            val dim = Dimension(0, 0)
            var rowWidth = 0
            var rowHeight = 0

            for (i in 0 until target.componentCount) {
                val m = target.getComponent(i)
                if (m.isVisible) {
                    val d = if (preferred) m.preferredSize else m.minimumSize
                    if (rowWidth + d.width > maxWidth) {
                        addRow(dim, rowWidth, rowHeight)
                        rowWidth = 0
                        rowHeight = 0
                    }
                    if (rowWidth != 0) {
                        rowWidth += hgap
                    }
                    rowWidth += d.width
                    rowHeight = maxOf(rowHeight, d.height)
                }
            }
            addRow(dim, rowWidth, rowHeight)

            dim.width += horizontalInsetsAndGap
            dim.height += insets.top + insets.bottom + vgap * 2

            return dim
        }
    }

    private fun addRow(dim: Dimension, rowWidth: Int, rowHeight: Int) {
        dim.width = maxOf(dim.width, rowWidth)
        if (dim.height > 0) {
            dim.height += vgap
        }
        dim.height += rowHeight
    }
}