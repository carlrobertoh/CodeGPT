package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import org.jetbrains.annotations.NotNull;

public class CodeGPTEditorListener implements EditorFactoryListener {

  @Override
  public void editorCreated(@NotNull EditorFactoryEvent event) {
    var manager = CodeCompletionListenerManager.getInstance();
    Editor editor = event.getEditor();

    if (manager.hasBinder(editor)) {
      manager.removeBinder(editor);
    }
    var binder = new CodeCompletionListenerBinder(editor);
    manager.setBinder(editor, binder);
    EditorUtil.disposeWithEditor(editor, binder);
  }

  @Override
  public void editorReleased(@NotNull EditorFactoryEvent event) {
    var manager = CodeCompletionListenerManager.getInstance();
    Editor editor = event.getEditor();
    if (manager.hasBinder(editor)) {
      manager.removeBinder(editor);
    }
  }

}
