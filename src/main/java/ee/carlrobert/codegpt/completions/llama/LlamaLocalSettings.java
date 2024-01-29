package ee.carlrobert.codegpt.completions.llama;

import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.settings.state.LocalSettings;

public class LlamaLocalSettings extends LocalSettings<LlamaHuggingFaceModel> {

  public LlamaLocalSettings() {
    super(LlamaHuggingFaceModel.CODE_LLAMA_7B_Q4);
  }

  public LlamaLocalSettings(boolean useCustomModel, String customModel,
      LlamaHuggingFaceModel llmModel, PromptTemplate promptTemplate,
      Integer serverPort, int contextSize, int threads, String additionalCompileParameters) {
    super(useCustomModel, customModel, llmModel, promptTemplate, serverPort, contextSize, threads,
        additionalCompileParameters);
  }
}
