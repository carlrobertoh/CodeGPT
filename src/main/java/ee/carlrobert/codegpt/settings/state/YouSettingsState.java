package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_YouSettings", storages = @Storage("CodeGPT_YouSettings.xml"))
public class YouSettingsState implements PersistentStateComponent<YouSettingsState> {

  private boolean displayWebSearchResults = true;
  private boolean useGPT4Model;
  private String baseHost;

  public static YouSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(YouSettingsState.class);
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

  public void setBaseHost(String baseHost) {
    this.baseHost = baseHost;
  }

  public String getBaseHost() {
    return baseHost;
  }
}
