package ee.carlrobert.codegpt.codecompletions;

import static java.util.stream.Collectors.toList;

import com.intellij.codeInsight.daemon.impl.EditorTrackerListener;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.messages.MessageBusConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class InlineCodeCompletionEditorTrackListener implements EditorTrackerListener {

  private static final Map<Project, InlineCodeCompletionEditorTrackListener> listeners =
      new HashMap<>();

  private static final Map<Project, MessageBusConnection> projectMessageBusConnections =
      new HashMap<>();

  private final Map<Editor, EditorInlayHandler> completionHandlers = new HashMap<>();

  public static void registerAll() {
    register(ProjectManager.getInstance().getOpenProjects());
  }

  public static void register(Project project) {
    register(new Project[]{project});
  }

  private static void register(Project[] projects) {
    for (Project project : projects) {
      MessageBusConnection connection = project.getMessageBus().connect();
      projectMessageBusConnections.put(project, connection);
      InlineCodeCompletionEditorTrackListener inlineCodeCompletionEditorTrackListener =
          new InlineCodeCompletionEditorTrackListener();
      connection.subscribe(EditorTrackerListener.TOPIC, inlineCodeCompletionEditorTrackListener);
      listeners.put(project, inlineCodeCompletionEditorTrackListener);
      Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
      if (selectedTextEditor != null) {
        inlineCodeCompletionEditorTrackListener.activeEditorsChanged(List.of(selectedTextEditor));
      }
    }
  }

  public static void unregisterAll() {
    Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
    for (Project project : openProjects) {
      MessageBusConnection connection = projectMessageBusConnections.remove(project);
      if (connection != null) {
        connection.disconnect();
        connection.dispose();
      }
      InlineCodeCompletionEditorTrackListener listener = listeners.remove(project);
      if (listener != null) {
        listener.completionHandlers.values().forEach(renderer -> {
          renderer.removeResetSuggestionListeners();
          renderer.disableSuggestions();
        });
        listener.completionHandlers.clear();
      }
    }

  }

  @Override
  public void activeEditorsChanged(@NotNull List<? extends Editor> activeEditors) {
    List<Editor> newEditors = activeEditors.stream()
        .filter(activeEditor -> completionHandlers.keySet().stream()
            .noneMatch(activeEditor::equals))
        .collect(toList());
    List<Editor> inactiveEditors = completionHandlers.keySet().stream()
        .filter(editor -> activeEditors.stream()
            .noneMatch(editor::equals))
        .collect(toList());
    newEditors.forEach(editor ->
        completionHandlers.put(editor, new EditorInlayHandler(editor)));
    inactiveEditors.forEach(editor -> completionHandlers.remove(editor).dispose());
  }
}
