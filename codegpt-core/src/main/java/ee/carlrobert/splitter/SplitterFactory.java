package ee.carlrobert.splitter;

import org.jetbrains.annotations.Nullable;

public class SplitterFactory {

  public static @Nullable Splitter getCodeSplitter(String fileExtension) {
    switch (fileExtension) {
      case "java":
        return new JavaCodeSplitter();
      case "py":
        return new PythonCodeSplitter();
      case "json":
        return new JsonSplitter();
      case "ts":
      case "tsx":
        return new TypeScriptCodeSplitter();
      default:
        return null;
    }
  }
}
