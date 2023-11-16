package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceSelectionForm;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_OpenAISettings_210", storages = @Storage("CodeGPT_OpenAISettings_210.xml"))
public class OpenAISettingsState implements PersistentStateComponent<OpenAISettingsState> {

  private static final String BASE_PATH = "/v1/chat/completions";

  private String organization = "";
  private String baseHost = "https://api.openai.com";
  private String path = BASE_PATH;
  private String model = OpenAIChatCompletionModel.GPT_3_5.getCode();
  private boolean openAIQuotaExceeded;

  public static OpenAISettingsState getInstance() {
    return ApplicationManager.getApplication().getService(OpenAISettingsState.class);
  }

  @Override
  public OpenAISettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull OpenAISettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isModified(ServiceSelectionForm serviceSelectionForm) {
    return !serviceSelectionForm.getOpenAIApiKey()
        .equals(OpenAICredentialsManager.getInstance().getApiKey())
        || !serviceSelectionForm.getOpenAIOrganization().equals(organization)
        || !serviceSelectionForm.getOpenAIBaseHost().equals(baseHost)
        || !serviceSelectionForm.getOpenAIPath().equals(path)
        || !serviceSelectionForm.getOpenAIModel().equals(model);
  }

  public void apply(ServiceSelectionForm serviceSelectionForm) {
    organization = serviceSelectionForm.getOpenAIOrganization();
    baseHost = serviceSelectionForm.getOpenAIBaseHost();
    path = serviceSelectionForm.getOpenAIPath();
    model = serviceSelectionForm.getOpenAIModel();
  }

  public void reset(ServiceSelectionForm serviceSelectionForm) {
    serviceSelectionForm.setOpenAIApiKey(OpenAICredentialsManager.getInstance().getApiKey());
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

  public String getBaseHost() {
    return baseHost;
  }

  public void setBaseHost(String openAIBaseHost) {
    this.baseHost = openAIBaseHost;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public boolean isOpenAIQuotaExceeded() {
    return openAIQuotaExceeded;
  }

  public void setOpenAIQuotaExceeded(boolean openAIQuotaExceeded) {
    this.openAIQuotaExceeded = openAIQuotaExceeded;
  }
}
