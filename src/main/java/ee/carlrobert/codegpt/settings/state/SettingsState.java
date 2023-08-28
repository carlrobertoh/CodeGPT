package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(
    name = "ee.carlrobert.codegpt.state.settings.SettingsState",
    storages = @Storage("CodeGPTSettings_210.xml")
)
public class SettingsState implements PersistentStateComponent<SettingsState> {

  private String email = "";
  private String displayName = "";
  private boolean previouslySignedIn;
  private boolean useOpenAIService = true;
  private boolean useAzureService;
  private boolean useCustomService;

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

  public boolean isUseCustomService() {
    return useCustomService;
  }

  public void setUseCustomService(boolean useCustomService) {
    this.useCustomService = useCustomService;
  }
}
