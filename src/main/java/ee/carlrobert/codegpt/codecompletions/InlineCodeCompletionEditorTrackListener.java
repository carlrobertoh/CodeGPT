package ee.carlrobert.codegpt.codecompletions;

import static java.util.stream.Collectors.toList;

import com.intellij.codeInsight.daemon.impl.EditorTrackerListener;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import ee.carlrobert.codegpt.actions.CodeCompletionEnabledListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * Tracks which {@link Editor} are currently open and enables/disables {@link EditorInlayHandler}
 * for code-completion.<br/> Subscribes to {@link CodeCompletionEnabledListener#TOPIC} to
 * register/unregister the {@link EditorTrackerListener} accordingly.
 */
public class InlineCodeCompletionEditorTrackListener implements EditorTrackerListener {

  private final Project project;
  private final Map<Editor, EditorInlayHandler> inlayHandlers = new HashMap<>();
  private MessageBusConnection projectBusConnection;

  public InlineCodeCompletionEditorTrackListener(Project project) {
    this.project = project;
    this.project.getMessageBus().connect().subscribe(CodeCompletionEnabledListener.TOPIC,
        (CodeCompletionEnabledListener) codeCompletionsEnabled -> {
          if (codeCompletionsEnabled && projectBusConnection == null) {
            subscribe();
          } else if (!codeCompletionsEnabled) {
            unsubscribe();
          }
        });
  }

  /**
   * Subscribe as {@link EditorTrackerListener} on {@link #projectBusConnection} and notify for
   * currently active Editor.
   */
  private void subscribe() {
    projectBusConnection = project.getMessageBus().connect();
    projectBusConnection.subscribe(EditorTrackerListener.TOPIC, this);
    Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    if (selectedTextEditor != null) {
      activeEditorsChanged(List.of(selectedTextEditor));
    }
  }

  /**
   * Unsubscribe {@link EditorTrackerListener} from {@link #projectBusConnection} and disposes all
   * {@link #inlayHandlers}.
   */
  private void unsubscribe() {
    projectBusConnection.disconnect();
    projectBusConnection.dispose();
    projectBusConnection = null;
    inlayHandlers.values().forEach(EditorInlayHandler::dispose);
    inlayHandlers.clear();
  }


  @Override
  public void activeEditorsChanged(@NotNull List<? extends Editor> activeEditors) {
    List<Editor> newEditors = activeEditors.stream()
        .filter(activeEditor -> inlayHandlers.keySet().stream()
            .noneMatch(activeEditor::equals))
        .collect(toList());
    List<Editor> inactiveEditors = inlayHandlers.keySet().stream()
        .filter(editor -> activeEditors.stream()
            .noneMatch(editor::equals))
        .collect(toList());
    newEditors.forEach(editor ->
        inlayHandlers.put(editor, new EditorInlayHandler(editor)));
    inactiveEditors.forEach(editor -> inlayHandlers.remove(editor).dispose());
  }
}
