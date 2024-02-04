package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import com.intellij.ui.components.JBCheckBox;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.settings.state.YouSettings;

public class YouProCheckbox extends JBCheckBox {

  public YouProCheckbox() {
    super(CodeGPTBundle.get("toolwindow.chat.youProCheckBox.text"));
    var youSettings = YouSettings.getInstance().getState();
    var youUserManager = YouUserManager.getInstance();
    setOpaque(false);
    setEnabled(youUserManager.isSubscribed());
    setSelected(youSettings.isUseGPT4Model());
    setToolTipText(getTooltipText(youUserManager, isSelected()));
    addChangeListener(e -> {
      var selected = ((JBCheckBox) e.getSource()).isSelected();
      setToolTipText(getTooltipText(youUserManager, selected));
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
