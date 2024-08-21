package ee.carlrobert.codegpt.completions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings;
import ee.carlrobert.codegpt.settings.service.google.GoogleSettings;
import ee.carlrobert.codegpt.settings.service.google.GoogleSettingsState;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionRequest;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionStandardMessage;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionContent;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionRequest;
import ee.carlrobert.llm.client.google.completion.GoogleGenerationConfig;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaChatCompletionMessage;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest.Builder;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoice;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoiceDelta;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
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
    var application = ApplicationManager.getApplication();
    var requestProvider = new CompletionRequestProvider(callParameters.getConversation());
    return switch (GeneralSettings.getSelectedService()) {
      case CODEGPT -> CompletionClientProvider.getCodeGPTClient().getChatCompletionAsync(
          requestProvider.buildOpenAIChatCompletionRequest(
              application.getService(CodeGPTServiceSettings.class)
                  .getState()
                  .getChatCompletionSettings()
                  .getModel(),
              callParameters),
          eventListener
      );
      case OPENAI -> CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(
          requestProvider.buildOpenAIChatCompletionRequest(
              OpenAISettings.getCurrentState().getModel(),
              callParameters),
          eventListener);
      case CUSTOM_OPENAI -> getCustomOpenAIChatCompletionAsync(
          requestProvider.buildCustomOpenAIChatCompletionRequest(
              application.getService(CustomServiceSettings.class)
                  .getState()
                  .getChatCompletionSettings(),
              callParameters),
          eventListener);
      case ANTHROPIC -> CompletionClientProvider.getClaudeClient().getCompletionAsync(
          requestProvider.buildAnthropicChatCompletionRequest(callParameters),
          eventListener);
      case AZURE -> CompletionClientProvider.getAzureClient().getChatCompletionAsync(
          requestProvider.buildOpenAIChatCompletionRequest(null, callParameters),
          eventListener);
      case LLAMA_CPP -> CompletionClientProvider.getLlamaClient().getChatCompletionAsync(
          requestProvider.buildLlamaCompletionRequest(
              callParameters.getMessage(),
              callParameters.getConversationType()),
          eventListener);
      case OLLAMA -> CompletionClientProvider.getOllamaClient().getChatCompletionAsync(
          requestProvider.buildOllamaChatCompletionRequest(callParameters),
          eventListener);
      case GOOGLE -> {
        var settings = application.getService(GoogleSettings.class).getState();
        yield CompletionClientProvider.getGoogleClient().getChatCompletionAsync(
            requestProvider.buildGoogleChatCompletionRequest(
                settings.getModel(),
                callParameters),
            settings.getModel(),
            eventListener);
      }
    };
  }

  public void generateCommitMessageAsync(
      String systemPrompt,
      String gitDiff,
      CompletionEventListener<String> eventListener) {
    var configuration = ConfigurationSettings.getState();
    var openaiRequestBuilder = new Builder(List.of(
        new OpenAIChatCompletionStandardMessage("system", systemPrompt),
        new OpenAIChatCompletionStandardMessage("user", gitDiff)))
        .setModel(OpenAISettings.getCurrentState().getModel());
    var selectedService = GeneralSettings.getSelectedService();
    switch (selectedService) {
      case CODEGPT:
        CompletionClientProvider.getCodeGPTClient().getChatCompletionAsync(
            openaiRequestBuilder
                .setModel(
                    ApplicationManager.getApplication().getService(CodeGPTServiceSettings.class)
                        .getState()
                        .getChatCompletionSettings()
                        .getModel())
                .build(),
            eventListener);
        break;
      case OPENAI:
        CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(
            openaiRequestBuilder
                .setModel(OpenAISettings.getCurrentState().getModel())
                .build(),
            eventListener);
        break;
      case CUSTOM_OPENAI:
        var httpClient = CompletionClientProvider.getDefaultClientBuilder().build();
        EventSources.createFactory(httpClient).newEventSource(
            CompletionRequestProvider.buildCustomOpenAICompletionRequest(
                systemPrompt,
                gitDiff),
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
            .getChatCompletionAsync(openaiRequestBuilder.build(), eventListener);
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
      case OLLAMA:
        var model = ApplicationManager.getApplication()
            .getService(OllamaSettings.class)
            .getState()
            .getModel();
        var request = new OllamaChatCompletionRequest.Builder(
            model,
            List.of(
                new OllamaChatCompletionMessage("system", systemPrompt, null),
                new OllamaChatCompletionMessage("user", gitDiff, null)
            )
        ).build();
        CompletionClientProvider.getOllamaClient().getChatCompletionAsync(request, eventListener);
        break;
      case GOOGLE:
        GoogleSettingsState state = ApplicationManager.getApplication()
            .getService(GoogleSettings.class).getState();
        CompletionClientProvider.getGoogleClient()
            .getChatCompletionAsync(new GoogleCompletionRequest.Builder(
                List.of(
                    new GoogleCompletionContent("user", List.of(systemPrompt)),
                    new GoogleCompletionContent("model", List.of("Understood.")),
                    new GoogleCompletionContent("user", List.of(gitDiff))
                ))
                .generationConfig(new GoogleGenerationConfig.Builder()
                    .maxOutputTokens(configuration.getMaxTokens())
                    .temperature(configuration.getTemperature()).build())
                .build(), state.getModel(), eventListener);
        break;
      default:
        LOG.debug("Unknown service: {}", selectedService);
        break;
    }
  }

  public Optional<String> getLookupCompletion(String prompt) {
    var openaiRequest = CompletionRequestProvider.buildOpenAILookupCompletionRequest(prompt);
    var selectedService = GeneralSettings.getSelectedService();
    switch (selectedService) {
      case CODEGPT:
        var model = ApplicationManager.getApplication().getService(CodeGPTServiceSettings.class)
            .getState()
            .getChatCompletionSettings()
            .getModel();
        return tryExtractContent(
            CompletionClientProvider.getCodeGPTClient().getChatCompletion(
                CompletionRequestProvider.buildOpenAILookupCompletionRequest(prompt, model)));
      case OPENAI:
        return tryExtractContent(
            CompletionClientProvider.getOpenAIClient().getChatCompletion(openaiRequest));
      case AZURE:
        return tryExtractContent(
            CompletionClientProvider.getAzureClient().getChatCompletion(openaiRequest));
      case CUSTOM_OPENAI:
        var request = CompletionRequestProvider.buildCustomOpenAILookupCompletionRequest(prompt);
        var httpClient = CompletionClientProvider.getDefaultClientBuilder().build();
        try (var response = httpClient.newCall(request).execute()) {
          return tryExtractContent(
              DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      default:
        return Optional.empty();
    }
  }

  public boolean isAllowed() {
    return isRequestAllowed();
  }

  public static boolean isRequestAllowed() {
    return isRequestAllowed(GeneralSettings.getSelectedService());
  }

  public static boolean isRequestAllowed(ServiceType serviceType) {
    return switch (serviceType) {
      case OPENAI -> CredentialsStore.INSTANCE.isCredentialSet(CredentialKey.OPENAI_API_KEY);
      case AZURE -> CredentialsStore.INSTANCE.isCredentialSet(
          AzureSettings.getCurrentState().isUseAzureApiKeyAuthentication()
              ? CredentialKey.AZURE_OPENAI_API_KEY
              : CredentialKey.AZURE_ACTIVE_DIRECTORY_TOKEN);
      case CODEGPT, CUSTOM_OPENAI, ANTHROPIC, LLAMA_CPP, OLLAMA -> true;
      case GOOGLE -> CredentialsStore.INSTANCE.isCredentialSet(CredentialKey.GOOGLE_API_KEY);
    };
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
    return Stream.ofNullable(response.getChoices())
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(OpenAIChatCompletionResponseChoice::getMessage)
        .filter(Objects::nonNull)
        .map(OpenAIChatCompletionResponseChoiceDelta::getContent)
        .filter(c -> c != null && !c.isBlank())
        .findFirst();
  }
}
