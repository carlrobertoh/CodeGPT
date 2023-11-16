package ee.carlrobert.codegpt.toolwindow.chat.components;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.YouModelChangeNotifier;
import org.jetbrains.annotations.NotNull;

public class YouProCheckbox extends JBCheckBox {

  public YouProCheckbox(@NotNull Project project) {
    super(CodeGPTBundle.get("toolwindow.chat.youProCheckBox.text"));
    var youSettings = YouSettingsState.getInstance();
    var youUserManager = YouUserManager.getInstance();
    setOpaque(false);
    setEnabled(youUserManager.isSubscribed());
    setSelected(youSettings.isUseGPT4Model());
    setToolTipText(getTooltipText(youUserManager, isSelected()));
    addChangeListener(e -> {
      var selected = ((JBCheckBox) e.getSource()).isSelected();
      setToolTipText(getTooltipText(youUserManager, selected));
      // TODO: Remove
      project.getMessageBus()
          .syncPublisher(YouModelChangeNotifier.YOU_MODEL_CHANGE_NOTIFIER_TOPIC)
          .modelChanged(selected);
      youSettings.setUseGPT4Model(selected);
    });
  }

  private String getTooltipText(YouUserManager youUserManager, boolean selected) {
    if (youUserManager.isSubscribed()) {
      return selected
          ? CodeGPTBundle.get("toolwindow.chat.youProCheckBox.disable")
          : CodeGPTBundle.get("toolwindow.chat.youProCheckBox.enable");
    }
    return CodeGPTBundle.get("toolwindow.chat.youProCheckBox.notAllowed");
  }
}
