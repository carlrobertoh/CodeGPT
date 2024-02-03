package ee.carlrobert.codegpt.settings.state.util;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;

import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.credentials.CredentialsManager;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;

/**
 * Settings for using a remote service with model selection.
 */
public class RemoteOpenAiSettings<T extends CredentialsManager> extends
    RemoteSettings<T> {

  @OptionTag(converter = OpenAiModelConverter.class)
  protected OpenAIChatCompletionModel model;

  public RemoteOpenAiSettings() {
  }

  public RemoteOpenAiSettings(String baseHost, String path, OpenAIChatCompletionModel model,
      T credentialsManager) {
    this.baseHost = baseHost;
    this.path = path;
    this.model = model;
    this.credentialsManager = credentialsManager;
  }

  public OpenAIChatCompletionModel getModel() {
    return model;
  }

  public void setModel(OpenAIChatCompletionModel model) {
    this.model = model;
  }

  @Transient
  public boolean isModified(RemoteOpenAiSettings<T> remoteSettings) {
    return super.isModified(remoteSettings)
        || areValuesDifferent(remoteSettings.getModel(), this.getModel());
  }

}
