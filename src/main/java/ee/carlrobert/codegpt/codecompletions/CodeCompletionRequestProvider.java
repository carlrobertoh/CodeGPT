package ee.carlrobert.codegpt.codecompletions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.credentials.CustomServiceCredentialManager;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettingsState;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAITextCompletionRequest;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CodeCompletionRequestProvider {

  private static final int MAX_TOKENS = 128;
  private static final String CHAT_URL_SUFFIX = "v1/chat/completions";
  private static final String TEXT_URL_SUFFIX = "v1/completions";

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

  public Request buildCustomOpenAIRequest(CustomServiceSettingsState customConfiguration) {
    return buildCustomOpenAITextCompletionRequest(buildOpenAIRequest(), customConfiguration);
  }

  private Request buildCustomOpenAITextCompletionRequest(OpenAITextCompletionRequest request,
                                             CustomServiceSettingsState customConfiguration) {
    var requestBuilder
            = new Request.Builder().url(buildCustomOpenTextCompletionUrl(customConfiguration));
    for (var entry : customConfiguration.getHeaders().entrySet()) {
      String value = entry.getValue();
      if (value.contains("$CUSTOM_SERVICE_API_KEY")) {
        value = value.replace("$CUSTOM_SERVICE_API_KEY",
                CustomServiceCredentialManager.getInstance().getCredential());
      }
      requestBuilder.addHeader(entry.getKey(), value);
    }

    if (request.isStream()) {
      requestBuilder.addHeader("Accept", "text/event-stream");
    }

    try {
      var requestBody = RequestBody.create(
        new ObjectMapper().writeValueAsString(request), MediaType.parse("application/json"));
      return requestBuilder.post(requestBody).build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private String buildCustomOpenTextCompletionUrl(CustomServiceSettingsState customConfiguration) {
    var url =  customConfiguration.getUrl().trim();
    if (url.endsWith(CHAT_URL_SUFFIX) || url.endsWith(CHAT_URL_SUFFIX + "/")) {
      return url.replace(CHAT_URL_SUFFIX, TEXT_URL_SUFFIX);
    } else if (url.endsWith(TEXT_URL_SUFFIX) || url.endsWith(TEXT_URL_SUFFIX + "/")) {
      return url;
    } else {
      return url + TEXT_URL_SUFFIX;
    }
  }
}
