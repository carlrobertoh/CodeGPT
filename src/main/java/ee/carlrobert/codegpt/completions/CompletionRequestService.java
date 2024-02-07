package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.codecompletions.CodeCompletionRequestProvider;
import ee.carlrobert.codegpt.codecompletions.InfillRequestDetails;
import ee.carlrobert.codegpt.credentials.managers.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.managers.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.state.AzureSettings;
import ee.carlrobert.codegpt.settings.state.GeneralSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettings;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.Optional;
import okhttp3.sse.EventSource;

@Service
public final class CompletionRequestService {

  private CompletionRequestService() {
  }

  public static CompletionRequestService getInstance() {
    return ApplicationManager.getApplication().getService(CompletionRequestService.class);
  }

  public EventSource getChatCompletionAsync(
      CallParameters callParameters,
      boolean useContextualSearch,
      CompletionEventListener eventListener) {
    var requestProvider = new CompletionRequestProvider(callParameters.getConversation());
    switch (GeneralSettingsState.getInstance().getSelectedService()) {
      case OPENAI:
        var openAISettings = OpenAISettings.getInstance().getState();
        return CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(
            requestProvider.buildOpenAIChatCompletionRequest(
                openAISettings.getModel().getCode(),
                callParameters,
                useContextualSearch,
                openAISettings.isUsingCustomPath() ? openAISettings.getPath() : null),
            eventListener);
      case AZURE:
        var azureSettings = AzureSettings.getInstance().getState();
        return CompletionClientProvider.getAzureClient().getChatCompletionAsync(
            requestProvider.buildOpenAIChatCompletionRequest(
                null,
                callParameters,
                useContextualSearch,
                azureSettings.isUsingCustomPath() ? azureSettings.getPath() : null),
            eventListener);
      case YOU:
        return CompletionClientProvider.getYouClient().getChatCompletionAsync(
            requestProvider.buildYouCompletionRequest(callParameters.getMessage()),
            eventListener);
      case LLAMA_CPP:
        return CompletionClientProvider.getLlamaClient().getChatCompletionAsync(
            requestProvider.buildLlamaCompletionRequest(
                callParameters.getMessage(),
                callParameters.getConversationType()),
            eventListener);
      default:
        throw new IllegalArgumentException();
    }
  }

  public EventSource getCodeCompletionAsync(
      InfillRequestDetails requestDetails,
      CompletionEventListener eventListener) {
    var requestProvider = new CodeCompletionRequestProvider(requestDetails);
    switch (GeneralSettingsState.getInstance().getSelectedService()) {
      case OPENAI:
        return CompletionClientProvider.getOpenAIClient()
            .getCompletionAsync(requestProvider.buildOpenAIRequest(), eventListener);
      case LLAMA_CPP:
        return CompletionClientProvider.getLlamaClient()
            .getChatCompletionAsync(requestProvider.buildLlamaRequest(), eventListener);
      default:
        throw new IllegalArgumentException("Code completion not supported for selected service");
    }
  }

  public void generateCommitMessageAsync(
      String prompt,
      CompletionEventListener eventListener) {
    var request = new OpenAIChatCompletionRequest.Builder(List.of(
        new OpenAIChatCompletionMessage("system",
            ConfigurationState.getInstance().getCommitMessagePrompt()),
        new OpenAIChatCompletionMessage("user", prompt)))
        .setModel(OpenAISettings.getInstance().getState().getModel())
        .build();
    var selectedService = GeneralSettingsState.getInstance().getSelectedService();
    if (selectedService == OPENAI) {
      CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(request, eventListener);
    }
    if (selectedService == AZURE) {
      CompletionClientProvider.getAzureClient().getChatCompletionAsync(request, eventListener);
    }
  }

  public Optional<String> getLookupCompletion(String prompt) {
    var selectedService = GeneralSettingsState.getInstance().getSelectedService();
    if (selectedService == YOU || selectedService == LLAMA_CPP) {
      return Optional.empty();
    }

    var request = CompletionRequestProvider.buildOpenAILookupCompletionRequest(prompt);
    var response = selectedService == OPENAI
        ? CompletionClientProvider.getOpenAIClient().getChatCompletion(request)
        : CompletionClientProvider.getAzureClient().getChatCompletion(request);
    return response
        .getChoices()
        .stream()
        .findFirst()
        .map(item -> item.getMessage().getContent());
  }

  public boolean isRequestAllowed() {
    var selectedService = GeneralSettingsState.getInstance().getSelectedService();
    if (selectedService == AZURE) {
      return AzureCredentialsManager.getInstance().getCredentials().isCredentialSet();
    }
    if (selectedService == OPENAI) {
      return OpenAICredentialsManager.getInstance().getCredentials().isCredentialSet();
    }
    return true;
  }
}
