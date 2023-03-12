package ee.carlrobert.codegpt.client.unofficial.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseMessageContent {

  private final String contentType;
  private final List<String> parts;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ApiResponseMessageContent(String contentType, List<String> parts) {
    this.contentType = contentType;
    this.parts = parts;
  }

  public String getContentType() {
    return contentType;
  }

  public List<String> getParts() {
    return parts;
  }
}
