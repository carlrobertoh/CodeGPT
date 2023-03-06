package ee.carlrobert.chatgpt.client.official.chat.request;

public class ApiRequestMessage {

  private final String role;
  private final String content;

  public ApiRequestMessage(String role, String content) {
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
