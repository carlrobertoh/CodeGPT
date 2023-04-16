package ee.carlrobert.codegpt.toolwindow.chat.html;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ReplaceInEditorQuery {

  private final Function<String, JBCefJSQuery.Response> replaceCodeInEditorHandler;
  private final JBCefJSQuery replaceCodeQuery;

  ReplaceInEditorQuery(@NotNull Project project, @NotNull JBCefBrowserBase browserBase, Consumer<Editor> onSelectionChanged) {
    this.replaceCodeQuery = JBCefJSQuery.create(browserBase);
    this.replaceCodeInEditorHandler = getReplaceCodeInEditorHandler(project);
    addEditorListener(FileEditorManager.getInstance(project).getSelectedTextEditor(), onSelectionChanged);
    project.getMessageBus()
        .connect(browserBase)
        .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
          @Override
          public void fileOpenedSync(
              @NotNull FileEditorManager source, @NotNull VirtualFile file,
              @NotNull Pair<FileEditor[], FileEditorProvider[]> editors) {
            addEditorListener(source.getSelectedTextEditor(), onSelectionChanged);
          }

          @Override
          public void selectionChanged(@NotNull FileEditorManagerEvent event) {
            addEditorListener(event.getManager().getSelectedTextEditor(), onSelectionChanged);
          }
        });
  }

  JBCefJSQuery getQuery() {
    return replaceCodeQuery;
  }

  private void addEditorListener(@Nullable Editor editor, Consumer<Editor> onSelectionChanged) {
    if (editor != null) {
      ApplicationManager.getApplication().runReadAction(() ->
          editor.getSelectionModel().addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChanged(@NotNull SelectionEvent e) {
              SelectionListener.super.selectionChanged(e);
              onSelectionChanged.accept(editor);
            }
          }));
      replaceCodeQuery.removeHandler(replaceCodeInEditorHandler);
      replaceCodeQuery.addHandler(replaceCodeInEditorHandler);
    }
  }

  private Function<String, JBCefJSQuery.Response> getReplaceCodeInEditorHandler(Project project) {
    return (content) -> {
      var application = ApplicationManager.getApplication();
      application.invokeLater(() ->
          application.runWriteAction(() -> WriteCommandAction.runWriteCommandAction(project, () -> {
            var editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
              var selectionModel = editor.getSelectionModel();
              editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), content);
              editor.getContentComponent().requestFocus();
            }
          })));
      return null;
    };
  }
}
