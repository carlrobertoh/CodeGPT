package ee.carlrobert.codegpt.settings.service.openai;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.DocumentAdapter;
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
import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import org.jetbrains.annotations.NotNull;

public class OpenAISettingsForm {

  private final JBPasswordField apiKeyField;
  private final JBTextField customModelField;
  private final JBTextField baseHostField;
  private final JBTextField pathField;
  private final JBTextField organizationField;
  private final ComboBox<OpenAIChatCompletionModel> completionModelComboBox;

  public OpenAISettingsForm(OpenAISettingsState settings) {
    apiKeyField = new JBPasswordField();
    apiKeyField.setColumns(30);
    apiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
    completionModelComboBox = new ComboBox<>(
        new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
    completionModelComboBox.setEnabled(settings.getCustomModel().isEmpty());
    completionModelComboBox.setSelectedItem(
        OpenAIChatCompletionModel.findByCode(settings.getModel()));
    customModelField = new JBTextField(settings.getCustomModel(), 20);
    customModelField.getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent e) {
        completionModelComboBox.setEnabled(customModelField.getText().isEmpty());
      }
    });
    baseHostField = new JBTextField(settings.getBaseHost(), 30);
    pathField = new JBTextField(settings.getPath(), 30);
    organizationField = new JBTextField(settings.getOrganization(), 30);
  }

  public JPanel getForm() {
    var requestConfigurationPanel = UI.PanelFactory.grid()
        .add(UI.PanelFactory.panel(completionModelComboBox)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.model.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(customModelField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.openai.customModel.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(organizationField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.openai.organization.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.openai.organization.comment")))
        .add(UI.PanelFactory.panel(baseHostField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.baseHost.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(pathField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.path.label"))
            .resizeX(false))
        .createPanel();

    var apiKeyFieldPanel = UI.PanelFactory.panel(apiKeyField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
        .resizeX(false)
        .withComment(
            CodeGPTBundle.get("settingsConfigurable.service.openai.apiKey.comment"))
        .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked)
        .createPanel();

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")))
        .addComponent(withEmptyLeftBorder(apiKeyFieldPanel))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.requestConfiguration.title")))
        .addComponent(withEmptyLeftBorder(requestConfigurationPanel))
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
    state.setCustomModel(customModelField.getText());
    state.setOrganization(organizationField.getText());
    state.setBaseHost(baseHostField.getText());
    state.setPath(pathField.getText());
    return state;
  }

  public void resetForm() {
    var state = OpenAISettings.getCurrentState();
    apiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
    completionModelComboBox.setSelectedItem(
        OpenAIChatCompletionModel.findByCode(state.getModel()));
    customModelField.setText(state.getCustomModel());
    organizationField.setText(state.getOrganization());
    baseHostField.setText(state.getBaseHost());
    pathField.setText(state.getPath());
  }
}
