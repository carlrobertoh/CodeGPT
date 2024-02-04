package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.PasswordCredentials;
import ee.carlrobert.codegpt.credentials.manager.YouCredentialsManager;
import ee.carlrobert.codegpt.settings.state.you.YouSettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_YouSettings", storages = @Storage("CodeGPT_YouSettings.xml"))
public class YouSettings implements PersistentStateComponent<YouSettingsState> {

  private final YouSettingsState state;

  public YouSettings() {
    this.state = new YouSettingsState();
  }

  public static YouSettings getInstance() {
    return ApplicationManager.getApplication()
        .getService(YouSettings.class);
  }

  @Override
  public void loadState(@NotNull YouSettingsState state) {
    XmlSerializerUtil.copyBean(state, this.state);
    this.state.setCredentials(YouCredentialsManager.getInstance().getCredentials());
  }

  @Override
  @NotNull
  public YouSettingsState getState() {
    return this.state;
  }

  public void apply(PasswordCredentials credentials) {
    this.state.setCredentials(credentials);
    YouCredentialsManager.getInstance().apply(credentials);
  }
}
