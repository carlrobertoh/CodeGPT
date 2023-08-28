package ee.carlrobert.codegpt.settings;

import java.util.Map;

public class CustomServicePresetDetails {

  private final String description;
  private final String url;
  private final String httpMethod;
  private final Map<String, String> headers;
  private final Map<String, String> queryParams;
  private final String bodyJson;
  private final String responseJson;

  public CustomServicePresetDetails(
      String description,
      String url,
      String httpMethod,
      Map<String, String> headers,
      Map<String, String> queryParams,
      String bodyJson,
      String responseJson) {
    this.description = description;
    this.url = url;
    this.httpMethod = httpMethod;
    this.headers = headers;
    this.queryParams = queryParams;
    this.bodyJson = bodyJson;
    this.responseJson = responseJson;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url;
  }

  public String getHttpMethod() {
    return httpMethod;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public Map<String, String> getQueryParams() {
    return queryParams;
  }

  public String getBodyJson() {
    return bodyJson;
  }

  public String getResponseJson() {
    return responseJson;
  }
}
