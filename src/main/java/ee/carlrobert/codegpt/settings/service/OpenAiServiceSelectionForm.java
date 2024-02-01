package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.ui.UIUtil;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import javax.swing.JPanel;

public class OpenAiServiceSelectionForm extends FormBuilder {

  private final JBPasswordField openAIApiKeyField;
  private final JBTextField openAIBaseHostField;
  private final JBTextField openAIPathField;
  private final JBTextField openAIOrganizationField;
  private final ComboBox<OpenAIChatCompletionModel> openAICompletionModelComboBox;

  public OpenAiServiceSelectionForm() {

    var openAISettings = OpenAISettingsState.getInstance();
    openAIApiKeyField = new JBPasswordField();
    openAIApiKeyField.setColumns(30);
    openAIApiKeyField.setText(openAISettings.getCredentialsManager().getApiKey());

    openAIBaseHostField = new JBTextField(openAISettings.getBaseHost(), 30);
    openAIPathField = new JBTextField(openAISettings.getPath(), 30);
    openAIOrganizationField = new JBTextField(openAISettings.getOrganization(), 30);

    var selectedOpenAIModel = OpenAIChatCompletionModel.findByCode(openAISettings.getModel());

    openAICompletionModelComboBox = new ComboBox<>(
        new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
    openAICompletionModelComboBox.setSelectedItem(selectedOpenAIModel);

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

    addComponent(new TitledSeparator(
        CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")))
        .addComponent(withEmptyLeftBorder(apiKeyFieldPanel))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.requestConfiguration.title")))
        .addComponent(withEmptyLeftBorder(requestConfigurationPanel))
        .addComponentFillVertically(new JPanel(), 0);
  }

  public void setOpenAIApiKey(String apiKey) {
    openAIApiKeyField.setText(apiKey);
  }

  public String getOpenAIApiKey() {
    return new String(openAIApiKeyField.getPassword());
  }

  public void setOpenAIBaseHost(String baseHost) {
    openAIBaseHostField.setText(baseHost);
  }

  public String getOpenAIBaseHost() {
    return openAIBaseHostField.getText();
  }

  public void setOpenAIOrganization(String organization) {
    openAIOrganizationField.setText(organization);
  }

  public String getOpenAIOrganization() {
    return openAIOrganizationField.getText();
  }

  public void setOpenAIModel(String model) {
    openAICompletionModelComboBox.setSelectedItem(OpenAIChatCompletionModel.findByCode(model));
  }

  public String getOpenAIModel() {
    return ((OpenAIChatCompletionModel) (openAICompletionModelComboBox.getModel()
        .getSelectedItem()))
        .getCode();
  }

  public void setOpenAIPath(String path) {
    openAIPathField.setText(path);
  }

  public String getOpenAIPath() {
    return openAIPathField.getText();
  }

}
