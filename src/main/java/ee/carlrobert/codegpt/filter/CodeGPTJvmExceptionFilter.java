package ee.carlrobert.codegpt.filter;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.JvmExceptionOccurrenceFilter;
import com.intellij.execution.impl.InlayProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class CodeGPTJvmExceptionFilter implements JvmExceptionOccurrenceFilter {

  @Override
  public Filter.ResultItem applyFilter(@NotNull String exceptionClassName,
      @NotNull List<PsiClass> classes, int exceptionStartOffset) {
    return new CreateExceptionBreakpointResult(exceptionStartOffset,
        exceptionStartOffset + exceptionClassName.length(), classes
        .get(0).getProject());
  }

  private static class CreateExceptionBreakpointResult extends Filter.ResultItem implements
      InlayProvider {

    private final Project project;

    private final int startOffset;

    CreateExceptionBreakpointResult(int highlightStartOffset, int highlightEndOffset,
        Project project) {
      super(highlightStartOffset, highlightEndOffset, null);
      this.project = project;
      this.startOffset = highlightStartOffset;
    }

    public EditorCustomElementRenderer createInlayRenderer(Editor editor) {
      return new CodeGPTPresentation(editor, this.project, this.startOffset);
    }
  }
}
