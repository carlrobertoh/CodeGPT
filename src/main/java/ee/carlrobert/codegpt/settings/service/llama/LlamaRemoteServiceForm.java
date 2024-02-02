package ee.carlrobert.codegpt.settings.service.llama;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.util.RemoteServiceForm;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.ui.ChatPromptTemplatePanel;
import ee.carlrobert.codegpt.ui.InfillPromptTemplatePanel;
import java.util.List;

/**
 * Form containing fields for all {@link LlamaRemoteSettings}
 */
public class LlamaRemoteServiceForm extends RemoteServiceForm<LlamaCredentialsManager> {


  private ChatPromptTemplatePanel chatPromptTemplatePanel;
  private InfillPromptTemplatePanel infillPromptTemplatePanel;

  public LlamaRemoteServiceForm() {
    super(LlamaSettingsState.getInstance().getRemoteSettings(), ServiceType.LLAMA_CPP);
  }

  @Override
  protected List<PanelBuilder> additionalServerConfigPanels() {
    chatPromptTemplatePanel = new ChatPromptTemplatePanel(
        LlamaSettingsState.getInstance().getRemoteSettings().getChatPromptTemplate(), true);
    infillPromptTemplatePanel = new InfillPromptTemplatePanel(
        LlamaSettingsState.getInstance().getRemoteSettings().getInfillPromptTemplate(), true);
    return List.of(
        UI.PanelFactory.panel(chatPromptTemplatePanel)
            .withLabel(CodeGPTBundle.get("shared.promptTemplate"))
            .withComment(
                CodeGPTBundle.get("settingsConfigurable.service.llama.promptTemplate.comment"))
            .resizeX(false),
        UI.PanelFactory.panel(infillPromptTemplatePanel)
            .withLabel(CodeGPTBundle.get("shared.infillPromptTemplate"))
            .withComment(
                CodeGPTBundle.get("settingsConfigurable.service.llama.promptTemplate.comment"))
            .resizeX(false));
  }

  public void setRemoteSettings(LlamaRemoteSettings settings) {
    super.setRemoteSettings(settings);
    chatPromptTemplatePanel.setPromptTemplate(settings.getChatPromptTemplate());
  }

  public LlamaRemoteSettings getRemoteSettings() {
    return new LlamaRemoteSettings(
        chatPromptTemplatePanel.getPromptTemplate(),
        infillPromptTemplatePanel.getPromptTemplate(),
        baseHostField.getText()
    );
  }

}
