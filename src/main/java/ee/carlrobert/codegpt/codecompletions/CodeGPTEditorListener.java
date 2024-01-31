package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import org.jetbrains.annotations.NotNull;

public class CodeGPTEditorListener implements EditorFactoryListener {

  @Override
  public void editorCreated(@NotNull EditorFactoryEvent event) {
    Editor editor = event.getEditor();
    EditorUtil.disposeWithEditor(editor, new CodeCompletionListenerBinder(editor));
  }
}
