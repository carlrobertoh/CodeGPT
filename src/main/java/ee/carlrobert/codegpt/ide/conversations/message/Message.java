package ee.carlrobert.codegpt.ide.conversations.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

  private final String prompt;
  private String response;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Message(@JsonProperty("prompt") String prompt) {
    this.prompt = prompt;
  }

  public Message(String prompt, String response) {
    this.prompt = prompt;
    this.response = response;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }
}
