package ee.carlrobert.codegpt.settings.service.llama;

import com.intellij.openapi.ui.panel.PanelBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.service.util.RemoteServiceForm;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.ui.PromptTemplateField;
import java.util.List;

/**
 * Form containing fields for all {@link LlamaRemoteSettings}
 */
public class LlamaRemoteServiceForm extends RemoteServiceForm<LlamaCredentialsManager> {


  private PromptTemplateField promptTemplateField;

  public LlamaRemoteServiceForm() {
    super(LlamaSettingsState.getInstance().getRemoteSettings(), ServiceType.LLAMA);
  }

  @Override
  protected List<PanelBuilder> additionalServerConfigPanels() {
    promptTemplateField = new PromptTemplateField(
        LlamaSettingsState.getInstance().getRemoteSettings().getPromptTemplate(), true);
    return List.of(
        UI.PanelFactory.panel(promptTemplateField)
            .withLabel(CodeGPTBundle.get("shared.promptTemplate"))
            .withComment(
                CodeGPTBundle.get("settingsConfigurable.service.llama.promptTemplate.comment"))
            .resizeX(false));
  }

  public void setRemoteSettings(LlamaRemoteSettings settings) {
    super.setRemoteSettings(settings);
    promptTemplateField.setPromptTemplate(settings.getPromptTemplate());
  }

  public LlamaRemoteSettings getRemoteSettings() {
    return new LlamaRemoteSettings(
        promptTemplateField.getPromptTemplate(),
        baseHostField.getText()
    );
  }

}
