package ee.carlrobert.chatgpt.client.official.chat.request;

public class RequestMessage {

  private final String role;
  private final String content;

  public RequestMessage(String role, String content) {
    this.role = role;
    this.content = content;
  }

  public String getRole() {
    return role;
  }

  public String getContent() {
    return content;
  }
}
