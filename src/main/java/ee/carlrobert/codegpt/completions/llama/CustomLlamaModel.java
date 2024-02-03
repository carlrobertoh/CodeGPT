package ee.carlrobert.codegpt.completions.llama;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;

import java.io.File;

public class CustomLlamaModel implements LlamaCompletionModel {

  private final String code;
  private final String description;
  private final Integer maxTokens;
  private final String modelPath;

  public CustomLlamaModel(String modelPath) {
    this.modelPath = modelPath;
    this.code = null;
    this.description = null;
    this.maxTokens = 0;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public int getMaxTokens() {
    return maxTokens;
  }

  public String getModelPath() {
    return modelPath;
  }

  public String getModelFileName() {
    return modelPath.substring(modelPath.lastIndexOf(File.separator) + 1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CustomLlamaModel)) {
      return false;
    }
    return !areValuesDifferent(this.modelPath, ((CustomLlamaModel) obj).modelPath);
  }
}
