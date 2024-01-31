package ee.carlrobert.codegpt.settings.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServicesSelectionForm;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
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

  public boolean isModified(ServicesSelectionForm servicesSelectionForm) {
    return OpenAICredentialsManager.getInstance().isModified(servicesSelectionForm.getOpenAIApiKey())
        || !servicesSelectionForm.getOpenAIOrganization().equals(organization)
        || !servicesSelectionForm.getOpenAIBaseHost().equals(baseHost)
        || !servicesSelectionForm.getOpenAIPath().equals(path)
        || !servicesSelectionForm.getOpenAIModel().equals(model);
  }

  public void apply(ServicesSelectionForm servicesSelectionForm) {
    organization = servicesSelectionForm.getOpenAIOrganization();
    baseHost = servicesSelectionForm.getOpenAIBaseHost();
    path = servicesSelectionForm.getOpenAIPath();
    model = servicesSelectionForm.getOpenAIModel();
  }

  public void reset(ServicesSelectionForm servicesSelectionForm) {
    servicesSelectionForm.setOpenAIApiKey(OpenAICredentialsManager.getInstance().getApiKey());
    servicesSelectionForm.setOpenAIOrganization(organization);
    servicesSelectionForm.setOpenAIBaseHost(baseHost);
    servicesSelectionForm.setOpenAIPath(path);
    servicesSelectionForm.setOpenAIModel(model);
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
