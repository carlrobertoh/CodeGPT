package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.YouCredentialsManager;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_YouSettings", storages = @Storage("CodeGPT_YouSettings.xml"))
public class YouSettingsState extends RemoteSettings<YouCredentialsManager> implements PersistentStateComponent<YouSettingsState> {

  private boolean displayWebSearchResults = true;
  private boolean useGPT4Model;

  public static YouSettingsState getInstance() {
    YouSettingsState settingsState = new YouSettingsState();
    settingsState.setCredentialsManager(new YouCredentialsManager());
    return settingsState;
  }

  public YouSettingsState() {
    super(null, null);
  }

  @Override
  public YouSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull YouSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isDisplayWebSearchResults() {
    return displayWebSearchResults;
  }

  public void setDisplayWebSearchResults(boolean displayWebSearchResults) {
    this.displayWebSearchResults = displayWebSearchResults;
  }

  public boolean isUseGPT4Model() {
    return useGPT4Model;
  }

  public void setUseGPT4Model(boolean useGPT4Model) {
    this.useGPT4Model = useGPT4Model;
  }
}
