package ee.carlrobert.codegpt.completions.llama;

import com.intellij.openapi.util.io.FileUtil;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.llm.completion.CompletionModel;
import java.io.File;

public interface LlamaCompletionModel extends CompletionModel {

  static String getModelPath(LlamaCompletionModel model) {
    return model instanceof HuggingFaceModel ? CodeGPTPlugin.getLlamaModelsPath() + File.separator
        + ((HuggingFaceModel) model).getModelFileName() : model.getCode();
  }

  static boolean isModelExists(LlamaCompletionModel model) {
    return FileUtil.exists(getModelPath(model));
  }

  @Override
  default String getDescription() {
    return null;
  }

  @Override
  default int getMaxTokens() {
    return -1;
  }
}
