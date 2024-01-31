package ee.carlrobert.codegpt.ui;

import javax.swing.JComponent;

public interface ComponentWithStringValue {
  abstract JComponent getComponent();
  abstract String getValue();

  abstract void setValue(String value);

}
