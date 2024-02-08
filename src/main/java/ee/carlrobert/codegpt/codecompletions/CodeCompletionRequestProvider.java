package ee.carlrobert.codegpt.codecompletions;

import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaParameters;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CodeCompletionRequestProvider {

  private static final int MAX_TOKENS = 128;

  private final InfillRequestDetails details;

  public CodeCompletionRequestProvider(InfillRequestDetails details) {
    this.details = details;
  }

  public OpenAITextCompletionRequest buildOpenAIRequest() {
    return new OpenAITextCompletionRequest.Builder(details.getPrefix())
        .setSuffix(details.getSuffix())
        .setStream(true)
        .setMaxTokens(MAX_TOKENS)
        .setTemperature(0.1)
        .build();
  }

  public LlamaCompletionRequest buildLlamaRequest() {
    InfillPromptTemplate promptTemplate = getLlamaInfillPromptTemplate();
    String prompt = promptTemplate.buildPrompt(details.getPrefix(), details.getSuffix());
    return new LlamaCompletionRequest.Builder(prompt)
        .setN_predict(MAX_TOKENS)
        .setStream(true)
        .setTemperature(0.1)
        .setStop(promptTemplate.getStopTokens())
        .build();
  }

  public OllamaCompletionRequest buildOllamaRequest() {
    InfillPromptTemplate promptTemplate = getOllamaInfillPromptTemplate();
    String prompt = promptTemplate.buildPrompt(details.getPrefix(), details.getSuffix());
    return new OllamaCompletionRequest.Builder(
        OllamaSettings.getCurrentState().getHuggingFaceModel().getOllamaTag(), prompt)
        .setOptions(new OllamaParameters.Builder()
            .numPredict(MAX_TOKENS)
            .temperature(0.1)
            .stop(promptTemplate.getStopTokens())
            .build())
        .setStream(true)
        .build();
  }

  private InfillPromptTemplate getLlamaInfillPromptTemplate() {
    var settings = LlamaSettings.getCurrentState();
    if (!settings.isRunLocalServer()) {
      return settings.getRemoteModelInfillPromptTemplate();
    }
    if (settings.isUseCustomModel()) {
      return settings.getLocalModelInfillPromptTemplate();
    }
    return LlamaModel.findByHuggingFaceModel(settings.getHuggingFaceModel())
        .getInfillPromptTemplate();
  }

  private InfillPromptTemplate getOllamaInfillPromptTemplate() {
    var settings = OllamaSettings.getCurrentState();
    return LlamaModel.findByHuggingFaceModel(settings.getHuggingFaceModel())
        .getInfillPromptTemplate();
  }
}
