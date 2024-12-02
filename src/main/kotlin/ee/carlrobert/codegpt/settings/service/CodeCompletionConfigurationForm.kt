package ee.carlrobert.codegpt.settings.service

import com.intellij.icons.AllIcons.General
import com.intellij.ide.HelpTooltip
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate
import ee.carlrobert.codegpt.codecompletions.InfillRequest
import org.apache.commons.text.StringEscapeUtils
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.JPanel

class CodeCompletionConfigurationForm(
    codeCompletionsEnabled: Boolean,
    fimOverride: Boolean?,
    fimTemplate: InfillPromptTemplate?
) {

    private val codeCompletionsEnabledCheckBox = JBCheckBox(
        CodeGPTBundle.get("codeCompletionsForm.enableFeatureText"),
        codeCompletionsEnabled
    )
    private val promptTemplateComboBox =
        ComboBox(EnumComboBoxModel(InfillPromptTemplate::class.java)).apply {
            item = fimTemplate
            isEnabled = fimOverride == null || fimOverride == false
            addItemListener {
                updatePromptTemplateHelpTooltip(it.item as InfillPromptTemplate)
            }
        }
    private val promptTemplateHelpText = JBLabel(General.ContextHelp)
    private val fimOverrideCheckbox = JBCheckBox(
        CodeGPTBundle.get("codeCompletionsForm.overrideFimTemplate.label"),
        fimOverride ?: false
    )

    fun getForm(): JPanel {
        val formBuilder = FormBuilder.createFormBuilder()
            .addComponent(codeCompletionsEnabledCheckBox)
        if (fimTemplate != null) {
            formBuilder.addComponent(panel {
                row {
                    cell(fimOverrideCheckbox)
                        .visible(fimOverride != null)
                        .onChanged { promptTemplateComboBox.isEnabled = !it.isSelected }
                        .comment(CodeGPTBundle.get("codeCompletionsForm.overrideFimTemplate.description"))
                }
            })
            formBuilder.addVerticalGap(4)
                .addLabeledComponent(
                    CodeGPTBundle.get("codeCompletionsForm.selectFimTemplate"),
                    JPanel(FlowLayout(FlowLayout.LEADING, 0, 0)).apply {
                        add(promptTemplateComboBox)
                        add(Box.createHorizontalStrut(4))
                        add(promptTemplateHelpText)
                    })
        }
        return formBuilder.panel
    }

    var isCodeCompletionsEnabled: Boolean
        get() = codeCompletionsEnabledCheckBox.isSelected
        set(enabled) {
            codeCompletionsEnabledCheckBox.isSelected = enabled
        }

    var fimTemplate: InfillPromptTemplate?
        get() = promptTemplateComboBox.item
        set(template) {
            promptTemplateComboBox.item = template
        }

    var fimOverride: Boolean?
        get() = fimOverrideCheckbox.isSelected
        set(value) {
            if (value != null) {
                fimOverrideCheckbox.isSelected = value
            }
        }

    private fun updatePromptTemplateHelpTooltip(template: InfillPromptTemplate) {
        promptTemplateHelpText.setToolTipText(null)

        val description = StringEscapeUtils.escapeHtml4(
            template.buildPrompt(InfillRequest.Builder("PREFIX", "SUFFIX", 0).build())
        )
        HelpTooltip()
            .setTitle(template.toString())
            .setDescription("<html><p>$description</p></html>")
            .installOn(promptTemplateHelpText)
    }
}
