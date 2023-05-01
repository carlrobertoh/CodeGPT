package ee.carlrobert.codegpt.state.conversations.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class Message {

  private final UUID id;
  private final String prompt;
  private String response;

  public Message(String prompt, String response) {
    this(prompt);
    this.response = response;
  }

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public Message(@JsonProperty("prompt") String prompt) {
    this.id = UUID.randomUUID();
    this.prompt = prompt;
  }

  public UUID getId() {
    return id;
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
