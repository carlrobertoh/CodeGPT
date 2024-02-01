package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.OpenAiServiceSelectionForm;
import ee.carlrobert.codegpt.settings.service.ServicesSelectionForm;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_OpenAISettings_210", storages = @Storage("CodeGPT_OpenAISettings_210.xml"))
public class OpenAISettingsState extends RemoteSettings<OpenAICredentialsManager> implements
    PersistentStateComponent<OpenAISettingsState> {

  private static final String BASE_PATH = "/v1/chat/completions";

  private String organization = "";
  private String model = OpenAIChatCompletionModel.GPT_3_5.getCode();
  private boolean openAIQuotaExceeded;

  public OpenAISettingsState() {
    super("https://api.openai.com", BASE_PATH);
  }

  public static OpenAISettingsState getInstance() {
    OpenAISettingsState service = ApplicationManager.getApplication()
        .getService(OpenAISettingsState.class);
    service.setCredentialsManager(new OpenAICredentialsManager());
    return service;
  }

  @Override
  public OpenAISettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull OpenAISettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isModified(OpenAiServiceSelectionForm serviceSelectionForm) {
    return super.isModified(serviceSelectionForm.getOpenAIBaseHost(),
        serviceSelectionForm.getOpenAIPath())
        || credentialsManager.isModified(serviceSelectionForm.getOpenAIApiKey())
        || !serviceSelectionForm.getOpenAIOrganization().equals(organization)
        || !serviceSelectionForm.getOpenAIModel().equals(model);
  }

  public void apply(OpenAiServiceSelectionForm serviceSelectionForm) {
    organization = serviceSelectionForm.getOpenAIOrganization();
    baseHost = serviceSelectionForm.getOpenAIBaseHost();
    path = serviceSelectionForm.getOpenAIPath();
    model = serviceSelectionForm.getOpenAIModel();
  }

  public void reset(OpenAiServiceSelectionForm serviceSelectionForm) {
    serviceSelectionForm.setOpenAIApiKey(credentialsManager.getApiKey());
    serviceSelectionForm.setOpenAIOrganization(organization);
    serviceSelectionForm.setOpenAIBaseHost(baseHost);
    serviceSelectionForm.setOpenAIPath(path);
    serviceSelectionForm.setOpenAIModel(model);
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

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public boolean isOpenAIQuotaExceeded() {
    return openAIQuotaExceeded;
  }

  public void setOpenAIQuotaExceeded(boolean openAIQuotaExceeded) {
    this.openAIQuotaExceeded = openAIQuotaExceeded;
  }
}
