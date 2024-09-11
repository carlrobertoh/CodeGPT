package ee.carlrobert.codegpt.toolwindow.chat.actionprocessor;

import ee.carlrobert.codegpt.ui.textarea.AppliedActionInlay;
import ee.carlrobert.codegpt.ui.textarea.AppliedCodeActionInlay;
import ee.carlrobert.codegpt.ui.textarea.AppliedSuggestionActionInlay;

public class ActionProcessorFactory {

  public static ActionProcessor getProcessor(AppliedActionInlay action) {
    if (action instanceof AppliedSuggestionActionInlay) {
      return new SuggestionActionProcessor();
    } else if (action instanceof AppliedCodeActionInlay) {
      return new CodeActionProcessor();
    }
    throw new IllegalArgumentException("Unknown action type");
  }
}
