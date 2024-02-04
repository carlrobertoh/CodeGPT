package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
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
public class OpenAIServiceForm extends RemoteServiceForm<ApiKeyCredentials> {

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

  public void setOrganization(String organization) {
    organizationField.setText(organization);
  }

  public String getOrganization() {
    return organizationField.getText();
  }

  public OpenAISettingsState getSettings() {
    return new OpenAISettingsState(getBaseHost(), getPath(), getModel(),
        new ApiKeyCredentials(getApiKey()),
        getOrganization(), false
    );
  }

  public void setSettings(OpenAISettingsState settings) {
    super.setSettings(settings);
    modelSelector.setSelectedModel(settings.getModel());
    organizationField.setText(settings.getOrganization());
  }

  public void setModel(OpenAIChatCompletionModel model) {
    modelSelector.setSelectedModel(model);
  }

  public OpenAIChatCompletionModel getModel() {
    return modelSelector.getSelectedModel();
  }

}
