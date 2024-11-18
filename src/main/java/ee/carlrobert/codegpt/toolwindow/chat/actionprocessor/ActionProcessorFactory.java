package ee.carlrobert.codegpt.toolwindow.chat.actionprocessor;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.ui.textarea.AppliedActionInlay;
import ee.carlrobert.codegpt.ui.textarea.AppliedCodeActionInlay;
import ee.carlrobert.codegpt.ui.textarea.AppliedSuggestionActionInlay;

public class ActionProcessorFactory {

  public static ActionProcessor getProcessor(Project project, AppliedActionInlay action) {
    if (action instanceof AppliedSuggestionActionInlay) {
      return new SuggestionActionProcessor(project);
    } else if (action instanceof AppliedCodeActionInlay) {
      return new CodeActionProcessor();
    }
    throw new IllegalArgumentException("Unknown action type");
  }
}
