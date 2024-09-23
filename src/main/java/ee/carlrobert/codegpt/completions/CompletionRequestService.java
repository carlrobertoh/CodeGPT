package ee.carlrobert.codegpt.completions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.actions.editor.EditCodeRequestParams;
import ee.carlrobert.codegpt.completions.factory.CustomOpenAIRequest;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.google.GoogleSettings;
import ee.carlrobert.llm.client.DeserializationUtil;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionRequest;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionRequest;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoice;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponseChoiceDelta;
import ee.carlrobert.llm.completion.CompletionEventListener;
import ee.carlrobert.llm.completion.CompletionRequest;
import java.io.IOException;
import java.util.Collection;
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

  public String getLookupCompletion(String prompt) {
    return getChatCompletion(
        CompletionRequestFactory.getFactory(GeneralSettings.getSelectedService())
            .createLookupRequest(prompt));
  }

  public EventSource getCommitMessageAsync(
      String systemPrompt,
      String gitDiff,
      CompletionEventListener<String> eventListener) {
    return getChatCompletionAsync(
        CompletionRequestFactory.getFactory(GeneralSettings.getSelectedService())
            .createCommitMessageRequest(systemPrompt, gitDiff),
        eventListener);
  }

  public EventSource getEditCodeCompletionAsync(
      EditCodeRequestParams params,
      CompletionEventListener<String> eventListener) {
    var input = "%s\n\n%s".formatted(params.getPrompt(), params.getSelectedText());
    return getChatCompletionAsync(
        CompletionRequestFactory.getFactory(GeneralSettings.getSelectedService())
            .createEditCodeRequest(input),
        eventListener);
  }

  public EventSource getChatCompletionAsync(
      CallParameters callParameters,
      CompletionEventListener<String> eventListener) {
    return getChatCompletionAsync(
        CompletionRequestFactory.getFactory(GeneralSettings.getSelectedService())
            .createChatRequest(callParameters),
        eventListener);
  }

  private EventSource getChatCompletionAsync(
      CompletionRequest request,
      CompletionEventListener<String> eventListener) {
    if (request instanceof OpenAIChatCompletionRequest completionRequest) {
      return switch (GeneralSettings.getSelectedService()) {
        case CODEGPT -> CompletionClientProvider.getCodeGPTClient()
            .getChatCompletionAsync(completionRequest, eventListener);
        case OPENAI -> CompletionClientProvider.getOpenAIClient()
            .getChatCompletionAsync(completionRequest, eventListener);
        case AZURE -> CompletionClientProvider.getAzureClient()
            .getChatCompletionAsync(completionRequest, eventListener);
        default -> throw new RuntimeException("Unknown service selected");
      };
    }
    if (request instanceof CustomOpenAIRequest completionRequest) {
      return getCustomOpenAIChatCompletionAsync(completionRequest.getRequest(), eventListener);
    }
    if (request instanceof ClaudeCompletionRequest completionRequest) {
      return CompletionClientProvider.getClaudeClient().getCompletionAsync(
          completionRequest,
          eventListener);
    }
    if (request instanceof GoogleCompletionRequest completionRequest) {
      return CompletionClientProvider.getGoogleClient().getChatCompletionAsync(
          completionRequest,
          ApplicationManager.getApplication().getService(GoogleSettings.class)
              .getState()
              .getModel(),
          eventListener);
    }
    if (request instanceof OllamaChatCompletionRequest completionRequest) {
      return CompletionClientProvider.getOllamaClient().getChatCompletionAsync(
          completionRequest,
          eventListener);
    }
    if (request instanceof LlamaCompletionRequest completionRequest) {
      return CompletionClientProvider.getLlamaClient().getChatCompletionAsync(
          completionRequest,
          eventListener);
    }

    throw new IllegalStateException("Unknown request type: " + request.getClass());
  }

  private String getChatCompletion(CompletionRequest request) {
    if (request instanceof OpenAIChatCompletionRequest completionRequest) {
      var response = switch (GeneralSettings.getSelectedService()) {
        case CODEGPT -> CompletionClientProvider.getCodeGPTClient()
            .getChatCompletion(completionRequest);
        case OPENAI -> CompletionClientProvider.getOpenAIClient()
            .getChatCompletion(completionRequest);
        case AZURE -> CompletionClientProvider.getAzureClient()
            .getChatCompletion(completionRequest);
        default -> throw new RuntimeException("Unknown service selected");
      };
      return tryExtractContent(response).orElseThrow();
    }
    if (request instanceof CustomOpenAIRequest completionRequest) {
      var httpClient = CompletionClientProvider.getDefaultClientBuilder().build();
      try (var response = httpClient.newCall(completionRequest.getRequest()).execute()) {
        return DeserializationUtil.mapResponse(response, OpenAIChatCompletionResponse.class)
            .getChoices().get(0)
            .getMessage()
            .getContent();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    if (request instanceof ClaudeCompletionRequest completionRequest) {
      return CompletionClientProvider.getClaudeClient()
          .getCompletion(completionRequest)
          .getContent().get(0)
          .getText();
    }
    if (request instanceof GoogleCompletionRequest completionRequest) {
      return CompletionClientProvider.getGoogleClient().getChatCompletion(
              completionRequest,
              ApplicationManager.getApplication().getService(GoogleSettings.class)
                  .getState()
                  .getModel())
          .getCandidates().get(0)
          .getContent().getParts().get(0)
          .getText();
    }
    if (request instanceof OllamaChatCompletionRequest completionRequest) {
      return CompletionClientProvider.getOllamaClient()
          .getChatCompletion(completionRequest)
          .getMessage()
          .getContent();
    }
    if (request instanceof LlamaCompletionRequest completionRequest) {
      return CompletionClientProvider.getLlamaClient()
          .getChatCompletion(completionRequest)
          .getContent();
    }

    throw new IllegalStateException("Unknown request type: " + request.getClass());
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
