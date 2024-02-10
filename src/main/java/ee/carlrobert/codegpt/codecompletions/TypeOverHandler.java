package ee.carlrobert.codegpt.codecompletions;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public class TypeOverHandler extends TypedHandlerDelegate {

  private static final Key<Long> TYPE_OVER_STAMP = Key.create("codegpt.typeOverStamp");

  public static boolean getPendingTypeOverAndReset(@NotNull Editor editor) {
    Long stamp = TYPE_OVER_STAMP.get(editor);
    if (stamp == null) {
      return false;
    }
    TYPE_OVER_STAMP.set(editor, null);
    return stamp == editor.getDocument().getModificationStamp();
  }

  @NotNull
  public TypedHandlerDelegate.@NotNull Result beforeCharTyped(char c, @NotNull Project project,
      @NotNull Editor editor, @NotNull PsiFile file, @NotNull FileType fileType) {
    boolean validTypeOver = Stream.of(')', '}', ']', '"', '\'', '>', ';').anyMatch(it -> it == c);
    if (validTypeOver && CommandProcessor.getInstance().getCurrentCommand() != null) {
      TYPE_OVER_STAMP.set(editor, editor.getDocument().getModificationStamp());
    } else {
      TYPE_OVER_STAMP.set(editor, null);
    }
    return Result.CONTINUE;
  }
}
