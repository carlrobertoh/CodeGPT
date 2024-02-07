package ee.carlrobert.codegpt.settings.service.util;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.google.common.collect.Lists;
import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.util.RemoteSettings;
import java.util.List;
import javax.swing.JPanel;

/**
 * Form for all {@link RemoteSettings} fields.
 */
public abstract class RemoteServiceForm extends FormBuilder {

  protected final JBTextField baseHostField;
  protected final JBTextField pathField;
  protected final RemoteSettings remoteSettings;
  protected final ServiceType serviceType;


  public RemoteServiceForm(RemoteSettings remoteSettings, ServiceType serviceType) {
    this.remoteSettings = remoteSettings;
    this.serviceType = serviceType;

    baseHostField = new JBTextField(remoteSettings.getBaseHost(), 35);
    pathField = new JBTextField(remoteSettings.getPath(), 35);
  }

  @Override
  public JPanel getPanel() {
    addPanels();
    return super.getPanel();
  }

  protected void addPanels() {
    addAuthenticationPanel();
    addRequestConfigurationPanel();
    addComponentFillVertically(new JPanel(), 0);
  }

  protected void addAuthenticationPanel() {
    addComponent(new TitledSeparator(
        CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")));
    var authenticationPanel = UI.PanelFactory.grid();
    authenticationPanels().forEach(authenticationPanel::add);
    addComponent(withEmptyLeftBorder(authenticationPanel.createPanel()));
  }

  protected void addRequestConfigurationPanel() {
    addComponent(new TitledSeparator(
        CodeGPTBundle.get("settingsConfigurable.service.requestConfiguration.title")));
    var requestConfigPanel = UI.PanelFactory.grid();
    requestConfigurationPanels().forEach(requestConfigPanel::add);
    addComponent(withEmptyLeftBorder(requestConfigPanel.createPanel()));
  }

  protected List<PanelBuilder> authenticationPanels() {
    return Lists.newArrayList();
  }

  protected List<PanelBuilder> requestConfigurationPanels() {
    List<PanelBuilder> panels = Lists.newArrayList(UI.PanelFactory.panel(baseHostField)
        .withLabel(CodeGPTBundle.get(
            "settingsConfigurable.shared.baseHost.label"))
        .resizeX(false));
    if (remoteSettings.getPath() != null) {
      panels.add(UI.PanelFactory.panel(pathField)
          .withLabel(CodeGPTBundle.get(
              "settingsConfigurable.shared.path.label"))
          .resizeX(false));
    }
    return panels;
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

  public RemoteSettings getSettings() {
    return new RemoteSettings(getBaseHost(), getPath());
  }

  public void setSettings(RemoteSettings remoteSettings) {
    setBaseHost(remoteSettings.getBaseHost());
    setPath(remoteSettings.getPath());
  }

}
