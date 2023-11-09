package ee.carlrobert.codegpt.completions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.llm.completion.CompletionEventListener;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;

@Service
public final class CompletionRequestService {

  private CompletionRequestService() {
  }

  public static CompletionRequestService getInstance() {
    return ApplicationManager.getApplication().getService(CompletionRequestService.class);
  }

  public EventSource getChatCompletionAsync(
      @NotNull Conversation conversation,
      @NotNull Message message,
      boolean retry,
      boolean useContextualSearch,
      CompletionEventListener eventListener) {
    var requestProvider = new CompletionRequestProvider(conversation);
    switch (SettingsState.getInstance().getSelectedService()) {
      case OPENAI:
        var openAISettings = OpenAISettingsState.getInstance();
        return CompletionClientProvider.getOpenAIClient().getChatCompletion(
            requestProvider.buildOpenAIChatCompletionRequest(
                openAISettings.getModel(),
                message,
                retry,
                useContextualSearch,
                openAISettings.isUsingCustomPath() ? openAISettings.getPath() : null),
            eventListener);
      case AZURE:
        var azureSettings = AzureSettingsState.getInstance();
        return CompletionClientProvider.getAzureClient().getChatCompletion(
            requestProvider.buildOpenAIChatCompletionRequest(
                null,
                message,
                retry,
                useContextualSearch,
                azureSettings.isUsingCustomPath() ? azureSettings.getPath() : null),
            eventListener);
      case YOU:
        return CompletionClientProvider.getYouClient().getChatCompletion(
            requestProvider.buildYouCompletionRequest(message),
            eventListener);
      case LLAMA_CPP:
        return CompletionClientProvider.getLlamaClient().getChatCompletion(
            requestProvider.buildLlamaCompletionRequest(message),
            eventListener);
      default:
        throw new IllegalArgumentException();
    }
  }

  public boolean isRequestAllowed() {
    var selectedService = SettingsState.getInstance().getSelectedService();
    if (selectedService == ServiceType.AZURE) {
      return AzureCredentialsManager.getInstance().isCredentialSet();
    }
    if (selectedService == ServiceType.OPENAI) {
      return OpenAICredentialsManager.getInstance().isApiKeySet();
    }
    return true;
  }
}
