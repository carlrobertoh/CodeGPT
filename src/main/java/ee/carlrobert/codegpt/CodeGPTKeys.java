package ee.carlrobert.codegpt;

import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.util.Key;
import ee.carlrobert.embedding.ReferencedFile;
import java.util.List;

public class CodeGPTKeys {

  public static final Key<String> PREVIOUS_INLAY_TEXT =
      Key.create("codegpt.editor.inlay.prev-value");
  public static final Key<Inlay<EditorCustomElementRenderer>> SINGLE_LINE_INLAY =
      Key.create("codegpt.editor.inlay.single-line");
  public static final Key<Inlay<EditorCustomElementRenderer>> MULTI_LINE_INLAY =
      Key.create("codegpt.editor.inlay.multi-line");
  public static final Key<List<ReferencedFile>> SELECTED_FILES =
      Key.create("codegpt.selectedFiles");
}
