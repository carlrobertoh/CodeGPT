package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import org.jetbrains.annotations.NotNull;

public class InlineCodeCompletionInitializer implements EditorFactoryListener {

  private static final Logger LOG = Logger.getInstance(InlineCodeCompletionInitializer.class);

  @Override
  public void editorCreated(@NotNull EditorFactoryEvent event) {
    Editor editor = event.getEditor();
    LOG.warn("Editor created");
    new InlineCodeCompletionRenderer(editor);
  }

  @Override
  public void editorReleased(@NotNull EditorFactoryEvent event) {
    LOG.warn("Editor released");
  }
}
