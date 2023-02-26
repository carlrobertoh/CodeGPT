package ee.carlrobert.chatgpt.client;

import java.util.Map;

public class ApiRequestDetails {

  private final String url;
  private final Map<String, Object> body;
  private final String token;

  public ApiRequestDetails(String url, Map<String, Object> body, String token) {
    this.url = url;
    this.body = body;
    this.token = token;
  }

  public String getUrl() {
    return url;
  }

  public Map<String, Object> getBody() {
    return body;
  }

  public String getToken() {
    return token;
  }
}
