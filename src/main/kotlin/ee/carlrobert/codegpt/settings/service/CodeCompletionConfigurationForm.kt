package ee.carlrobert.codegpt.settings.service

import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import javax.swing.JPanel

class CodeCompletionConfigurationForm(
    codeCompletionsEnabled: Boolean
) {

    private val codeCompletionsEnabledCheckBox = JBCheckBox(
        CodeGPTBundle.get("codeCompletionsForm.enableFeatureText"),
        codeCompletionsEnabled
    )

    fun getForm(): JPanel {
        val formBuilder = FormBuilder.createFormBuilder()
            .addComponent(codeCompletionsEnabledCheckBox)
        return formBuilder.panel
    }

    var isCodeCompletionsEnabled: Boolean
        get() = codeCompletionsEnabledCheckBox.isSelected
        set(enabled) {
            codeCompletionsEnabledCheckBox.isSelected = enabled
        }
}
