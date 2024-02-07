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

public class OpenAISettingsForm {

  private final JBPasswordField openAIApiKeyField;
  private final JBTextField openAIBaseHostField;
  private final JBTextField openAIPathField;
  private final JBTextField openAIOrganizationField;
  private final ComboBox<OpenAIChatCompletionModel> openAICompletionModelComboBox;

  public OpenAISettingsForm(OpenAISettingsState settings) {
    openAIApiKeyField = new JBPasswordField();
    openAIApiKeyField.setColumns(30);
    openAIApiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
    openAIBaseHostField = new JBTextField(settings.getBaseHost(), 30);
    openAIPathField = new JBTextField(settings.getPath(), 30);
    openAIOrganizationField = new JBTextField(settings.getOrganization(), 30);
    openAICompletionModelComboBox = new ComboBox<>(
        new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
    openAICompletionModelComboBox.setSelectedItem(
        OpenAIChatCompletionModel.findByCode(settings.getModel()));
  }

  public JPanel getForm() {
    var requestConfigurationPanel = UI.PanelFactory.grid()
        .add(UI.PanelFactory.panel(openAICompletionModelComboBox)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.model.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(openAIOrganizationField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.openai.organization.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.openai.organization.comment")))
        .add(UI.PanelFactory.panel(openAIBaseHostField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.baseHost.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(openAIPathField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.path.label"))
            .resizeX(false))
        .createPanel();

    var apiKeyFieldPanel = UI.PanelFactory.panel(openAIApiKeyField)
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

  public String getApiKey() {
    return new String(openAIApiKeyField.getPassword());
  }

  public String getModel() {
    return ((OpenAIChatCompletionModel) (openAICompletionModelComboBox.getModel()
        .getSelectedItem()))
        .getCode();
  }

  public OpenAISettingsState getCurrentState() {
    var state = new OpenAISettingsState();
    state.setOrganization(openAIOrganizationField.getText());
    state.setBaseHost(openAIBaseHostField.getText());
    state.setPath(openAIPathField.getText());
    state.setModel(getModel());
    return state;
  }

  public void resetForm() {
    var state = OpenAISettings.getCurrentState();
    openAIApiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
    openAIOrganizationField.setText(state.getOrganization());
    openAIBaseHostField.setText(state.getBaseHost());
    openAIPathField.setText(state.getPath());
    openAICompletionModelComboBox.setSelectedItem(
        OpenAIChatCompletionModel.findByCode(state.getModel()));
  }
}
