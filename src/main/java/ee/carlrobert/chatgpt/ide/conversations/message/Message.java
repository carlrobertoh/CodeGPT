package ee.carlrobert.chatgpt.ide.conversations.message;

public class Message {

  private String prompt;
  private String response;

  public String getPrompt() {
    return prompt;
  }

  public void setPrompt(String prompt) {
    this.prompt = prompt;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }
}
