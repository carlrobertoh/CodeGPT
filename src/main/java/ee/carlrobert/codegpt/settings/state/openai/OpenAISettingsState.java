package ee.carlrobert.codegpt.settings.state.openai;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Transient;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
import ee.carlrobert.codegpt.settings.state.util.RemoteSettings;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;

public class OpenAISettingsState extends RemoteSettings<ApiKeyCredentials> {

  private static final String BASE_PATH = "/v1/chat/completions";

  @OptionTag(converter = OpenAIModelConverter.class)
  protected OpenAIChatCompletionModel model = OpenAIChatCompletionModel.GPT_3_5;
  private String organization = "";
  private boolean openAIQuotaExceeded;

  public OpenAISettingsState() {
    super("https://api.openai.com", BASE_PATH, new ApiKeyCredentials());
  }

  public OpenAISettingsState(String baseHost, String path, OpenAIChatCompletionModel model,
      ApiKeyCredentials credentials, String organization,
      boolean openAIQuotaExceeded) {
    super(baseHost, path, credentials);
    this.model = model;
    this.organization = organization;
    this.openAIQuotaExceeded = openAIQuotaExceeded;
  }

  @Transient
  public boolean isModified(OpenAISettingsState settingsState) {
    return super.isModified(settingsState)
        || credentials.isModified(settingsState.getCredentials())
        || !StringUtil.equals(organization, settingsState.getOrganization())
        || !model.equals(settingsState.getModel());
  }

  public boolean isUsingCustomPath() {
    return !BASE_PATH.equals(path);
  }

  public OpenAIChatCompletionModel getModel() {
    return model;
  }

  public void setModel(OpenAIChatCompletionModel model) {
    this.model = model;
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
