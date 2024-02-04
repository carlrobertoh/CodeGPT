package ee.carlrobert.codegpt.codecompletions;

import ee.carlrobert.codegpt.completions.llama.CustomLlamaModel;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.state.LlamaCppSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.OllamaSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaRemoteSettings;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaParameters;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaParameters.Builder;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CodeCompletionRequestProvider {

  private static final int MAX_TOKENS = 256;

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
    var promptTemplate = getLlamaInfillPromptTemplate(LlamaCppSettingsState.getInstance());
    var prompt = promptTemplate.buildPrompt(details.getPrefix(), details.getSuffix());
    return new LlamaCompletionRequest.Builder(prompt)
        .setN_predict(MAX_TOKENS)
        .setStream(true)
        .setTemperature(0.1)
        .setStop(promptTemplate.getStopTokens())
        .build();
  }

  public OllamaCompletionRequest buildOllamaRequest() {
    var settingsState = OllamaSettingsState.getInstance();
    var model = LlamaCompletionModel.getOllamaId(settingsState.getUsedModel());
    var promptTemplate = getLlamaInfillPromptTemplate(settingsState);
    var prompt = promptTemplate.buildPrompt(details.getPrefix(), details.getSuffix());
    var configuration = ConfigurationState.getInstance();
    var requestSettings = settingsState.getRequestSettings();
    return new OllamaCompletionRequest.Builder(model, prompt)
        .setOptions(new OllamaParameters(new Builder()
            .temperature(configuration.getTemperature())
            .numPredict(configuration.getMaxTokens())
            .topK(requestSettings.getTopK())
            .topP(requestSettings.getTopP())
            .repeatPenalty(requestSettings.getRepeatPenalty())
        ))
        .setStream(true)
        .build();
  }

  private InfillPromptTemplate getLlamaInfillPromptTemplate(
      LlamaSettingsState<? extends LlamaRemoteSettings> settingsState) {
    if (!settingsState.isRunLocalServer()) {
      return settingsState.getRemoteSettings().getInfillPromptTemplate();
    }
    LlamaCompletionModel model = settingsState.getLocalSettings().getModel();
    if (model instanceof CustomLlamaModel) {
      return settingsState.getLocalSettings().getInfillPromptTemplate();
    }
    return LlamaModel.findByHuggingFaceModel((HuggingFaceModel) model)
        .getInfillPromptTemplate();
  }

}
