package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import ee.carlrobert.llm.client.openai.completion.text.OpenAITextCompletionModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_ModelSettings", storages = @Storage("CodeGPT_ModelSettings.xml"))
public class ModelSettingsState implements PersistentStateComponent<ModelSettingsState> {

  private String textCompletionModel = OpenAITextCompletionModel.DAVINCI.getCode();
  private String chatCompletionModel = OpenAIChatCompletionModel.GPT_3_5.getCode();
  private boolean useChatCompletion = true;
  private boolean useTextCompletion;

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
    var isChatCompletion = "chat.completion".equals(conversation.getClientCode());
    if (isChatCompletion) {
      chatCompletionModel = conversation.getModel();
    } else {
      textCompletionModel = conversation.getModel();
    }
    useChatCompletion = isChatCompletion;
    useTextCompletion = !isChatCompletion;
  }

  public String getCompletionModel() {
    return useChatCompletion ? chatCompletionModel : textCompletionModel;
  }

  public String getTextCompletionModel() {
    return textCompletionModel;
  }

  public void setTextCompletionModel(String textCompletionModel) {
    this.textCompletionModel = textCompletionModel;
  }

  public String getChatCompletionModel() {
    return chatCompletionModel;
  }

  public void setChatCompletionModel(String chatCompletionModel) {
    this.chatCompletionModel = chatCompletionModel;
  }

  public boolean isUseChatCompletion() {
    return useChatCompletion;
  }

  public void setUseChatCompletion(boolean useChatCompletion) {
    this.useChatCompletion = useChatCompletion;
  }

  public boolean isUseTextCompletion() {
    return useTextCompletion;
  }

  public void setUseTextCompletion(boolean useTextCompletion) {
    this.useTextCompletion = useTextCompletion;
  }
}
