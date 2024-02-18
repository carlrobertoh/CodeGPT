package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.settings.service.ServiceType.AZURE;
import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP;
import static ee.carlrobert.codegpt.settings.service.ServiceType.OPENAI;
import static ee.carlrobert.codegpt.settings.service.ServiceType.YOU;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.codecompletions.CodeCompletionRequestProvider;
import ee.carlrobert.codegpt.codecompletions.InfillRequestDetails;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
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
      CompletionEventListener<String> eventListener) {
    var requestProvider = new CompletionRequestProvider(callParameters.getConversation());
    switch (GeneralSettings.getCurrentState().getSelectedService()) {
      case OPENAI:
        var openAISettings = OpenAISettings.getCurrentState();
        var customModel = openAISettings.getCustomModel();
        return CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(
            requestProvider.buildOpenAIChatCompletionRequest(
                customModel.trim().isEmpty() ? openAISettings.getModel() : customModel,
                callParameters,
                useContextualSearch,
                openAISettings.isUsingCustomPath() ? openAISettings.getPath() : null),
            eventListener);
      case AZURE:
        var azureSettings = AzureSettings.getCurrentState();
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
      CompletionEventListener<String> eventListener) {
    var requestProvider = new CodeCompletionRequestProvider(requestDetails);
    switch (GeneralSettings.getCurrentState().getSelectedService()) {
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
      CompletionEventListener<String> eventListener) {
    var request = new OpenAIChatCompletionRequest.Builder(List.of(
        new OpenAIChatCompletionMessage("system",
            ConfigurationSettings.getCurrentState().getCommitMessagePrompt()),
        new OpenAIChatCompletionMessage("user", prompt)))
        .setModel(OpenAISettings.getCurrentState().getModel())
        .build();
    var selectedService = GeneralSettings.getCurrentState().getSelectedService();
    if (selectedService == OPENAI) {
      CompletionClientProvider.getOpenAIClient().getChatCompletionAsync(request, eventListener);
    }
    if (selectedService == AZURE) {
      CompletionClientProvider.getAzureClient().getChatCompletionAsync(request, eventListener);
    }
  }

  public Optional<String> getLookupCompletion(String prompt) {
    var selectedService = GeneralSettings.getCurrentState().getSelectedService();
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
    var selectedService = GeneralSettings.getCurrentState().getSelectedService();
    if (selectedService == AZURE) {
      return AzureCredentialsManager.getInstance().isCredentialSet();
    }
    if (selectedService == OPENAI) {
      return OpenAICredentialManager.getInstance().isCredentialSet();
    }
    return true;
  }
}
