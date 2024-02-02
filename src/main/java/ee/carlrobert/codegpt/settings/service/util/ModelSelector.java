package ee.carlrobert.codegpt.settings.service.util;

import ee.carlrobert.llm.completion.CompletionModel;
import javax.swing.JComponent;

/**
 * Helper interface to indicate a Component holds a {@link CompletionModel} value
 */
public interface ModelSelector<T extends CompletionModel> {

  void setSelectedModel(T model);

  T getSelectedModel();

  JComponent getComponent();
}
