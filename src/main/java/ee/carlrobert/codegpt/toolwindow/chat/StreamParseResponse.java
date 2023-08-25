package ee.carlrobert.codegpt.toolwindow.chat;

public class StreamParseResponse {

  private final StreamResponseType type;
  private final String response;

  public StreamParseResponse(StreamResponseType type, String response) {
    this.type = type;
    this.response = response;
  }

  public StreamResponseType getType() {
    return type;
  }

  public String getResponse() {
    return response;
  }
}
