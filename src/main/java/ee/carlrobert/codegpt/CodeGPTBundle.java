package ee.carlrobert.codegpt;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class CodeGPTBundle extends DynamicBundle {

  private static final CodeGPTBundle INSTANCE = new CodeGPTBundle();

  private CodeGPTBundle() {
    super("messages.codegpt");
  }

  public static String get(
      @NotNull @PropertyKey(resourceBundle = "messages.codegpt") String key,
      Object... params) {
    return INSTANCE.getMessage(key, params);
  }
}
