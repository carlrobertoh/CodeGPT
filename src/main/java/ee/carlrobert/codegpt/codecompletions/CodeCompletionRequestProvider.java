package ee.carlrobert.codegpt.codecompletions;

import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest.Builder;
import ee.carlrobert.llm.client.llama.completion.LlamaInfillRequest;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CodeCompletionRequestProvider {

  private final InfillRequestDetails details;

  public CodeCompletionRequestProvider(InfillRequestDetails details) {
    this.details = details;
  }

  public OpenAICompletionRequest buildOpenAIRequest(
      @Nullable String model,
      @Nullable String overriddenPath) {
    var builder = new OpenAIChatCompletionRequest.Builder(List.of(new OpenAIChatCompletionMessage(
        "user",
        String.format(
            "<|fim_prefix|> %s <|fim_suffix|> %s <|fim_middle|>",
            details.getPrefix(),
            details.getSuffix()))))
        .setModel(model)
        .setMaxTokens(ConfigurationState.getInstance().getMaxTokens())
        .setStream(false)
        .setTemperature(0.0);

    if (overriddenPath != null) {
      builder.setOverriddenPath(overriddenPath);
    }

    return builder.build();
  }

  public LlamaInfillRequest buildLlamaRequest() {
    var settings = LlamaSettingsState.getInstance();
    var configuration = ConfigurationState.getInstance();
    return new LlamaInfillRequest(new Builder("")
        .setN_predict(configuration.getMaxTokens())
        .setTemperature(configuration.getTemperature())
        .setTop_k(settings.getTopK())
        .setTop_p(settings.getTopP())
        .setMin_p(settings.getMinP())
        .setRepeat_penalty(settings.getRepeatPenalty())
        .setStream(true)
        .setStop(List.of("  <EOT>", "<EOT>")), details.getPrefix(), details.getSuffix());
  }
}
