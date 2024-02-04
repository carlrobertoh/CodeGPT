package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.settings.state.azure.AzureSettingsState;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_AzureSettings_210", storages = @Storage("CodeGPT_AzureSettings_210.xml"))
public class AzureSettings implements PersistentStateComponent<AzureSettingsState> {

  private final AzureSettingsState state;

  public AzureSettings() {
    this.state = new AzureSettingsState();
  }

  public static AzureSettingsState getInstance() {
    return ApplicationManager.getApplication()
        .getService(AzureSettings.class).getState();
  }

  @Override
  public AzureSettingsState getState() {
    return this.state;
  }

  @Override
  public void loadState(@NotNull AzureSettingsState state) {
    XmlSerializerUtil.copyBean(state, this.state);
  }
}
