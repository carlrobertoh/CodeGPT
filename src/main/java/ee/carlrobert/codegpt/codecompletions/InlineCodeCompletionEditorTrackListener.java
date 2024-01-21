package ee.carlrobert.codegpt.codecompletions;

import static java.util.stream.Collectors.toList;

import com.intellij.codeInsight.daemon.impl.EditorTrackerListener;
import com.intellij.openapi.editor.Editor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class InlineCodeCompletionEditorTrackListener implements EditorTrackerListener {

  private final Map<Editor, EditorInlayHandler> completionRenderers = new HashMap<>();

  @Override
  public void activeEditorsChanged(@NotNull List<? extends Editor> activeEditors) {
    List<Editor> newEditors = activeEditors.stream()
        .filter(activeEditor -> completionRenderers.keySet().stream()
            .noneMatch(activeEditor::equals))
        .collect(toList());
    List<Editor> inactiveEditors = completionRenderers.keySet().stream()
        .filter(editor -> activeEditors.stream()
            .noneMatch(editor::equals))
        .collect(toList());
    newEditors.forEach(editor ->
        completionRenderers.put(editor, new EditorInlayHandler(editor)));
    inactiveEditors.forEach(editor -> completionRenderers.remove(editor).dispose());
  }
}
