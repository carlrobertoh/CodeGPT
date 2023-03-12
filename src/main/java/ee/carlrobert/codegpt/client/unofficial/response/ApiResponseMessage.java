package ee.carlrobert.codegpt.client.unofficial.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseMessage {

  private final String id;
  private final ApiResponseMessageAuthor author;
  private final ApiResponseMessageContent content;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ApiResponseMessage(
      @JsonProperty("id") String id,
      @JsonProperty("author") ApiResponseMessageAuthor author,
      @JsonProperty("content") ApiResponseMessageContent content) {
    this.id = id;
    this.author = author;
    this.content = content;
  }

  public String getId() {
    return id;
  }

  public ApiResponseMessageAuthor getAuthor() {
    return author;
  }

  public ApiResponseMessageContent getContent() {
    return content;
  }
}
