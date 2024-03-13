package ee.carlrobert.codegpt.settings.service.openai;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.ui.UIUtil;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nullable;

public class OpenAISettingsForm {

  private final JBPasswordField apiKeyField;
  private final JBTextField organizationField;
  private final ComboBox<OpenAIChatCompletionModel> completionModelComboBox;

  public OpenAISettingsForm(OpenAISettingsState settings) {
    apiKeyField = new JBPasswordField();
    apiKeyField.setColumns(30);
    apiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
    organizationField = new JBTextField(settings.getOrganization(), 30);
    completionModelComboBox = new ComboBox<>(
        new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
    completionModelComboBox.setSelectedItem(
        OpenAIChatCompletionModel.findByCode(settings.getModel()));
  }

  public JPanel getForm() {
    var configurationGrid = UI.PanelFactory.grid()
        .add(UI.PanelFactory.panel(apiKeyField)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get("settingsConfigurable.service.openai.apiKey.comment"))
            .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked))
        .add(UI.PanelFactory.panel(organizationField)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.service.openai.organization.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.openai.organization.comment")))
        .add(UI.PanelFactory.panel(completionModelComboBox)
            .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.model.label"))
            .resizeX(false))
        .createPanel();

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(CodeGPTBundle.get("shared.configuration")))
        .addComponent(withEmptyLeftBorder(configurationGrid))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public @Nullable String getApiKey() {
    var apiKey = new String(apiKeyField.getPassword());
    return apiKey.isEmpty() ? null : apiKey;
  }

  public String getModel() {
    return ((OpenAIChatCompletionModel) (completionModelComboBox.getModel()
        .getSelectedItem()))
        .getCode();
  }

  public OpenAISettingsState getCurrentState() {
    var state = new OpenAISettingsState();
    state.setModel(getModel());
    state.setOrganization(organizationField.getText());
    return state;
  }

  public void resetForm() {
    var state = OpenAISettings.getCurrentState();
    apiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
    completionModelComboBox.setSelectedItem(
        OpenAIChatCompletionModel.findByCode(state.getModel()));
    organizationField.setText(state.getOrganization());
  }
}
