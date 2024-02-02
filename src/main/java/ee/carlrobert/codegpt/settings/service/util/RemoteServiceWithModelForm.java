package ee.carlrobert.codegpt.settings.service.util;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.util.RemoteSettings;
import ee.carlrobert.codegpt.settings.state.util.RemoteWithModelSettings;
import ee.carlrobert.llm.completion.CompletionModel;
import java.util.List;
/**
 * Form for all {@link RemoteWithModelSettings} fields (including apiKey from
 * {@link ApiKeyCredentialsManager})
 */
public abstract class RemoteServiceWithModelForm<T extends ApiKeyCredentialsManager, M extends CompletionModel> extends
    RemoteServiceForm<T> {

  protected final ModelSelector<M> modelSelector;

  protected final RemoteWithModelSettings<T, M> remoteSettings;

  public RemoteServiceWithModelForm(
      RemoteWithModelSettings<T, M> remoteSettings,
      ServiceType serviceType, ModelSelector<M> modelSelector) {
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

  public void setModel(M model) {
    modelSelector.setSelectedModel(model);
  }

  public M getModel() {
    return (M) modelSelector.getSelectedModel();
  }


  public RemoteWithModelSettings<T, M> getRemoteWithModelSettings() {
    return new RemoteWithModelSettings<>(getBaseHost(),
        getPath(), getModel(), remoteSettings.getCredentialsManager());
  }

  public void setRemoteWithModelSettings(RemoteWithModelSettings<T, M> remoteSettings) {
    setRemoteSettings(remoteSettings);
    setModel(remoteSettings.getModel());
  }

}
