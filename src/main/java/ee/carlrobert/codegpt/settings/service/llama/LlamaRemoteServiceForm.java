package ee.carlrobert.codegpt.settings.service.llama;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.util.RemoteServiceForm;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.ui.ChatPromptTemplatePanel;
import ee.carlrobert.codegpt.ui.InfillPromptTemplatePanel;
import java.util.ArrayList;
import java.util.List;

/**
 * Form containing fields for all {@link LlamaRemoteSettings}.
 */
public abstract class LlamaRemoteServiceForm<T extends LlamaRemoteSettings> extends
    RemoteServiceForm<LlamaCredentialsManager> {

  protected final T remoteSettings;
  protected ChatPromptTemplatePanel chatPromptTemplatePanel;
  protected InfillPromptTemplatePanel infillPromptTemplatePanel;

  public LlamaRemoteServiceForm(T remoteSettings, ServiceType serviceType) {
    super(remoteSettings, serviceType);
    this.remoteSettings = remoteSettings;
  }

  @Override
  protected List<PanelBuilder> additionalServerConfigPanels() {
    chatPromptTemplatePanel = new ChatPromptTemplatePanel(remoteSettings.getChatPromptTemplate(),
        true);
    infillPromptTemplatePanel = new InfillPromptTemplatePanel(
        remoteSettings.getInfillPromptTemplate(), true);
    return new ArrayList<>(List.of(
        UI.PanelFactory.panel(chatPromptTemplatePanel)
            .withLabel(CodeGPTBundle.get("shared.promptTemplate"))
            .withComment(
                CodeGPTBundle.get("settingsConfigurable.service.llama.promptTemplate.comment"))
            .resizeX(false),
        UI.PanelFactory.panel(infillPromptTemplatePanel)
            .withLabel(CodeGPTBundle.get("shared.infillPromptTemplate"))
            .withComment(
                CodeGPTBundle.get("settingsConfigurable.service.llama.infillTemplate.comment"))
            .resizeX(false)));
  }

  public void setRemoteSettings(T settings) {
    super.setRemoteSettings(settings);
    if (chatPromptTemplatePanel != null && infillPromptTemplatePanel != null) {
      chatPromptTemplatePanel.setPromptTemplate(settings.getChatPromptTemplate());
      infillPromptTemplatePanel.setPromptTemplate(settings.getInfillPromptTemplate());
    }
  }

  public abstract T getRemoteSettings();

}
