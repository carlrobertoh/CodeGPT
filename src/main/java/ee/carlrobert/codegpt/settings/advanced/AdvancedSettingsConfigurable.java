package ee.carlrobert.codegpt.settings.advanced;

import com.intellij.openapi.options.Configurable;
import ee.carlrobert.codegpt.CodeGPTBundle;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class AdvancedSettingsConfigurable implements Configurable {

  private AdvancedSettingsComponent advancedSettingsComponent;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return CodeGPTBundle.get("advancedSettingsConfigurable.displayName");
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return new AdvancedSettingsComponent(AdvancedSettings.getCurrentState()).getPanel();
  }

  @Override
  public boolean isModified() {
    return !advancedSettingsComponent.getCurrentFormState()
        .equals(AdvancedSettings.getCurrentState());
  }

  @Override
  public void apply() {
    AdvancedSettings.getInstance().loadState(advancedSettingsComponent.getCurrentFormState());
  }

  @Override
  public void reset() {
    advancedSettingsComponent.resetForm();
  }

  @Override
  public void disposeUIResources() {
    advancedSettingsComponent = null;
  }
}
