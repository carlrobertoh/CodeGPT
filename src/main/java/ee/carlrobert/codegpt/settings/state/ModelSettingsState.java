package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_ModelSettings_210", storages = @Storage("CodeGPT_ModelSettings_210.xml"))
public class ModelSettingsState implements PersistentStateComponent<ModelSettingsState> {

  private String chatCompletionModel = OpenAIChatCompletionModel.GPT_3_5.getCode();

  public static ModelSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(ModelSettingsState.class);
  }

  @Override
  public ModelSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull ModelSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public String getChatCompletionModel() {
    return chatCompletionModel;
  }

  public void setChatCompletionModel(String chatCompletionModel) {
    this.chatCompletionModel = chatCompletionModel;
  }
}
