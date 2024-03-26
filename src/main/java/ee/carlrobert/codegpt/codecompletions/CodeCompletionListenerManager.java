package ee.carlrobert.codegpt.codecompletions;
import com.intellij.openapi.editor.Editor;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;

import java.util.HashMap;
import java.util.Map;

public class CodeCompletionListenerManager {
  private static CodeCompletionListenerManager instance;
  private final Map<Editor, CodeCompletionListenerBinder> binders;

  private CodeCompletionListenerManager() {
    binders = new HashMap<>();
  }

  public static CodeCompletionListenerManager getInstance() {
    if (instance == null) {
      instance = new CodeCompletionListenerManager();
    }
    return instance;
  }

  public CodeCompletionListenerBinder getBinder(Editor editor) {
    return binders.get(editor);
  }

  public void setBinder(Editor editor, CodeCompletionListenerBinder binder) {
    binders.put(editor, binder);
  }

  public void removeBinder(Editor editor) {
    binders.remove(editor);
  }

  public boolean hasBinder(Editor editor) {
    return binders.containsKey(editor);
  }

  public void triggerCompletions(Editor editor) {
    var project = editor.getProject();
    var binder = this.getBinder(editor);
    if (project != null && binder != null) {
      if (!ConfigurationSettings.getCurrentState().isCodeCompletionsEnabled()) {
        binder.addListeners();
      }
      var caretOffset = editor.getCaretModel().getOffset();
      var codeCompletionService = CodeCompletionService.getInstance(project);
      codeCompletionService.triggerCompletions(editor, caretOffset, CodeCompletionTriggerType.MANUAL);
    }
  }
}
