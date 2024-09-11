package ee.carlrobert.codegpt.toolwindow.chat.actionprocessor;

import com.intellij.openapi.editor.Editor;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.ui.textarea.AppliedActionInlay;
import ee.carlrobert.codegpt.ui.textarea.AppliedSuggestionActionInlay;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.CreateDocumentationActionItem;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.DocumentationActionItem;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.GitCommitActionItem;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.PersonaActionItem;
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.WebSearchActionItem;

public class SuggestionActionProcessor implements ActionProcessor {

  @Override
  public void process(Message message, AppliedActionInlay action, Editor editor,
      StringBuilder promptBuilder) {
    if (!(action instanceof AppliedSuggestionActionInlay suggestionAction)) {
      throw new IllegalArgumentException("Invalid action type");
    }
    processSuggestionAction(message, suggestionAction, editor, promptBuilder);
  }

  private void processSuggestionAction(
      Message message,
      AppliedSuggestionActionInlay action,
      Editor editor,
      StringBuilder promptBuilder) {
    message.setWebSearchIncluded(action.getSuggestion() instanceof WebSearchActionItem);
    processDocumentationAction(message, action, editor);
    processPersonaAction(message, action, editor);
    processGitCommitAction(action, promptBuilder);
  }

  private void processDocumentationAction(Message message, AppliedSuggestionActionInlay action,
      Editor editor) {
    var project = editor.getProject();
    var addedDocumentation = CodeGPTKeys.ADDED_DOCUMENTATION.get(project);
    var appliedInlayExists = action.getSuggestion() instanceof DocumentationActionItem
        || action.getSuggestion() instanceof CreateDocumentationActionItem;

    if (addedDocumentation != null && appliedInlayExists) {
      message.setDocumentationDetails(addedDocumentation);
      CodeGPTKeys.ADDED_DOCUMENTATION.set(project, null);
    }
  }

  private void processPersonaAction(Message message, AppliedSuggestionActionInlay action,
      Editor editor) {
    var project = editor.getProject();
    var addedPersona = CodeGPTKeys.ADDED_PERSONA.get(project);
    var personaInlayExists = action.getSuggestion() instanceof PersonaActionItem;
    if (addedPersona != null && personaInlayExists) {
      message.setPersonaDetails(addedPersona);
      CodeGPTKeys.ADDED_PERSONA.set(project, null);
    }
  }

  private void processGitCommitAction(
      AppliedSuggestionActionInlay action,
      StringBuilder promptBuilder) {
    if (action.getSuggestion() instanceof GitCommitActionItem gitCommitActionItem) {
      promptBuilder
          .append("\n```shell\n")
          .append(gitCommitActionItem.getDiffString())
          .append("\n```\n");
    }
  }
}
