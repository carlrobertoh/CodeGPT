package ee.carlrobert.codegpt.actions.editor;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Key;
import ee.carlrobert.codegpt.codecompletions.InlineCodeCompletionRenderer;
import org.jetbrains.annotations.NotNull;

public class InsertInlineTextAction extends EditorAction {
    public InsertInlineTextAction(
            Key<Inlay<InlineCodeCompletionRenderer>> inlayKey,
            String text
    ) {
        super(new EditorActionHandler() {
            @Override
            protected void doExecute(@NotNull Editor editor, Caret caret, DataContext dataContext) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    Inlay<InlineCodeCompletionRenderer> inlay = editor.getUserData(inlayKey);
                    if (inlay != null) {
                        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                            int offset = inlay.getOffset();
                            editor.getDocument().insertString(offset, text);
                            editor.getCaretModel().moveToOffset(offset + text.length());
                            Inlay<InlineCodeCompletionRenderer> currentInlay = editor.getUserData(inlayKey);
                            if (currentInlay != null) {
                                currentInlay.dispose();
                                editor.putUserData(inlayKey, null);
                            }
                        });
                    }
                });
            }
        });
    }
}
