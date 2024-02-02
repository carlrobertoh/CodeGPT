package ee.carlrobert.codegpt.ui;

import javax.swing.JComponent;

/**
 * Helper interface for a JComponent that holds a string value
 */
public interface ComponentWithStringValue {
  abstract JComponent getComponent();
  abstract String getValue();

  abstract void setValue(String value);

}
