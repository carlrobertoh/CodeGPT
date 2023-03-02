package ee.carlrobert.chatgpt.client;

public enum BaseModel {

  ADA("text-ada-001", "Ada - Fastest"),
  BABBAGE("text-babbage-001", "Babbage - Powerful"),
  CURIE("text-curie-001", "Curie - Fast and efficient"),
  DAVINCI("text-davinci-003", "Davinci - Most powerful (Default)"),
  CHATGPT("gpt-3.5-turbo", "ChatGPT - Most recent and capable model (Default)"),
  CHATGPT_SNAPSHOT("gpt-3.5-turbo-0301", "ChatGPT - Snapshot of gpt-3.5-turbo from March 1st 2023");

  private final String model;
  private final String description;

  BaseModel(String model, String description) {
    this.model = model;
    this.description = description;
  }

  public String getModel() {
    return model;
  }

  public String getDescription() {
    return description;
  }
}
