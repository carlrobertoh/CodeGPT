package ee.carlrobert.codegpt.settings.prompts.form.details

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.observable.util.not
import com.intellij.ui.CardLayoutPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.selected
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.util.ui.components.BorderLayoutPanel
import ee.carlrobert.codegpt.settings.prompts.PersonasState.Companion.DEFAULT_PERSONA_PROMPT
import ee.carlrobert.codegpt.settings.prompts.PromptsSettings
import javax.swing.JComponent
import javax.swing.JPanel

class PersonasDetailsPanel(onSelected: (PersonaPromptDetails) -> Unit) : PromptDetailsPanel {

    private val cardLayoutPanel =
        object : CardLayoutPanel<PersonaPromptDetails, PersonaPromptDetails, JComponent>() {
            override fun prepare(key: PersonaPromptDetails): PersonaPromptDetails = key

            override fun create(state: PersonaPromptDetails): JComponent {
                return PersonaEditorPanel(state, onSelected).getPanel()
            }
        }

    init {
        service<PromptsSettings>().state.personas.prompts.forEach {
            cardLayoutPanel.select(PersonaPromptDetails(it), true)
        }
    }

    override fun getPanel() = cardLayoutPanel

    override fun updateData(details: FormPromptDetails) {
        if (details !is PersonaPromptDetails) {
            return
        }

        cardLayoutPanel.select(details, true)
    }

    override fun remove(details: FormPromptDetails) {
        if (details !is PersonaPromptDetails) {
            return
        }

        cardLayoutPanel.remove(cardLayoutPanel.getValue(details, false))
    }

    private class PersonaEditorPanel(
        private val details: PersonaPromptDetails,
        private val onSelected: (PersonaPromptDetails) -> Unit
    ) : AbstractEditorPromptPanel(details) {

        private val nameField = JBTextField(details.name).apply {
            isEnabled = details.id != 1L
        }

        override fun getPanel(): JPanel = panel {
            row {
                cell(BorderLayoutPanel().addToTop(editor.component))
                    .align(Align.FILL)
                    .resizableColumn()
            }
            row {
                link("Revert to default") {
                    updateEditorText(DEFAULT_PERSONA_PROMPT)
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
                    return details.id == 1L && DEFAULT_PERSONA_PROMPT != editor.document.text
                }
            })
            row {
                comment(
                    "Set of instructions that serve as the starting point when starting a new chat session. This defines things for the model and helps to focus its capabilities.",
                    60
                )
            }
            row {
                cell(nameField)
                    .label("Persona name:")
                    .align(Align.FILL)
                    .onChanged { details.name = it.text }
            }
            row {
                checkBox("Disable persona")
                    .selected(details.disabled)
                    .comment("If checked, the selected persona will not be used when making chat queries. This is particularly useful when the model does not support system prompt.")
                    .onChanged { details.disabled = it.isSelected }
            }
            row {
                button("Set as Default") {
                    onSelected(details)
                }.enabledIf(details.selected.not())
            }.topGap(TopGap.SMALL)
        }
    }
}