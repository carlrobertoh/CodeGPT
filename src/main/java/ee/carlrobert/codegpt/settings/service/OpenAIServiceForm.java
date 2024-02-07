package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.codegpt.ui.UIUtil.createApiKeyPanel;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
import ee.carlrobert.codegpt.credentials.managers.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.openai.OpenAiModelSelector;
import ee.carlrobert.codegpt.settings.service.util.ModelSelector;
import ee.carlrobert.codegpt.settings.service.util.RemoteServiceForm;
import ee.carlrobert.codegpt.settings.state.OpenAISettings;
import ee.carlrobert.codegpt.settings.state.openai.OpenAISettingsState;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.util.List;

/**
 * Form containing all forms to configure using
 * {@link ee.carlrobert.codegpt.settings.service.ServiceType#OPENAI}.
 */
public class OpenAIServiceForm extends RemoteServiceForm {

  private JBTextField organizationField;
  private final ModelSelector<OpenAIChatCompletionModel> modelSelector;


  public OpenAIServiceForm() {
    super(OpenAISettings.getInstance().getState(), ServiceType.OPENAI);
    this.modelSelector = new OpenAiModelSelector();
    this.modelSelector.setSelectedModel(OpenAISettings.getInstance().getState().getModel());
  }

  @Override
  protected List<PanelBuilder> additionalServerConfigPanels() {
    var openAISettings = OpenAISettings.getInstance().getState();
    organizationField = new JBTextField(openAISettings.getOrganization(), 30);
    List<PanelBuilder> panels = super.additionalServerConfigPanels();
    panels.add(UI.PanelFactory.panel(this.modelSelector.getComponent())
        .withLabel(CodeGPTBundle.get(
            "settingsConfigurable.shared.model.label"))
        .resizeX(false));
    panels.add(
        UI.PanelFactory.panel(organizationField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.openai.organization.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.openai.organization.comment")));
    return panels;
  }

  @Override
  protected List<PanelBuilder> authenticationComponents() {
    List<PanelBuilder> panels = super.authenticationComponents();
    panels.add(
        createApiKeyPanel(OpenAICredentialsManager.getInstance().getCredentials().getApiKey(),
            apiKeyField,
        "settingsConfigurable.service.openai.apiKey.comment"));
    return panels;
  }

  public void setCredentials(ApiKeyCredentials credentials) {
    setApiKey(credentials.getApiKey());
  }

  public ApiKeyCredentials getCredentials() {
    return new ApiKeyCredentials(getApiKey());
  }

  public OpenAISettingsState getSettings() {
    return new OpenAISettingsState(getBaseHost(), getPath(), modelSelector.getSelectedModel(),
        organizationField.getText(), false);
  }

  public void setSettings(OpenAISettingsState settings) {
    super.setSettings(settings);
    modelSelector.setSelectedModel(settings.getModel());
    organizationField.setText(settings.getOrganization());
  }

}
