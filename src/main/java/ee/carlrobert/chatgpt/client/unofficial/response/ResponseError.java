package ee.carlrobert.chatgpt.client.unofficial.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseError {

  private ResponseErrorDetails detail;

  public ResponseErrorDetails getDetail() {
    return detail;
  }

  public void setDetail(ResponseErrorDetails detail) {
    this.detail = detail;
  }
}
