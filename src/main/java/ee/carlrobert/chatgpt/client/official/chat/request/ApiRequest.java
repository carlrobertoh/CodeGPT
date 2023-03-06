package ee.carlrobert.chatgpt.client.official.chat.request;

import java.util.List;

public class ApiRequest {

  private final String model;
  private final boolean stream;
  private final List<ApiRequestMessage> messages;

  public ApiRequest(String model, boolean stream, List<ApiRequestMessage> messages) {
    this.model = model;
    this.stream = stream;
    this.messages = messages;
  }

  public String getModel() {
    return model;
  }

  public boolean isStream() {
    return stream;
  }

  public List<ApiRequestMessage> getMessages() {
    return messages;
  }
}
