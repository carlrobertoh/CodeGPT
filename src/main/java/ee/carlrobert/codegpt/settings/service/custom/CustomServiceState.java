package ee.carlrobert.codegpt.settings.service.custom;

import static ee.carlrobert.codegpt.settings.service.custom.CustomServiceTemplate.OPENAI;

import java.util.Map;
import java.util.Objects;

public class CustomServiceState {

  private String url = OPENAI.getUrl();
  private Map<String, String> headers = OPENAI.getHeaders();
  private Map<String, ?> body = OPENAI.getBody();
  private CustomServiceTemplate template = OPENAI;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public Map<String, ?> getBody() {
    return body;
  }

  public void setBody(Map<String, ?> body) {
    this.body = body;
  }

  public CustomServiceTemplate getTemplate() {
    return template;
  }

  public void setTemplate(CustomServiceTemplate template) {
    this.template = template;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomServiceState that = (CustomServiceState) o;
    return Objects.equals(url, that.url)
        && Objects.equals(headers, that.headers)
        && Objects.equals(body, that.body)
        && template == that.template;
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, headers, body, template);
  }
}
