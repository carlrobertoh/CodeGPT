package ee.carlrobert.codegpt.ide.settings.configuration;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.ide.action.ActionsUtil;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "ee.carlrobert.codegpt.ide.settings.configuration.ConfigurationState",
    storages = @Storage("CodeGPTConfiguration.xml")
)
public class ConfigurationState implements PersistentStateComponent<ConfigurationState> {

  public Map<String, String> tableData = ActionsUtil.DEFAULT_ACTIONS;

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
