package ee.carlrobert.codegpt.settings.state.llama.ollama;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;

import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.codegpt.settings.state.util.LlamaCompletionModelConverter;

/**
 * All settings for using an existing remote Llama server.
 */
public class OllamaRemoteSettings extends LlamaRemoteSettings {

  @OptionTag(converter = LlamaCompletionModelConverter.class)
  private LlamaCompletionModel model = HuggingFaceModel.CODE_LLAMA_7B_Q4;

  public OllamaRemoteSettings() {
    super();
  }

  public OllamaRemoteSettings(PromptTemplate chatPromptTemplate,
      InfillPromptTemplate infillPromptTemplate, String baseHost,
      LlamaCredentialsManager credentialsManager, LlamaCompletionModel model) {
    super(chatPromptTemplate, infillPromptTemplate, baseHost, credentialsManager);
    this.model = model;
  }

  @Transient
  public boolean isModified(OllamaRemoteSettings remoteSettings) {
    return super.isModified(remoteSettings)
        || areValuesDifferent(remoteSettings.getModel(), this.getModel());
  }

  public LlamaCompletionModel getModel() {
    return model;
  }

  public void setModel(LlamaCompletionModel model) {
    this.model = model;
  }
}
