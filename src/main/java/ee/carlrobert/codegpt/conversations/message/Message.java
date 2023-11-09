package ee.carlrobert.codegpt.conversations.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Message {

  private final UUID id;
  private final String prompt;
  private String response;
  private String userMessage;
  private List<YouSerpResult> serpResults;

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

  public String getUserMessage() {
    return userMessage;
  }

  public void setUserMessage(String userMessage) {
    this.userMessage = userMessage;
  }

  public List<YouSerpResult> getSerpResults() {
    return serpResults;
  }

  public void setSerpResults(List<YouSerpResult> serpResults) {
    this.serpResults = serpResults;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Message)) {
      return false;
    }
    Message other = (Message) obj;
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, prompt);
  }
}
