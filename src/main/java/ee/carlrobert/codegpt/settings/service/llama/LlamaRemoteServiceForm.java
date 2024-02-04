package ee.carlrobert.codegpt.settings.service.llama;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.util.RemoteServiceForm;
import ee.carlrobert.codegpt.settings.state.LlamaSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.ui.ChatPromptTemplatePanel;
import ee.carlrobert.codegpt.ui.InfillPromptTemplatePanel;
import java.util.List;

/**
 * Form containing fields for all {@link LlamaRemoteSettings}.
 */
public class LlamaRemoteServiceForm extends RemoteServiceForm<ApiKeyCredentials> {


  private ChatPromptTemplatePanel chatPromptTemplatePanel;
  private InfillPromptTemplatePanel infillPromptTemplatePanel;

  public LlamaRemoteServiceForm() {
    super(LlamaSettings.getInstance().getState().getRemoteSettings(), ServiceType.LLAMA_CPP);
  }

  @Override
  protected List<PanelBuilder> additionalServerConfigPanels() {
    chatPromptTemplatePanel = new ChatPromptTemplatePanel(
        LlamaSettings.getInstance().getState().getRemoteSettings().getChatPromptTemplate(), true);
    infillPromptTemplatePanel = new InfillPromptTemplatePanel(
        LlamaSettings.getInstance().getState().getRemoteSettings().getInfillPromptTemplate(), true);
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
    super.setSettings(settings);
    chatPromptTemplatePanel.setPromptTemplate(settings.getChatPromptTemplate());
  }

  public LlamaRemoteSettings getRemoteSettings() {
    return new LlamaRemoteSettings(
        chatPromptTemplatePanel.getPromptTemplate(),
        infillPromptTemplatePanel.getPromptTemplate(),
        baseHostField.getText(),
        new ApiKeyCredentials(getApiKey())
    );
  }

}
