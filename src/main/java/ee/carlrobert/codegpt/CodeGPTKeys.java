package ee.carlrobert.codegpt;

import com.intellij.openapi.util.Key;
import ee.carlrobert.embedding.CheckedFile;
import java.util.List;

public class CodeGPTKeys {

  public static final Key<List<CheckedFile>> SELECTED_FILES = Key.create("selectedFiles");
}
