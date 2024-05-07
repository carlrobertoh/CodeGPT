package ee.carlrobert.codegpt.settings.service

import com.intellij.icons.AllIcons.General
import com.intellij.ide.HelpTooltip
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.panel.ComponentPanelBuilder
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.fields.IntegerField
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate
import org.apache.commons.text.StringEscapeUtils
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JPanel

class CodeCompletionConfigurationForm(
    codeCompletionsEnabled: Boolean,
    maxTokens: Int,
    fimTemplate: InfillPromptTemplate?
) {

    private val codeCompletionsEnabledCheckBox = JBCheckBox(
        CodeGPTBundle.get("codeCompletionsForm.enableFeatureText"),
        codeCompletionsEnabled
    )
    private val codeCompletionMaxTokensField =
        IntegerField("completion_max_tokens", 8, 4096).apply {
            columns = 12
            value = maxTokens
        }
    private val promptTemplateComboBox =
        ComboBox(EnumComboBoxModel(InfillPromptTemplate::class.java)).apply {
            item = fimTemplate
            addItemListener {
                updatePromptTemplateHelpTooltip(it.item as InfillPromptTemplate)
            }
        }
    private val promptTemplateHelpText = JBLabel(General.ContextHelp)

    fun getForm(): JPanel {
        val formBuilder = FormBuilder.createFormBuilder()
            .addComponent(codeCompletionsEnabledCheckBox)
            .addVerticalGap(4);
        if (fimTemplate != null) {
            formBuilder.addVerticalGap(4)
                .addLabeledComponent(
                    "FIM template:",
                    JPanel(FlowLayout(FlowLayout.LEADING, 0, 0)).apply {
                        add(promptTemplateComboBox)
                        add(Box.createHorizontalStrut(4))
                        add(promptTemplateHelpText)
                    })
        }
        return formBuilder.addLabeledComponent(
            CodeGPTBundle.get("codeCompletionsForm.maxTokensLabel"),
            codeCompletionMaxTokensField
        )
            .addComponentToRightColumn(
                ComponentPanelBuilder.createCommentComponent(
                    CodeGPTBundle.get("codeCompletionsForm.maxTokensComment"), true, 48, true
                )
            )
            .panel
    }

    var isCodeCompletionsEnabled: Boolean
        get() = codeCompletionsEnabledCheckBox.isSelected
        set(enabled) {
            codeCompletionsEnabledCheckBox.isSelected = enabled
        }

    var maxTokens: Int
        get() = codeCompletionMaxTokensField.value
        set(maxTokens) {
            codeCompletionMaxTokensField.value = maxTokens
        }

    var fimTemplate: InfillPromptTemplate?
        get() = promptTemplateComboBox.item
        set(template) {
            promptTemplateComboBox.item = template
        }

    private fun updatePromptTemplateHelpTooltip(template: InfillPromptTemplate) {
        promptTemplateHelpText.setToolTipText(null)

        val description = StringEscapeUtils.escapeHtml4(template.buildPrompt("PREFIX", "SUFFIX"))
        HelpTooltip()
            .setTitle(template.toString())
            .setDescription("<html><p>$description</p></html>")
            .installOn(promptTemplateHelpText)
    }
}
