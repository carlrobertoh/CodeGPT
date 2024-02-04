package ee.carlrobert.codegpt.completions.llama;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;

import java.io.File;

public class CustomLlamaModel implements LlamaCompletionModel {

  private final String code;
  private final String description;
  private final Integer maxTokens;
  private final String model;

  public CustomLlamaModel(String model) {
    this.model = model;
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

  public String getModel() {
    return model;
  }

  public String getModelFileName() {
    return model.substring(model.lastIndexOf(File.separator) + 1);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof CustomLlamaModel)) {
      return false;
    }
    return !areValuesDifferent(this.model, ((CustomLlamaModel) obj).model);
  }
}
