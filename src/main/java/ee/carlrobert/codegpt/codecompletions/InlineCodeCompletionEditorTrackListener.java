package ee.carlrobert.codegpt.codecompletions;

import com.intellij.codeInsight.daemon.impl.EditorTrackerListener;
import com.intellij.openapi.editor.Editor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class InlineCodeCompletionEditorTrackListener implements EditorTrackerListener {

  private final Map<Editor, InlineCodeCompletionRenderer> completionRenderers = new HashMap<>();

  @Override
  public void activeEditorsChanged(@NotNull List<? extends Editor> activeEditors) {
    List<Editor> newEditors = activeEditors.stream()
        .filter(activeEditor -> completionRenderers.keySet().stream()
            .noneMatch(activeEditor::equals))
        .collect(Collectors.toList());
    List<Editor> inactiveEditors = completionRenderers.keySet().stream()
        .filter(editor -> activeEditors.stream()
            .noneMatch(editor::equals))
        .collect(Collectors.toList());
    newEditors.forEach(editor -> {
      completionRenderers.put(editor, new InlineCodeCompletionRenderer(editor));
    });
    inactiveEditors.forEach(editor -> {
      InlineCodeCompletionRenderer completionRenderer = completionRenderers.remove(editor);
      completionRenderer.disableSuggestions();
    });

  }
}
