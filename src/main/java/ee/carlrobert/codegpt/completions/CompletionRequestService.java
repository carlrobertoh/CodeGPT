package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionResponse;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.response.OpenAIChatCompletionResponse;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.util.List;
import java.util.Optional;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.Nullable;

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
    switch (SettingsState.getInstance().getSelectedService()) {
      case OPENAI:
        var openAISettings = OpenAISettingsState.getInstance();
        return CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(
            requestProvider.buildOpenAIChatCompletionRequest(
                openAISettings.getModel(),
                callParameters,
                useContextualSearch,
                openAISettings.isUsingCustomPath() ? openAISettings.getPath() : null),
            eventListener);
      case AZURE:
        var azureSettings = AzureSettingsState.getInstance();
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

  public @Nullable String getCodeCompletion(CallParameters callParameters) {
    var requestProvider = new CompletionRequestProvider(callParameters.getConversation());
    switch (SettingsState.getInstance().getSelectedService()) {
      case OPENAI:
        var openAISettings = OpenAISettingsState.getInstance();
        OpenAIChatCompletionResponse chatCompletion = CompletionClientProvider.getOpenAIClient()
            .getChatCompletion(
                requestProvider.buildOpenAICodeCompletionRequest(
                    openAISettings.getModel(),
                    callParameters,
                    openAISettings.isUsingCustomPath() ? openAISettings.getPath() : null));
        return chatCompletion.getChoices().get(0).getMessage().getContent();
      case AZURE:
        var azureSettings = AzureSettingsState.getInstance();
        OpenAIChatCompletionResponse azureResponse = CompletionClientProvider.getAzureClient()
            .getChatCompletion(
                requestProvider.buildOpenAICodeCompletionRequest(
                    null,
                    callParameters,
                    azureSettings.isUsingCustomPath() ? azureSettings.getPath() : null));
        return azureResponse.getChoices().get(0).getMessage().getContent();
      case LLAMA_CPP:
        LlamaCompletionResponse llamaResponse = CompletionClientProvider.getLlamaClient()
            .getInfill(requestProvider.buildLlamaInfillRequest(callParameters.getMessage()));
        return llamaResponse.getContent();
      default:
        throw new IllegalArgumentException();
    }
  }

  public void generateCommitMessageAsync(
      String prompt,
      CompletionEventListener eventListener) {
    var request = new OpenAIChatCompletionRequest.Builder(List.of(
        new OpenAIChatCompletionMessage("system",
            ConfigurationState.getInstance().getCommitMessagePrompt()),
        new OpenAIChatCompletionMessage("user", prompt)))
        .setModel(OpenAISettingsState.getInstance().getModel())
        .build();
    var selectedService = SettingsState.getInstance().getSelectedService();
    if (selectedService == OPENAI) {
      CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(request, eventListener);
    }
    if (selectedService == AZURE) {
      CompletionClientProvider.getAzureClient().getChatCompletionAsync(request, eventListener);
    }
  }

  public Optional<String> getLookupCompletion(String prompt) {
    var selectedService = SettingsState.getInstance().getSelectedService();
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
    var selectedService = SettingsState.getInstance().getSelectedService();
    if (selectedService == AZURE) {
      return AzureCredentialsManager.getInstance().isCredentialSet();
    }
    if (selectedService == ServiceType.OPENAI) {
      return OpenAICredentialsManager.getInstance().isApiKeySet();
    }
    return true;
  }
}
