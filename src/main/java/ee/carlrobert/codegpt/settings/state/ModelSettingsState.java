package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_ModelSettings", storages = @Storage("CodeGPT_ModelSettings.xml"))
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

  public void sync(Conversation conversation) {
    var settings = SettingsState.getInstance();
    var clientCode = conversation.getClientCode();

    settings.setUseOpenAIService("chat.completion".equals(clientCode));
    settings.setUseAzureService("azure.chat.completion".equals(clientCode));
    settings.setUseYouService("you.chat.completion".equals(clientCode));

    chatCompletionModel = conversation.getModel();
  }

  public String getCompletionModel() {
    return chatCompletionModel;
  }

  public String getChatCompletionModel() {
    return chatCompletionModel;
  }

  public void setChatCompletionModel(String chatCompletionModel) {
    this.chatCompletionModel = chatCompletionModel;
  }
}
