package ee.carlrobert.codegpt.state.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "ee.carlrobert.codegpt.state.settings.SettingsState",
    storages = @Storage("CodeGPTSettings_170.xml")
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

  public String apiKey = "";
  public boolean useApiKeyFromEnvVar = false;
  public boolean useOpenAIService = true;
  public boolean useAzureService;
  public boolean useCustomService;
  public String resourceName = "";
  public String deploymentId = "";
  public String apiVersion = "";
  public boolean useActiveDirectoryAuthentication;
  public String organization = "";
  public String customHost = "";
  public String displayName = getDisplayName();
  public boolean useOpenAIAccountName = true;
  public String textCompletionBaseModel = TextCompletionModel.DAVINCI.getCode();
  public String chatCompletionBaseModel = ChatCompletionModel.GPT_3_5.getCode();
  public String customChatCompletionModel = "";
  public String customTextCompletionModel = "";
  public boolean isChatCompletionOptionSelected = true;
  public boolean isTextCompletionOptionSelected;

  public static SettingsState getInstance() {
    return ApplicationManager.getApplication().getService(SettingsState.class);
  }

  @Nullable
  @Override
  public SettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull SettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void syncSettings(Conversation conversation) {
    var isChatCompletions = ClientCode.CHAT_COMPLETION.equals(conversation.getClientCode());
    var model = conversation.getModel();
    if (isChatCompletions) {
      if (useCustomService) {
        customChatCompletionModel = model;
      } else {
        chatCompletionBaseModel = model;
      }
    } else {
      if (useCustomService) {
        customTextCompletionModel = model;
      } else {
        textCompletionBaseModel = model;
      }
    }
    isChatCompletionOptionSelected = isChatCompletions;
    isTextCompletionOptionSelected = !isChatCompletions;
  }

  public String getApiKey() {
    if (useApiKeyFromEnvVar) {
      var envApiKey = System.getenv("OPENAI_API_KEY");
      return envApiKey == null ? "" : envApiKey;
    }
    return apiKey;
  }

  public String getChatCompletionModel() {
    if (useCustomService) {
      return customChatCompletionModel;
    }
    return chatCompletionBaseModel;
  }

  public String getTextCompletionModel() {
    if (useCustomService) {
      return customTextCompletionModel;
    }
    return textCompletionBaseModel;
  }

  private String getDisplayName() {
    var name = System.getProperty("user.name");
    return name == null || name.isEmpty() ? "User" : name;
  }
}
