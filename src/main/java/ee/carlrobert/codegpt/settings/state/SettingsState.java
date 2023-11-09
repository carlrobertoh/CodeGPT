package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.completions.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_GeneralSettings_210", storages = @Storage("CodeGPT_GeneralSettings_210.xml"))
public class SettingsState implements PersistentStateComponent<SettingsState> {

  private String email = "";
  private String displayName = "";
  private boolean previouslySignedIn;
  private ServiceType selectedService = ServiceType.OPENAI;

  public SettingsState() {
  }

  public static SettingsState getInstance() {
    return ApplicationManager.getApplication().getService(SettingsState.class);
  }

  @Override
  public SettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull SettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public void sync(Conversation conversation) {
    var clientCode = conversation.getClientCode();
    if ("chat.completion".equals(clientCode)) {
      setSelectedService(ServiceType.OPENAI);
      OpenAISettingsState.getInstance().setModel(conversation.getModel());
    }
    if ("azure.chat.completion".equals(clientCode)) {
      setSelectedService(ServiceType.AZURE);
    }
    if ("llama.chat.completion".equals(clientCode)) {
      setSelectedService(ServiceType.LLAMA_CPP);
      var llamaSettings = LlamaSettingsState.getInstance();
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
      setSelectedService(ServiceType.YOU);
    }
  }

  public String getModel() {
    switch (selectedService) {
      case OPENAI:
        return OpenAISettingsState.getInstance().getModel();
      case AZURE:
        return AzureSettingsState.getInstance().getDeploymentId();
      case YOU:
        return "YouCode";
      case LLAMA_CPP:
        var llamaSettings = LlamaSettingsState.getInstance();
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
