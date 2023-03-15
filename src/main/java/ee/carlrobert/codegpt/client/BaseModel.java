package ee.carlrobert.codegpt.client;

public enum BaseModel {

  ADA("text-ada-001", "Ada - Fastest"),
  BABBAGE("text-babbage-001", "Babbage - Powerful"),
  CURIE("text-curie-001", "Curie - Fast and efficient"),
  DAVINCI("text-davinci-003", "Davinci - Most powerful (Default)"),
  CHATGPT_3_5("gpt-3.5-turbo", "ChatGPT - Most recent and capable model (Default)"),
  CHATGPT_3_5_SNAPSHOT("gpt-3.5-turbo-0301", "ChatGPT - Snapshot of gpt-3.5-turbo from March 1st 2023"),
  UNOFFICIAL_CHATGPT("text-davinci-002-render-sha", "Unofficial ChatGPT");

  private final String code;
  private final String description;

  BaseModel(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }
}
