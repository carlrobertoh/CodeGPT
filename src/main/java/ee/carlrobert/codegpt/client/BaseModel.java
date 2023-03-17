package ee.carlrobert.codegpt.client;

public enum BaseModel {

  ADA("text-ada-001", "Ada - Fastest"),
  BABBAGE("text-babbage-001", "Babbage - Powerful"),
  CURIE("text-curie-001", "Curie - Fast and efficient"),
  DAVINCI("text-davinci-003", "Davinci - Most powerful (Default)"),
  CHATGPT("gpt-3.5-turbo", "ChatGPT(3.5) - Most capable model (Default)"),
  CHATGPT_SNAPSHOT("gpt-3.5-turbo-0301", "ChatGPT(3.5) - Snapshot of gpt-3.5-turbo from March 1st 2023"),
  CHATGPT_4("gpt-4", "ChatGPT(4.0) - Most recent model (Requires access which is behind the waitlist approval process)"),
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
