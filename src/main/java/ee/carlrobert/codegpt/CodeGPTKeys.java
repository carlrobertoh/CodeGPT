package ee.carlrobert.codegpt;

import com.intellij.openapi.util.Key;
import ee.carlrobert.embedding.ReferencedFile;
import java.util.List;

public class CodeGPTKeys {

  public static final Key<List<ReferencedFile>> SELECTED_FILES = Key.create("selectedFiles");
}
