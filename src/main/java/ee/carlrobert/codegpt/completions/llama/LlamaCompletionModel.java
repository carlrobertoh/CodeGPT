package ee.carlrobert.codegpt.completions.llama;

import com.intellij.openapi.util.io.FileUtil;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.OllamaSettingsState;
import ee.carlrobert.llm.completion.CompletionModel;
import java.io.File;

public interface LlamaCompletionModel extends CompletionModel {

  public static String getModelPath(LlamaCompletionModel model) {
    return model instanceof CustomLlamaModel ? ((CustomLlamaModel) model).getModel()
        : CodeGPTPlugin.getLlamaModelsPath() + File.separator
            + ((HuggingFaceModel) model).getModelFileName();
  }

  public static String getOllamaId(LlamaCompletionModel model) {
    return model instanceof CustomLlamaModel ? ((CustomLlamaModel) model).getModel()
        : ((HuggingFaceModel) model).getModelTag();
  }

  static boolean isModelExists(LlamaCompletionModel model, ServiceType serviceType) {
    if (model instanceof CustomLlamaModel || serviceType.equals(ServiceType.LLAMA_CPP)) {
      return FileUtil.exists(getModelPath(model));
    }
    // TODO: defer until server settings are done
//    String ollamaId = getOllamaId(model);
//    return OllamaSettingsState.getInstance().getAvailableModels().stream()
//        .anyMatch(ollamaModel -> ollamaModel.getModel().equals(ollamaId));

      return true;
  }

}
