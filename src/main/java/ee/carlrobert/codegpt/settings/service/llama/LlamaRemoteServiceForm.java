package ee.carlrobert.codegpt.settings.service.llama;

import static ee.carlrobert.codegpt.ui.UIUtil.createApiKeyPanel;
import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
import ee.carlrobert.codegpt.credentials.managers.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.util.RemoteServiceForm;
import ee.carlrobert.codegpt.settings.state.LlamaSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.ui.ChatPromptTemplatePanel;
import ee.carlrobert.codegpt.ui.InfillPromptTemplatePanel;
import java.util.List;
import javax.swing.JPanel;

/**
 * Form containing fields for all {@link LlamaRemoteSettings}.
 */
public class LlamaRemoteServiceForm extends RemoteServiceForm {

  private ChatPromptTemplatePanel chatPromptTemplatePanel;
  private InfillPromptTemplatePanel infillPromptTemplatePanel;
  private final JBPasswordField apiKeyField;


  public LlamaRemoteServiceForm() {
    super(LlamaSettings.getInstance().getState().getRemoteSettings(), ServiceType.LLAMA_CPP);
    this.apiKeyField = new JBPasswordField();
  }

  @Override
  public void addPanels() {
    addRequestConfigurationPanel();
    addAuthenticationPanel();
    addComponentFillVertically(new JPanel(), 0);
  }

  @Override
  protected void addRequestConfigurationPanel() {
    var requestConfigPanel = UI.PanelFactory.grid();
    requestConfigurationPanels().forEach(requestConfigPanel::add);
    addComponent(withEmptyLeftBorder(requestConfigPanel.createPanel()));
  }

  @Override
  protected List<PanelBuilder> requestConfigurationPanels() {
    chatPromptTemplatePanel = new ChatPromptTemplatePanel(
        LlamaSettings.getInstance().getState().getRemoteSettings().getChatPromptTemplate(), true);
    infillPromptTemplatePanel = new InfillPromptTemplatePanel(
        LlamaSettings.getInstance().getState().getRemoteSettings().getInfillPromptTemplate(), true);
    List<PanelBuilder> panels = super.requestConfigurationPanels();
    panels.addAll(List.of(
        UI.PanelFactory.panel(chatPromptTemplatePanel)
            .withLabel(CodeGPTBundle.get("shared.promptTemplate"))
            .withComment(
                CodeGPTBundle.get("settingsConfigurable.service.llama.promptTemplate.comment"))
            .resizeX(false),
        UI.PanelFactory.panel(infillPromptTemplatePanel)
            .withLabel(CodeGPTBundle.get("shared.infillPromptTemplate"))
            .withComment(
                CodeGPTBundle.get("settingsConfigurable.service.llama.promptTemplate.comment"))
            .resizeX(false)));
    return panels;
  }

  @Override
  protected List<PanelBuilder> authenticationPanels() {
    List<PanelBuilder> panels = super.authenticationPanels();
    panels.add(createApiKeyPanel(LlamaCredentialsManager.getInstance().getCredentials().getApiKey(),
        apiKeyField, "settingsConfigurable.shared.apiKey.comment"));
    return panels;
  }

  public void setRemoteSettings(LlamaRemoteSettings settings) {
    super.setSettings(settings);
    chatPromptTemplatePanel.setPromptTemplate(settings.getChatPromptTemplate());
    infillPromptTemplatePanel.setPromptTemplate(settings.getInfillPromptTemplate());
  }

  public LlamaRemoteSettings getRemoteSettings() {
    return new LlamaRemoteSettings(
        chatPromptTemplatePanel.getPromptTemplate(),
        infillPromptTemplatePanel.getPromptTemplate(),
        baseHostField.getText()
    );
  }

  public ApiKeyCredentials getCredentials() {
    return new ApiKeyCredentials(new String(apiKeyField.getPassword()));
  }

  public void setCredentials(ApiKeyCredentials credentials) {
    apiKeyField.setText(credentials.getApiKey());
  }

}
