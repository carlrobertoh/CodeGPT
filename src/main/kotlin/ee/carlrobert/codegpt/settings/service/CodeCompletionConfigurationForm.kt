package ee.carlrobert.codegpt.settings.service

import com.intellij.openapi.ui.panel.ComponentPanelBuilder
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.fields.IntegerField
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import javax.swing.JPanel

class CodeCompletionConfigurationForm(codeCompletionsEnabled: Boolean, maxTokens: Int) {

    private val codeCompletionsEnabledCheckBox = JBCheckBox(
        CodeGPTBundle.get("codeCompletionsForm.enableFeatureText"),
        codeCompletionsEnabled
    )
    private val codeCompletionMaxTokensField =
        IntegerField("completion_max_tokens", 8, 4096).apply {
            columns = 12
            value = maxTokens
        }

    fun getForm(): JPanel {
        return FormBuilder.createFormBuilder()
            .addComponent(codeCompletionsEnabledCheckBox)
            .addVerticalGap(4)
            .addLabeledComponent(
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
}
