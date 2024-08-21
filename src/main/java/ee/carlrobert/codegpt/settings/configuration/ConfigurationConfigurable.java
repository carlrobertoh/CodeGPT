package ee.carlrobert.codegpt.settings.configuration;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
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
        ConfigurationSettings.getState());
    return component.getPanel();
  }

  @Override
  public boolean isModified() {
    return !component.getCurrentFormState().equals(ConfigurationSettings.getState());
  }

  @Override
  public void apply() {
    ApplicationManager.getApplication().getService(ConfigurationSettings.class)
        .loadState(component.getCurrentFormState());
    EditorActionsUtil.refreshActions();
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
