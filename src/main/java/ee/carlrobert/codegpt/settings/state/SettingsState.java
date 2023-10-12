package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.conversations.Conversation;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_GeneralSettings_210", storages = @Storage("CodeGPT_GeneralSettings_210.xml"))
public class SettingsState implements PersistentStateComponent<SettingsState> {

  private String email = "";
  private String displayName = "";
  private boolean previouslySignedIn;
  private boolean useOpenAIService = true;
  private boolean useAzureService;
  private boolean useYouService;
  private boolean useLlamaService;
  private String llamaModelPath = "";

  public SettingsState() {
  }

  public static SettingsState getInstance() {
    return ApplicationManager.getApplication().getService(SettingsState.class);
  }

  @Override
  public SettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull SettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void sync(Conversation conversation) {
    var clientCode = conversation.getClientCode();
    if ("chat.completion".equals(clientCode)) {
      OpenAISettingsState.getInstance().setModel(conversation.getModel());
    }
    if ("azure.chat.completion".equals(clientCode)) {
      AzureSettingsState.getInstance().setModel(conversation.getModel());
    }

    setUseOpenAIService("chat.completion".equals(clientCode));
    setUseAzureService("azure.chat.completion".equals(clientCode));
    setUseYouService("you.chat.completion".equals(clientCode));
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public boolean isPreviouslySignedIn() {
    return previouslySignedIn;
  }

  public void setPreviouslySignedIn(boolean previouslySignedIn) {
    this.previouslySignedIn = previouslySignedIn;
  }

  public boolean isUseOpenAIService() {
    return useOpenAIService;
  }

  public void setUseOpenAIService(boolean useOpenAIService) {
    this.useOpenAIService = useOpenAIService;
  }

  public boolean isUseAzureService() {
    return useAzureService;
  }

  public void setUseAzureService(boolean useAzureService) {
    this.useAzureService = useAzureService;
  }

  public boolean isUseYouService() {
    return useYouService;
  }

  public void setUseYouService(boolean useYouService) {
    this.useYouService = useYouService;
  }

  public boolean isUseLlamaService() {
    return useLlamaService;
  }

  public void setUseLlamaService(boolean useLlamaService) {
    this.useLlamaService = useLlamaService;
  }

  public String getLlamaModelPath() {
    return llamaModelPath;
  }

  public void setLlamaModelPath(String llamaModelPath) {
    this.llamaModelPath = llamaModelPath;
  }
}
