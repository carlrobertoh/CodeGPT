package ee.carlrobert.codegpt.settings.service.util;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.util.OpenAiRemoteSettings;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.util.List;

/**
 * Form for all {@link OpenAiRemoteSettings} fields (including apiKey from
 * {@link ApiKeyCredentialsManager}).
 */
public abstract class RemoteOpenAiServiceForm<T extends ApiKeyCredentialsManager> extends
    RemoteServiceForm<T> {

  protected final ModelSelector<OpenAIChatCompletionModel> modelSelector;

  protected final OpenAiRemoteSettings<T> remoteSettings;

  public RemoteOpenAiServiceForm(
      OpenAiRemoteSettings<T> remoteSettings,
      ServiceType serviceType, ModelSelector<OpenAIChatCompletionModel> modelSelector) {
    super(remoteSettings, serviceType);
    this.remoteSettings = remoteSettings;
    this.modelSelector = modelSelector;
    this.modelSelector.setSelectedModel(remoteSettings.getModel());
  }


  protected List<PanelBuilder> additionalServerConfigPanels() {
    List<PanelBuilder> panels = super.additionalServerConfigPanels();
    panels.add(UI.PanelFactory.panel(this.modelSelector.getComponent())
        .withLabel(CodeGPTBundle.get(
            "settingsConfigurable.shared.model.label"))
        .resizeX(false));
    return panels;
  }

  public void setModel(OpenAIChatCompletionModel model) {
    modelSelector.setSelectedModel(model);
  }

  public OpenAIChatCompletionModel getModel() {
    return modelSelector.getSelectedModel();
  }


  public OpenAiRemoteSettings<T> getRemoteWithModelSettings() {
    return new OpenAiRemoteSettings<>(getBaseHost(),
        getPath(), getModel(), remoteSettings.getCredentialsManager());
  }

  public void setRemoteWithModelSettings(OpenAiRemoteSettings<T> remoteSettings) {
    setRemoteSettings(remoteSettings);
    setModel(remoteSettings.getModel());
  }

}
