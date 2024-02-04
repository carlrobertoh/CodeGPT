package ee.carlrobert.codegpt.settings.state;

import static java.lang.String.format;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.completions.llama.CustomLlamaModel;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_GeneralSettings_210", storages = @Storage("CodeGPT_GeneralSettings_210.xml"))
public class GeneralSettingsState implements PersistentStateComponent<GeneralSettingsState> {

  private String email = "";
  private String displayName = "";
  private boolean previouslySignedIn;
  private ServiceType selectedService = ServiceType.OPENAI;

  public GeneralSettingsState() {
  }

  public static GeneralSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(GeneralSettingsState.class);
  }

  @Override
  public GeneralSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull GeneralSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void sync(Conversation conversation) {
    var clientCode = conversation.getClientCode();
    if ("chat.completion".equals(clientCode)) {
      setSelectedService(ServiceType.OPENAI);
      OpenAISettings.getInstance().getState()
          .setModel(OpenAIChatCompletionModel.findByCode(conversation.getModel()));
    }
    if ("azure.chat.completion".equals(clientCode)) {
      setSelectedService(ServiceType.AZURE);
    }
    if ("llama.chat.completion".equals(clientCode)) {
      setSelectedService(ServiceType.LLAMA_CPP);
      var llamaSettings = LlamaSettings.getInstance().getState();
      try {
        llamaSettings.getLocalSettings()
            .setModel(HuggingFaceModel.valueOf(conversation.getModel()));
      } catch (IllegalArgumentException ignore) {
        llamaSettings.getLocalSettings().setModel(new CustomLlamaModel(conversation.getModel()));
      }
    }
    if ("you.chat.completion".equals(clientCode)) {
      setSelectedService(ServiceType.YOU);
    }
  }

  public String getModel() {
    switch (selectedService) {
      case OPENAI:
        return OpenAISettings.getInstance().getState().getModel().getCode();
      case AZURE:
        return AzureSettings.getInstance().getState().getDeploymentId();
      case YOU:
        return "YouCode";
      case LLAMA_CPP:
        var llamaSettings = LlamaSettings.getInstance().getState();
        LlamaCompletionModel usedModel = llamaSettings.getUsedModel();
        if (usedModel instanceof CustomLlamaModel) {
          CustomLlamaModel customModel = (CustomLlamaModel) usedModel;
          var filePath = customModel.getModelPath();
          int lastSeparatorIndex = filePath.lastIndexOf('/');
          if (lastSeparatorIndex == -1) {
            return filePath;
          }
          return filePath.substring(lastSeparatorIndex + 1);
        }
        var huggingFaceModel = (HuggingFaceModel) usedModel;
        var llamaModel = LlamaModel.findByHuggingFaceModel(huggingFaceModel);
        return format(
            "%s %dB (Q%d)",
            llamaModel.getLabel(),
            huggingFaceModel.getParameterSize(),
            huggingFaceModel.getQuantization());
      default:
        return "Unknown";
    }
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getDisplayName() {
    if (displayName == null || displayName.isEmpty()) {
      var systemUserName = System.getProperty("user.name");
      if (systemUserName == null || systemUserName.isEmpty()) {
        return "User";
      }
      return systemUserName;
    }
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public boolean isPreviouslySignedIn() {
    return previouslySignedIn;
  }

  public void setPreviouslySignedIn(boolean previouslySignedIn) {
    this.previouslySignedIn = previouslySignedIn;
  }

  public ServiceType getSelectedService() {
    return selectedService;
  }

  public void setSelectedService(ServiceType selectedService) {
    this.selectedService = selectedService;
  }
}
