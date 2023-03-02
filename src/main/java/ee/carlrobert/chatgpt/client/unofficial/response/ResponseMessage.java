package ee.carlrobert.chatgpt.client.unofficial.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMessage {

  private String id;
  private ResponseMessageAuthor author;
  private ResponseMessageContent content;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ResponseMessageAuthor getAuthor() {
    return author;
  }

  public void setAuthor(ResponseMessageAuthor author) {
    this.author = author;
  }

  public ResponseMessageContent getContent() {
    return content;
  }

  public void setContent(ResponseMessageContent content) {
    this.content = content;
  }
}
