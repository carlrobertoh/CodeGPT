package ee.carlrobert.codegpt.settings.service.llama;

import javax.swing.JComponent;

public interface ComponentWithValue<T> {

  T getValue();

  void setValue(T value);

  JComponent getComponent();
}
