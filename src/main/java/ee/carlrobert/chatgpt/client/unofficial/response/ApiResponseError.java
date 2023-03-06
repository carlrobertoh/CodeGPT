package ee.carlrobert.chatgpt.client.unofficial.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseError {

  private ApiResponseErrorDetails detail;

  public ApiResponseErrorDetails getDetail() {
    return detail;
  }

  public void setDetail(ApiResponseErrorDetails detail) {
    this.detail = detail;
  }
}
