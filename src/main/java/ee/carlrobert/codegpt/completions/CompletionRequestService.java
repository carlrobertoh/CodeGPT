package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.settings.service.ServiceType.ANTHROPIC;
import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.CUSTOM_OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.codecompletions.CodeCompletionRequestFactory;
import ee.carlrobert.codegpt.codecompletions.InfillRequestDetails;
import ee.carlrobert.codegpt.completions.custom.CustomServiceRequestBuilder;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionRequest;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionStandardMessage;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoice;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoiceDelta;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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

  public EventSource getCustomOpenAICompletionAsync(
      Request customRequest,
      CompletionEventListener<String> eventListener) {
    var httpClient = CompletionClientProvider.getDefaultClientBuilder().build();
    return EventSources.createFactory(httpClient).newEventSource(
        customRequest,
        new OpenAITextCompletionEventSourceListener(eventListener));
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
    return switch (GeneralSettings.getCurrentState().getSelectedService()) {
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
    };
  }

  public EventSource getCodeCompletionAsync(
      InfillRequestDetails requestDetails,
      CompletionEventListener<String> eventListener) {
    var httpClient = CompletionClientProvider.getDefaultClientBuilder().build();
    return switch (GeneralSettings.getCurrentState().getSelectedService()) {
      case OPENAI -> CompletionClientProvider.getOpenAIClient()
          .getCompletionAsync(
              CodeCompletionRequestFactory.INSTANCE.buildOpenAIRequest(requestDetails),
              eventListener);
      case CUSTOM_OPENAI ->
          EventSources.createFactory(httpClient).newEventSource(
            CustomServiceRequestBuilder.buildCompletionRequest(
                CustomServiceSettings.getCurrentState(),
                requestDetails),
            new OpenAITextCompletionEventSourceListener(eventListener));
      case LLAMA_CPP -> CompletionClientProvider.getLlamaClient()
          .getChatCompletionAsync(
              CodeCompletionRequestFactory.INSTANCE.buildLlamaRequest(requestDetails),
              eventListener);
      default ->
          throw new IllegalArgumentException("Code completion not supported for selected service");
    };
  }

  public void generateCommitMessageAsync(
      String systemPrompt,
      String gitDiff,
      CompletionEventListener<String> eventListener) {
    var configuration = ConfigurationSettings.getCurrentState();
    var openaiRequest = new OpenAIChatCompletionRequest.Builder(List.of(
        new OpenAIChatCompletionStandardMessage("system", systemPrompt),
        new OpenAIChatCompletionStandardMessage("user", gitDiff)))
        .setModel(OpenAISettings.getCurrentState().getModel())
        .build();
    var selectedService = GeneralSettings.getCurrentState().getSelectedService();
    switch (selectedService) {
      case OPENAI:
        CompletionClientProvider.getOpenAIClient()
            .getChatCompletionAsync(openaiRequest, eventListener);
        break;
      case CUSTOM_OPENAI:
        var httpClient = CompletionClientProvider.getDefaultClientBuilder().build();
        EventSources.createFactory(httpClient).newEventSource(
            CustomServiceRequestBuilder.buildChatCompletionRequest(
                CustomServiceSettings.getCurrentState(),
                List.of(
                    new OpenAIChatCompletionStandardMessage("system", systemPrompt),
                    new OpenAIChatCompletionStandardMessage("user", gitDiff))
            ),
            new OpenAIChatCompletionEventSourceListener(eventListener));
        break;
      case ANTHROPIC:
        var anthropicSettings = AnthropicSettings.getCurrentState();
        var claudeRequest = new ClaudeCompletionRequest();
        claudeRequest.setSystem(systemPrompt);
        claudeRequest.setStream(true);
        claudeRequest.setMaxTokens(configuration.getMaxTokens());
        claudeRequest.setModel(anthropicSettings.getModel());
        claudeRequest.setMessages(List.of(new ClaudeCompletionStandardMessage("user", gitDiff)));
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
        var finalPrompt = promptTemplate.buildPrompt(systemPrompt, gitDiff, List.of());
        CompletionClientProvider.getLlamaClient().getChatCompletionAsync(
            new LlamaCompletionRequest.Builder(finalPrompt)
                .setN_predict(configuration.getMaxTokens())
                .setTemperature(configuration.getTemperature())
                .setTop_k(settings.getTopK())
                .setTop_p(settings.getTopP())
                .setMin_p(settings.getMinP())
                .setRepeat_penalty(settings.getRepeatPenalty())
                .build(), eventListener);
        break;
      default:
        LOG.debug("Unknown service: {}", selectedService);
        break;
    }
  }

  public Optional<String> getLookupCompletion(String prompt) {
    var selectedService = GeneralSettings.getCurrentState().getSelectedService();
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

  public boolean isRequestAllowed() {
    return isRequestAllowed(GeneralSettings.getCurrentState().getSelectedService());
  }

  public static boolean isRequestAllowed(ServiceType serviceType) {
    if (serviceType == OPENAI
        && CredentialsStore.INSTANCE.isCredentialSet(CredentialKey.OPENAI_API_KEY)) {
      return true;
    }

    var azureCredentialKey = AzureSettings.getCurrentState().isUseAzureApiKeyAuthentication()
        ? CredentialKey.AZURE_OPENAI_API_KEY
        : CredentialKey.AZURE_ACTIVE_DIRECTORY_TOKEN;
    if (serviceType == AZURE && CredentialsStore.INSTANCE.isCredentialSet(azureCredentialKey)) {
      return true;
    }

    return List.of(LLAMA_CPP, ANTHROPIC, CUSTOM_OPENAI).contains(serviceType);
  }

  /**
   * Content of the first choice.
   * <ul>
   *     <li>Search all choices which are not null</li>
   *     <li>Search all messages which are not null</li>
   *     <li>Use first content which is not null or blank (whitespace)</li>
   * </ul>
   *
   * @return First non-blank content or {@code Optional.empty()}
   */
  private Optional<String> tryExtractContent(OpenAIChatCompletionResponse response) {
    return response
        .getChoices()
        .stream()
        .filter(Objects::nonNull)
        .map(OpenAIChatCompletionResponseChoice::getMessage)
        .filter(Objects::nonNull)
        .map(OpenAIChatCompletionResponseChoiceDelta::getContent)
        .filter(c -> c != null && !c.isBlank())
        .findFirst();
  }
}
