package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.OpenAiServiceForm;
import ee.carlrobert.codegpt.settings.state.util.RemoteWithModelSettings;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_OpenAISettings_210", storages = @Storage("CodeGPT_OpenAISettings_210.xml"))
public class OpenAISettingsState extends
    RemoteWithModelSettings<OpenAICredentialsManager, OpenAIChatCompletionModel> implements
    PersistentStateComponent<OpenAISettingsState> {

  private static final String BASE_PATH = "/v1/chat/completions";

  private String organization = "";
  private boolean openAIQuotaExceeded;

  public OpenAISettingsState() {
    super("https://api.openai.com", BASE_PATH, OpenAIChatCompletionModel.GPT_3_5,
        new OpenAICredentialsManager());
  }

  public static OpenAISettingsState getInstance() {
    return ApplicationManager.getApplication()
        .getService(OpenAISettingsState.class);
  }

  @Override
  public OpenAISettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull OpenAISettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isModified(OpenAiServiceForm serviceSelectionForm) {
    return super.isModified(serviceSelectionForm.getRemoteWithModelSettings())
        || credentialsManager.isModified(serviceSelectionForm.getApiKey())
        || !serviceSelectionForm.getOrganization().equals(organization);
  }

  public void apply(OpenAiServiceForm serviceSelectionForm) {
    var remoteSettings = serviceSelectionForm.getRemoteWithModelSettings();
    baseHost = remoteSettings.getBaseHost();
    path = remoteSettings.getPath();
    setModel(remoteSettings.getModel());
    organization = serviceSelectionForm.getOrganization();
    credentialsManager.apply(serviceSelectionForm.getApiKey());
  }

  public void reset(OpenAiServiceForm serviceSelectionForm) {
    serviceSelectionForm.setRemoteWithModelSettings(
        new RemoteWithModelSettings<>(baseHost, path, model, credentialsManager));
    serviceSelectionForm.setApiKey(credentialsManager.getApiKey());
    serviceSelectionForm.setOrganization(organization);
  }

  public boolean isUsingCustomPath() {
    return !BASE_PATH.equals(path);
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public boolean isOpenAIQuotaExceeded() {
    return openAIQuotaExceeded;
  }

  public void setOpenAIQuotaExceeded(boolean openAIQuotaExceeded) {
    this.openAIQuotaExceeded = openAIQuotaExceeded;
  }
}
