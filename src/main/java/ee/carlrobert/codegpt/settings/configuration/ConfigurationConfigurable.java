package ee.carlrobert.codegpt.settings.configuration;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class ConfigurationConfigurable implements Configurable {

  private Disposable parentDisposable;

  private ConfigurationComponent component;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return CodeGPTBundle.get("configurationConfigurable.displayName");
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    parentDisposable = Disposer.newDisposable();
    component = new ConfigurationComponent(
        parentDisposable,
        ConfigurationSettings.getCurrentState());
    return component.getPanel();
  }

  @Override
  public boolean isModified() {
    return !component.getCurrentFormState()
        .equals(ConfigurationSettings.getCurrentState());
  }

  @Override
  public void apply() {
    ConfigurationSettings.getInstance().loadState(component.getCurrentFormState());
  }

  @Override
  public void reset() {
    component.resetForm();
  }

  @Override
  public void disposeUIResources() {
    if (parentDisposable != null) {
      Disposer.dispose(parentDisposable);
    }
    component = null;
  }
}
