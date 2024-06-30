package ee.carlrobert.codegpt.ui

import com.intellij.ide.IdeBundle
import com.intellij.ide.ui.laf.darcula.ui.DarculaButtonUI
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.SelectionEvent
import com.intellij.openapi.editor.event.SelectionListener
import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.util.MinimizeButton
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.ComponentPredicate
import com.intellij.util.ui.AsyncProcessIcon
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.actions.editor.EditCodeSubmissionHandler
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType.CODEGPT
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.ModelComboBoxAction
import ee.carlrobert.codegpt.util.ApplicationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

data class ObservableProperties(
    val accepted: AtomicBooleanProperty = AtomicBooleanProperty(false),
    val loading: AtomicBooleanProperty = AtomicBooleanProperty(false),
)

class EditCodePopover(private val editor: Editor) {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val observableProperties = ObservableProperties()
    private val submissionHandler = EditCodeSubmissionHandler(editor, observableProperties)
    private val promptTextField = JBTextField("", 40).apply {
        emptyText.appendText(CodeGPTBundle.get("editCodePopover.textField.emptyText"))
    }
    private val popup = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(
            createPopupPanel(),
            promptTextField
        )
        .setTitle(CodeGPTBundle.get("editCodePopover.title"))
        .setMovable(true)
        .setCancelKeyEnabled(true)
        .setCancelOnClickOutside(false)
        .setCancelOnWindowDeactivation(false)
        .setRequestFocus(true)
        .setCancelButton(MinimizeButton(IdeBundle.message("tooltip.hide")))
        .setCancelCallback {
            submissionHandler.handleReject()
            true
        }
        .createPopup()

    fun show() {
        popup.showInBestPositionFor(editor)
    }

    private fun createPopupPanel(): JPanel {
        val followUpButton =
            JButton(CodeGPTBundle.get("editCodePopover.followUpButton.title")).apply {
                isVisible = false
                isEnabled = false
                putClientProperty(DarculaButtonUI.DEFAULT_STYLE_KEY, true)
            }
        val acceptButton = JButton(CodeGPTBundle.get("editCodePopover.acceptButton.title")).apply {
            isVisible = false
            isEnabled = false
            addActionListener {
                submissionHandler.handleAccept()
                popup.cancel()
            }
        }
        val discardLink = ActionLink(CodeGPTBundle.get("shared.discard")) {
            submissionHandler.handleReject()
            popup.cancel()
        }.apply {
            isVisible = false
        }
        val spinner = AsyncProcessIcon("edit_code_spinner").apply {
            isVisible = observableProperties.loading.get()
        }
        val submitButton = JButton(CodeGPTBundle.get("editCodePopover.submitButton.title")).apply {
            isEnabled = promptTextField.text.isNotEmpty() && editor.selectionModel.hasSelection()
            putClientProperty(DarculaButtonUI.DEFAULT_STYLE_KEY, true)
        }
        submitButton.addActionListener {
            submitButton.isVisible = false
            followUpButton.isVisible = true
            acceptButton.isVisible = true
            discardLink.isVisible = true

            serviceScope.launch {
                submissionHandler.handleSubmit(
                    promptTextField.text,
                    followUpButton,
                    acceptButton,
                    spinner
                )
                promptTextField.text = ""
                promptTextField.emptyText.text =
                    CodeGPTBundle.get("editCodePopover.textField.followUp.emptyText")
            }
        }
        followUpButton.addActionListener {
            serviceScope.launch {
                submissionHandler.handleSubmit(
                    promptTextField.text,
                    followUpButton,
                    acceptButton,
                    spinner
                )
            }
        }
        promptTextField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                val textNotEmpty = e.document.getText(
                    e.document.startPosition.offset,
                    e.document.endPosition.offset
                ).isNotEmpty()
                runInEdt {
                    submitButton.isEnabled = textNotEmpty && editor.selectionModel.hasSelection()
                    followUpButton.isEnabled = textNotEmpty && editor.selectionModel.hasSelection()
                }
            }
        })
        editor.selectionModel.addSelectionListener(object : SelectionListener {
            override fun selectionChanged(e: SelectionEvent) {
                runInEdt {
                    submitButton.isEnabled =
                        e.editor.selectionModel.hasSelection() && promptTextField.text.isNotEmpty()
                    followUpButton.isEnabled =
                        e.editor.selectionModel.hasSelection() && promptTextField.text.isNotEmpty()
                }
            }
        })

        return panel {
            row {
                cell(promptTextField)
            }
            row {
                comment(CodeGPTBundle.get("editCodePopover.textField.comment"))
            }
            row {
                cell(submitButton)
                cell(followUpButton)
                cell(acceptButton)
                cell(spinner)
                cell(discardLink).align(AlignX.RIGHT)
            }
            separator()
            row {
                text(CodeGPTBundle.get("editCodePopover.cancel.helpText"))
                    .applyToComponent {
                        font = JBUI.Fonts.smallFont()
                    }
                cell(
                    ModelComboBoxAction(
                        ApplicationUtil.findCurrentProject(),
                        {},
                        GeneralSettings.getSelectedService(),
                        listOf(CODEGPT)
                    )
                        .createCustomComponent(ActionPlaces.UNKNOWN)
                ).align(AlignX.RIGHT)
            }
        }.apply {
            border = JBUI.Borders.empty(8, 8, 2, 8)
        }
    }

    private class EnabledButtonComponentPredicate(
        private val button: JButton,
        private val editor: Editor,
        private val promptTextField: JBTextField,
        private val observableProperties: ObservableProperties
    ) : ComponentPredicate() {
        override fun invoke(): Boolean {
            if (!editor.selectionModel.hasSelection()) {
                button.toolTipText = "Please select code to continue"
            }
            if (promptTextField.text.isEmpty()) {
                button.toolTipText = "Please enter a prompt to continue"
            }

            return editor.selectionModel.hasSelection()
                    && promptTextField.text.isNotEmpty()
                    && observableProperties.loading.get().not()
        }

        override fun addListener(listener: (Boolean) -> Unit) {
            promptTextField.document.addDocumentListener(object : DocumentAdapter() {
                override fun textChanged(e: DocumentEvent) {
                    runInEdt { listener(invoke()) }
                }
            })
            editor.selectionModel.addSelectionListener(object : SelectionListener {
                override fun selectionChanged(e: SelectionEvent) {
                    runInEdt { listener(invoke()) }
                }
            })
            observableProperties.loading.afterSet {
                runInEdt { listener(invoke()) }
            }
        }
    }
}