package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.openai.OpenAiModelSelector;
import ee.carlrobert.codegpt.settings.service.util.RemoteOpenAiServiceForm;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import java.util.List;

/**
 * Form containing all forms to configure using
 * {@link ee.carlrobert.codegpt.settings.service.ServiceType#OPENAI}.
 */

public class OpenAiServiceForm extends
    RemoteOpenAiServiceForm<OpenAICredentialsManager> {

  private JBTextField organizationField;

  public OpenAiServiceForm() {
    super(OpenAISettingsState.getInstance(), ServiceType.OPENAI, new OpenAiModelSelector());
  }

  @Override
  protected List<PanelBuilder> additionalServerConfigPanels() {
    var openAISettings = OpenAISettingsState.getInstance();
    organizationField = new JBTextField(openAISettings.getOrganization(), 30);
    List<PanelBuilder> panels = super.additionalServerConfigPanels();
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

}
