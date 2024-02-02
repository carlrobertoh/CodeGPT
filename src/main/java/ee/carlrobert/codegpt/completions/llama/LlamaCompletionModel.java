package ee.carlrobert.codegpt.completions.llama;

import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.llm.completion.CompletionModel;
import java.io.File;

public interface LlamaCompletionModel extends CompletionModel {

  static String getModelPath(LlamaCompletionModel model) {
    return model instanceof CustomLlamaModel ? ((CustomLlamaModel) model).getModelPath()
        : CodeGPTPlugin.getLlamaSourcePath() + File.separator
            + ((HuggingFaceModel) model).getModelFileName();
  }
}
