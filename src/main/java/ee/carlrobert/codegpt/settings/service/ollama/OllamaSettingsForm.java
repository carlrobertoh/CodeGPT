package ee.carlrobert.codegpt.settings.service.ollama;

import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.CodeCompletionConfigurationForm;

import javax.swing.*;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

public class OllamaSettingsForm extends JPanel {

    private final JBTextField hostField;
    private final JBTextField modelField;
    private final CodeCompletionConfigurationForm codeCompletionConfigurationForm;

    public OllamaSettingsForm(OllamaSettingsState settings) {
        hostField = new JBTextField();
        modelField = new JBTextField();
        codeCompletionConfigurationForm = new CodeCompletionConfigurationForm(
                settings.isCodeCompletionsEnabled(),
                settings.getCodeCompletionMaxTokens());

        init();
    }

    private void init() {
        add(FormBuilder.createFormBuilder()
                .addLabeledComponent(
                    CodeGPTBundle.get("settingsConfigurable.shared.baseHost.label"),
                    hostField
                )
                .addLabeledComponent(
                    CodeGPTBundle.get("settingsConfigurable.shared.model.label"),
                    modelField
                )
                .addSeparator()
                .addLabeledComponent(
                    CodeGPTBundle.get("shared.codeCompletions"),
                    withEmptyLeftBorder(codeCompletionConfigurationForm.getForm())
                )
                .getPanel());
    }

    public String getHost() {
        return hostField.getText();
    }
    public String getModel() {
        return modelField.getText();
    }

    public OllamaSettingsState getCurrentState() {
        var state = new OllamaSettingsState();
        state.setHost(getHost());
        state.setModel(getModel());
        state.setCodeCompletionsEnabled(codeCompletionConfigurationForm.isCodeCompletionsEnabled());
        state.setCodeCompletionMaxTokens(codeCompletionConfigurationForm.getMaxTokens());
        return state;
    }

    public void resetForm() {
        var state = OllamaSettings.getCurrentState();
        hostField.setText(state.getHost());
        modelField.setText(state.getModel());
        codeCompletionConfigurationForm.setCodeCompletionsEnabled(state.isCodeCompletionsEnabled());
        codeCompletionConfigurationForm.setMaxTokens(state.getCodeCompletionMaxTokens());
    }
}
