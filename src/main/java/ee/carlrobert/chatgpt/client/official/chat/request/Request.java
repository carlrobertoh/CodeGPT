package ee.carlrobert.chatgpt.client.official.chat.request;

import java.util.List;

public class Request {

  private final String model;
  private final boolean stream;
  private final List<RequestMessage> messages;

  public Request(String model, boolean stream, List<RequestMessage> messages) {
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

  public List<RequestMessage> getMessages() {
    return messages;
  }
}
