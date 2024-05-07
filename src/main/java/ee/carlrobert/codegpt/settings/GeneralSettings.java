package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.google.GoogleSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_GeneralSettings_210", storages = @Storage("CodeGPT_GeneralSettings_210.xml"))
public class GeneralSettings implements PersistentStateComponent<GeneralSettingsState> {

  private GeneralSettingsState state = new GeneralSettingsState();

  @Override
  @NotNull
  public GeneralSettingsState getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull GeneralSettingsState state) {
    this.state = state;
  }

  public static GeneralSettingsState getCurrentState() {
    return getInstance().getState();
  }

  public static GeneralSettings getInstance() {
    return ApplicationManager.getApplication().getService(GeneralSettings.class);
  }

  public void sync(Conversation conversation) {
    var clientCode = conversation.getClientCode();
    if ("chat.completion".equals(clientCode)) {
      state.setSelectedService(ServiceType.OPENAI);
      OpenAISettings.getCurrentState().setModel(conversation.getModel());
    }
    if ("anthropic.chat.completion".equals(clientCode)) {
      state.setSelectedService(ServiceType.ANTHROPIC);
      AnthropicSettings.getCurrentState().setModel(conversation.getModel());
    }
    if ("azure.chat.completion".equals(clientCode)) {
      state.setSelectedService(ServiceType.AZURE);
    }
    if ("custom.openai.chat.completion".equals(clientCode)) {
      state.setSelectedService(ServiceType.CUSTOM_OPENAI);
    }
    if ("llama.chat.completion".equals(clientCode)) {
      state.setSelectedService(ServiceType.LLAMA_CPP);
      var llamaSettings = LlamaSettings.getCurrentState();
      try {
        var huggingFaceModel = HuggingFaceModel.valueOf(conversation.getModel());
        llamaSettings.setHuggingFaceModel(huggingFaceModel);
        llamaSettings.setUseCustomModel(false);
      } catch (IllegalArgumentException ignore) {
        llamaSettings.setCustomLlamaModelPath(conversation.getModel());
        llamaSettings.setUseCustomModel(true);
      }
    }
    if ("you.chat.completion".equals(clientCode)) {
      state.setSelectedService(ServiceType.YOU);
    }
    if ("ollama.chat.completion".equals(clientCode)) {
      state.setSelectedService(ServiceType.OLLAMA);
    }
  }

  public String getModel() {
    switch (state.getSelectedService()) {
      case OPENAI:
        return OpenAISettings.getCurrentState().getModel();
      case ANTHROPIC:
        return AnthropicSettings.getCurrentState().getModel();
      case AZURE:
        return AzureSettings.getCurrentState().getDeploymentId();
      case YOU:
        return "YouCode";
      case LLAMA_CPP:
        var llamaSettings = LlamaSettings.getCurrentState();
        if (llamaSettings.isUseCustomModel()) {
          var filePath = llamaSettings.getCustomLlamaModelPath();
          int lastSeparatorIndex = filePath.lastIndexOf('/');
          if (lastSeparatorIndex == -1) {
            return filePath;
          }
          return filePath.substring(lastSeparatorIndex + 1);
        }
        var huggingFaceModel = llamaSettings.getHuggingFaceModel();
        var llamaModel = LlamaModel.findByHuggingFaceModel(huggingFaceModel);
        return String.format(
            "%s %dB (Q%d)",
            llamaModel.getLabel(),
            huggingFaceModel.getParameterSize(),
            huggingFaceModel.getQuantization());
      case OLLAMA:
        return ApplicationManager.getApplication()
            .getService(OllamaSettings.class)
            .getState()
            .getModel();
      case GOOGLE:
        return ApplicationManager.getApplication()
            .getService(GoogleSettings.class)
            .getState()
            .getModel();
      default:
        return "Unknown";
    }
  }
}
