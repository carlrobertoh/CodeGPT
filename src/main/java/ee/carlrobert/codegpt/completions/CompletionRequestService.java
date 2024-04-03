package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.CUSTOM_OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.codecompletions.CodeCompletionRequestProvider;
import ee.carlrobert.codegpt.codecompletions.InfillRequestDetails;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionRequest;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionRequestMessage;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import okhttp3.Request;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;

@Service
public final class CompletionRequestService {

  private static final Logger LOG = Logger.getInstance(CompletionRequestService.class);

  private CompletionRequestService() {
  }

  public static CompletionRequestService getInstance() {
    return ApplicationManager.getApplication().getService(CompletionRequestService.class);
  }

  public EventSource getCustomOpenAIChatCompletionAsync(
      Request customRequest,
      CompletionEventListener<String> eventListener) {
    var httpClient = CompletionClientProvider.getDefaultClientBuilder().build();
    return EventSources.createFactory(httpClient).newEventSource(
        customRequest,
        new OpenAIChatCompletionEventSourceListener(eventListener));
  }

  public EventSource getChatCompletionAsync(
      CallParameters callParameters,
      CompletionEventListener<String> eventListener) {
    var requestProvider = new CompletionRequestProvider(callParameters.getConversation());
    return switch (GeneralSettings.getCurrentState().getSelectedPersona().getModelProvider()) {
      case OPENAI -> CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(
          requestProvider.buildOpenAIChatCompletionRequest(
              OpenAISettings.getCurrentState().getModel(),
              callParameters),
          eventListener);
      case CUSTOM_OPENAI -> getCustomOpenAIChatCompletionAsync(
          requestProvider.buildCustomOpenAIChatCompletionRequest(
              CustomServiceSettings.getCurrentState(),
              callParameters),
          eventListener);
      case ANTHROPIC -> CompletionClientProvider.getClaudeClient().getCompletionAsync(
          requestProvider.buildAnthropicChatCompletionRequest(callParameters),
          eventListener);
      case AZURE -> CompletionClientProvider.getAzureClient().getChatCompletionAsync(
          requestProvider.buildOpenAIChatCompletionRequest(null, callParameters),
          eventListener);
      case YOU -> CompletionClientProvider.getYouClient().getChatCompletionAsync(
          requestProvider.buildYouCompletionRequest(callParameters.getMessage()),
          eventListener);
      case LLAMA_CPP -> CompletionClientProvider.getLlamaClient().getChatCompletionAsync(
          requestProvider.buildLlamaCompletionRequest(
              callParameters.getMessage(),
              callParameters.getConversationType()),
          eventListener);
      default -> throw new IllegalArgumentException();
    };
  }

  public EventSource getCodeCompletionAsync(
      InfillRequestDetails requestDetails,
      CompletionEventListener<String> eventListener) {
    var requestProvider = new CodeCompletionRequestProvider(requestDetails);
    return switch (GeneralSettings.getCurrentState().getSelectedPersona().getModelProvider()) {
      case OPENAI -> CompletionClientProvider.getOpenAIClient()
          .getCompletionAsync(requestProvider.buildOpenAIRequest(), eventListener);
      case LLAMA_CPP -> CompletionClientProvider.getLlamaClient()
          .getChatCompletionAsync(requestProvider.buildLlamaRequest(), eventListener);
      default ->
        throw new IllegalArgumentException("Code completion not supported for selected service");
    };
  }

  public void generateCommitMessageAsync(
      String prompt,
      CompletionEventListener<String> eventListener) {
    var configuration = ConfigurationSettings.getCurrentState();
    var commitMessagePrompt = configuration.getCommitMessagePrompt();
    var openaiRequest = new OpenAIChatCompletionRequest.Builder(List.of(
        new OpenAIChatCompletionMessage("system", commitMessagePrompt),
        new OpenAIChatCompletionMessage("user", prompt)))
        .setModel(OpenAISettings.getCurrentState().getModel())
        .build();
    var selectedService = GeneralSettings.getCurrentState().getSelectedPersona().getModelProvider();
    switch (selectedService) {
      case OPENAI:
        CompletionClientProvider.getOpenAIClient()
            .getChatCompletionAsync(openaiRequest, eventListener);
        break;
      case CUSTOM_OPENAI:
        var httpClient = CompletionClientProvider.getDefaultClientBuilder().build();
        EventSources.createFactory(httpClient).newEventSource(
            CompletionRequestProvider.buildCustomOpenAICompletionRequest(
                commitMessagePrompt,
                prompt),
            new OpenAIChatCompletionEventSourceListener(eventListener));
        break;
      case ANTHROPIC:
        var anthropicSettings = AnthropicSettings.getCurrentState();
        var claudeRequest = new ClaudeCompletionRequest();
        claudeRequest.setSystem(commitMessagePrompt);
        claudeRequest.setStream(true);
        claudeRequest.setMaxTokens(configuration.getMaxTokens());
        claudeRequest.setModel(anthropicSettings.getModel());
        claudeRequest.setMessages(
            List.of(new ClaudeCompletionRequestMessage("user", prompt)));
        CompletionClientProvider.getClaudeClient()
            .getCompletionAsync(claudeRequest, eventListener);
        break;
      case AZURE:
        CompletionClientProvider.getAzureClient()
            .getChatCompletionAsync(openaiRequest, eventListener);
        break;
      case LLAMA_CPP:
        var settings = LlamaSettings.getCurrentState();
        PromptTemplate promptTemplate;
        if (settings.isRunLocalServer()) {
          promptTemplate = settings.isUseCustomModel()
              ? settings.getLocalModelPromptTemplate()
              : LlamaModel.findByHuggingFaceModel(settings.getHuggingFaceModel())
                  .getPromptTemplate();
        } else {
          promptTemplate = settings.getRemoteModelPromptTemplate();
        }
        var finalPrompt = promptTemplate.buildPrompt(commitMessagePrompt, prompt, List.of());
        CompletionClientProvider.getLlamaClient().getChatCompletionAsync(
            new LlamaCompletionRequest.Builder(finalPrompt)
                .setN_predict(configuration.getMaxTokens())
                .setTemperature(configuration.getTemperature())
                .setTop_k(settings.getTopK())
                .setTop_p(settings.getTopP())
                .setMin_p(settings.getMinP())
                .setRepeat_penalty(settings.getRepeatPenalty())
                .build(),
            eventListener);
        break;
      default:
        LOG.debug("Unknown service: {}", selectedService);
        break;
    }
  }

  public Optional<String> getLookupCompletion(String prompt) {
    var selectedService = GeneralSettings.getCurrentState().getSelectedPersona().getModelProvider();
    if (selectedService == YOU || selectedService == LLAMA_CPP) {
      return Optional.empty();
    }

    if (selectedService == CUSTOM_OPENAI) {
      var request = CompletionRequestProvider.buildCustomOpenAILookupCompletionRequest(prompt);
      var httpClient = CompletionClientProvider.getDefaultClientBuilder().build();
      try (var response = httpClient.newCall(request).execute()) {
        return tryExtractContent(
            DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    var request = CompletionRequestProvider.buildOpenAILookupCompletionRequest(prompt);
    var response = selectedService == OPENAI
        ? CompletionClientProvider.getOpenAIClient().getChatCompletion(request)
        : CompletionClientProvider.getAzureClient().getChatCompletion(request);
    return tryExtractContent(response);
  }

  private Optional<String> tryExtractContent(OpenAIChatCompletionResponse response) {
    return response
        .getChoices()
        .stream()
        .findFirst()
        .map(item -> item.getMessage().getContent());
  }

  public boolean isRequestAllowed() {
    var selectedService = GeneralSettings.getCurrentState().getSelectedPersona().getModelProvider();
    if (selectedService == AZURE) {
      return AzureCredentialsManager.getInstance().isCredentialSet();
    }
    if (selectedService == OPENAI) {
      return OpenAICredentialManager.getInstance().isCredentialSet();
    }
    return true;
  }
}
