package ee.carlrobert.chatgpt.client;

public enum BaseModel {

  ADA("text-ada-001", "Ada - Fastest"),
  BABBAGE("text-babbage-001", "Babbage - Powerful"),
  CURIE("text-curie-001", "Curie - Fast and efficient"),
  DAVINCI("text-davinci-003", "Davinci - Most powerful (Default)");

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
