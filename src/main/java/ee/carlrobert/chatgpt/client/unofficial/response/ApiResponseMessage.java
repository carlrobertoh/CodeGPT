package ee.carlrobert.chatgpt.client.unofficial.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseMessage {

  private String id;
  private ApiResponseMessageAuthor author;
  private ApiResponseMessageContent content;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ApiResponseMessageAuthor getAuthor() {
    return author;
  }

  public void setAuthor(ApiResponseMessageAuthor author) {
    this.author = author;
  }

  public ApiResponseMessageContent getContent() {
    return content;
  }

  public void setContent(ApiResponseMessageContent content) {
    this.content = content;
  }
}
