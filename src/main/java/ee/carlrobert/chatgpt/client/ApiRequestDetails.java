package ee.carlrobert.chatgpt.client;

public class ApiRequestDetails {

  private final String url;
  private final Object body;
  private final String token;

  public ApiRequestDetails(String url, Object body, String token) {
    this.url = url;
    this.body = body;
    this.token = token;
  }

  public String getUrl() {
    return url;
  }

  public Object getBody() {
    return body;
  }

  public String getToken() {
    return token;
  }
}
