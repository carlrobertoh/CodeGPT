package ee.carlrobert.codegpt.settings.service.openai;

import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;

public class OpenAISettingsState {

  private static final String BASE_PATH = "/v1/chat/completions";

  private String organization = "";
  private String baseHost = "https://api.openai.com";
  private String path = BASE_PATH;
  private String model = OpenAIChatCompletionModel.GPT_3_5.getCode();
  private boolean openAIQuotaExceeded;

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
