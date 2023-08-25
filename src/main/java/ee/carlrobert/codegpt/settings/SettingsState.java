package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.openai.client.ClientCode;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "ee.carlrobert.codegpt.state.settings.SettingsState",
    storages = @Storage("CodeGPTSettings_210.xml")
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

  public String email = "";
  public boolean previouslySignedIn;
  public boolean useOpenAIService = true;
  public boolean useAzureService;
  public String azureResourceName = "";
  public String azureDeploymentId = "";
  public String azureApiVersion = "";
  public String azureBaseHost = "https://%s.openai.azure.com";
  public boolean useAzureApiKeyAuthentication = true;
  public boolean useAzureActiveDirectoryAuthentication;
  public String openAIOrganization = "";
  public String openAIBaseHost = "https://api.openai.com";
  public String displayName = "";
  public String textCompletionBaseModel = TextCompletionModel.DAVINCI.getCode();
  public String chatCompletionBaseModel = ChatCompletionModel.GPT_3_5.getCode();
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
      chatCompletionBaseModel = model;
    } else {
      textCompletionBaseModel = model;
    }
    isChatCompletionOptionSelected = isChatCompletions;
    isTextCompletionOptionSelected = !isChatCompletions;
  }

  public String getChatCompletionModel() {
    return chatCompletionBaseModel;
  }

  public String getTextCompletionModel() {
    return textCompletionBaseModel;
  }

  public String getDisplayName() {
    if (displayName == null || displayName.isEmpty()) {
      var systemUserName = System.getProperty("user.name");
      if (systemUserName == null || systemUserName.isEmpty()) {
        return "User";
      }
      return systemUserName;
    }
    return displayName;
  }
}
