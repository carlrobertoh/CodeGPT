package ee.carlrobert.codegpt.settings.service.util;

import static ee.carlrobert.codegpt.ui.UIUtil.createApiKeyPanel;

import com.google.common.collect.Lists;
import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.openapi.ui.panel.PanelGridBuilder;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.util.RemoteSettings;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * Form for all {@link RemoteSettings} fields (including apiKey from
 * {@link ApiKeyCredentialsManager}).
 */
public abstract class RemoteServiceForm<T extends ApiKeyCredentialsManager> extends FormBuilder {

  protected final JBTextField baseHostField;
  protected final JBTextField pathField;
  protected final JBPasswordField apiKeyField;

  private final RemoteSettings<T> remoteSettings;
  protected final ServiceType serviceType;

  private final PanelGridBuilder serverConfigurationPanelBuilder;
  private final PanelGridBuilder authenticationPanels;

  public RemoteServiceForm(RemoteSettings<T> remoteSettings, ServiceType serviceType) {
    this.remoteSettings = remoteSettings;
    this.serviceType = serviceType;

    apiKeyField = new JBPasswordField();
    baseHostField = new JBTextField(remoteSettings.getBaseHost(), 30);
    pathField = new JBTextField(remoteSettings.getPath(), 30);

    serverConfigurationPanelBuilder = UI.PanelFactory.grid()
        .add(UI.PanelFactory.panel(baseHostField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.baseHost.label"))
            .resizeX(false));
    if (remoteSettings.getPath() != null) {
      serverConfigurationPanelBuilder.add(UI.PanelFactory.panel(pathField)
          .withLabel(CodeGPTBundle.get(
              "settingsConfigurable.shared.path.label"))
          .resizeX(false));
    }
    authenticationPanels = UI.PanelFactory.grid();
  }

  @Override
  public JPanel getPanel() {
    addComponent(new TitledSeparator(
        CodeGPTBundle.get("settingsConfigurable.service.serverConfig.title")));
    additionalServerConfigPanels().forEach(serverConfigurationPanelBuilder::add);
    addComponent(serverConfigurationPanelBuilder.createPanel());
    addComponent(new TitledSeparator(
        CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")));
    authenticationComponents().forEach(authenticationPanels::add);
    addComponent(authenticationPanels.createPanel());
    addComponentFillVertically(new JPanel(), 0);
    return super.getPanel();
  }

  protected List<PanelBuilder> authenticationComponents() {
    return Lists.newArrayList(
        createApiKeyPanel(remoteSettings.getCredentialsManager().getApiKey(), apiKeyField));
  }

  protected List<PanelBuilder> additionalServerConfigPanels() {
    return new ArrayList<>();
  }

  public void setApiKey(String apiKey) {
    apiKeyField.setText(apiKey);
  }

  public String getApiKey() {
    return new String(apiKeyField.getPassword());
  }

  protected void setBaseHost(String baseHost) {
    baseHostField.setText(baseHost);
  }

  protected String getBaseHost() {
    return baseHostField.getText();
  }

  protected void setPath(String path) {
    pathField.setText(path);
  }

  protected String getPath() {
    return pathField.getText();
  }

  public RemoteSettings<T> getRemoteSettings() {
    return new RemoteSettings<>(getBaseHost(),
        getPath(), remoteSettings.getCredentialsManager());
  }

  public void setRemoteSettings(RemoteSettings<T> remoteSettings) {
    setBaseHost(remoteSettings.getBaseHost());
    setPath(remoteSettings.getPath());
    setApiKey(remoteSettings.getCredentialsManager().getApiKey());
  }

}
