package ee.carlrobert.codegpt.settings.prompts.form.details

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.ui.CardLayoutPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.util.ui.components.BorderLayoutPanel
import ee.carlrobert.codegpt.settings.prompts.ChatActionsState.Companion.DEFAULT_EXPLAIN_PROMPT
import ee.carlrobert.codegpt.settings.prompts.ChatActionsState.Companion.DEFAULT_FIND_BUGS_PROMPT
import ee.carlrobert.codegpt.settings.prompts.ChatActionsState.Companion.DEFAULT_OPTIMIZE_PROMPT
import ee.carlrobert.codegpt.settings.prompts.ChatActionsState.Companion.DEFAULT_REFACTOR_PROMPT
import ee.carlrobert.codegpt.settings.prompts.ChatActionsState.Companion.DEFAULT_WRITE_TESTS_PROMPT
import ee.carlrobert.codegpt.settings.prompts.PromptsSettings
import javax.swing.JComponent
import javax.swing.JPanel

class ChatActionsDetailsPanel : PromptDetailsPanel {

    private val cardLayoutPanel =
        object : CardLayoutPanel<ChatActionPromptDetails, ChatActionPromptDetails, JComponent>() {
            override fun prepare(key: ChatActionPromptDetails) = key

            override fun create(state: ChatActionPromptDetails): JComponent {
                val defaultInstructions = when (state.code) {
                    "FIND_BUGS" -> DEFAULT_FIND_BUGS_PROMPT
                    "WRITE_TESTS" -> DEFAULT_WRITE_TESTS_PROMPT
                    "EXPLAIN" -> DEFAULT_EXPLAIN_PROMPT
                    "REFACTOR" -> DEFAULT_REFACTOR_PROMPT
                    "OPTIMIZE" -> DEFAULT_OPTIMIZE_PROMPT
                    else -> null
                }

                return ChatActionEditorPanel(state, defaultInstructions).getPanel()
            }
        }

    init {
        service<PromptsSettings>().state.chatActions.prompts.forEach {
            cardLayoutPanel.select(ChatActionPromptDetails(it), true)
        }
    }

    override fun getPanel() = cardLayoutPanel

    override fun updateData(details: FormPromptDetails) {
        if (details !is ChatActionPromptDetails) {
            return
        }

        cardLayoutPanel.select(details, true)
    }

    override fun remove(details: FormPromptDetails) {
        if (details !is ChatActionPromptDetails) {
            return
        }

        cardLayoutPanel.remove(cardLayoutPanel.getValue(details, false))
    }

    private class ChatActionEditorPanel(
        private val details: ChatActionPromptDetails,
        private val defaultInstructions: String? = "",
    ) : AbstractEditorPromptPanel(details, listOf("{SELECTION}")) {

        private val nameField = JBTextField(details.name)
        private val startInNewChatCheckBox = JBCheckBox(
            "Start in a new chat window",
            service<PromptsSettings>().state.chatActions.startInNewWindow
        )

        override fun getPanel(): JPanel = panel {
            row {
                cell(BorderLayoutPanel().addToTop(editor.component)).align(Align.FILL)
                    .resizableColumn()
            }
            row {
                link("Revert to default") {
                    updateEditorText(defaultInstructions)
                }
            }.visibleIf(object : ComponentPredicate() {
                override fun addListener(listener: (Boolean) -> Unit) {
                    editor.document.addDocumentListener(object : DocumentListener {
                        override fun documentChanged(event: DocumentEvent) {
                            listener(invoke())
                        }
                    })
                }

                override fun invoke(): Boolean {
                    if (defaultInstructions == null) {
                        return false
                    }
                    return defaultInstructions != editor.document.text
                }
            })
            row {
                cell(nameField)
                    .label("Action name:")
                    .align(Align.FILL)
                    .onChanged { details.name = it.text }
            }
            row {
                cell(startInNewChatCheckBox)
                    .comment(
                        "If not checked, the previous chat history will be sent along with each action",
                        60
                    )
                    .onChanged {
                        service<PromptsSettings>().state
                            .chatActions
                            .startInNewWindow = it.isSelected
                    }
            }
        }
    }
}