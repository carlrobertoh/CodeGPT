package ee.carlrobert.codegpt.settings.configuration;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "ee.carlrobert.codegpt.state.settings.configuration.ConfigurationState",
    storages = @Storage("CodeGPTConfiguration.xml")
)
public class ConfigurationState implements PersistentStateComponent<ConfigurationState> {

  public String systemPrompt = "";
  public int maxTokens = 1000;
  public double temperature = 0.2;
  public boolean createNewChatOnEachAction;

  public Map<String, String> tableData = EditorActionsUtil.DEFAULT_ACTIONS;

  public static ConfigurationState getInstance() {
    return ApplicationManager.getApplication().getService(ConfigurationState.class);
  }

  @Nullable
  @Override
  public ConfigurationState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull ConfigurationState state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}
