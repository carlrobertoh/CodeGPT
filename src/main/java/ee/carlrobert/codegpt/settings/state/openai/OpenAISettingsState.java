package ee.carlrobert.codegpt.settings.state.openai;

import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.OpenAiServiceForm;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;

public class OpenAISettingsState extends OpenAIRemoteSettings<OpenAICredentialsManager> {

  private static final String BASE_PATH = "/v1/chat/completions";

  private String organization = "";
  private boolean openAIQuotaExceeded;

  public OpenAISettingsState() {
    super("https://api.openai.com", BASE_PATH, OpenAIChatCompletionModel.GPT_3_5,
        new OpenAICredentialsManager());
  }

  public OpenAISettingsState(String baseHost, String path, OpenAIChatCompletionModel model,
      OpenAICredentialsManager credentialsManager, String organization,
      boolean openAIQuotaExceeded) {
    super(baseHost, path, model, credentialsManager);
    this.organization = organization;
    this.openAIQuotaExceeded = openAIQuotaExceeded;
  }

  @Transient
  public boolean isModified(OpenAISettingsState settingsState) {
    return super.isModified(settingsState)
        || credentialsManager.isModified(settingsState.getCredentialsManager().getApiKey())
        || !settingsState.getOrganization().equals(organization);
  }

  public void apply(OpenAISettingsState settingsState) {
    baseHost = settingsState.getBaseHost();
    path = settingsState.getPath();
    setModel(settingsState.getModel());
    organization = settingsState.getOrganization();
    credentialsManager.apply(settingsState.getCredentialsManager().getApiKey());
  }

  public void reset(OpenAiServiceForm serviceSelectionForm) {
    serviceSelectionForm.setRemoteWithModelSettings(
        new OpenAIRemoteSettings<>(baseHost, path, model, credentialsManager));
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
