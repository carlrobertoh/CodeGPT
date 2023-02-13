package ee.carlrobert.chatgpt.client.response;

public class ApiResponseChoice {

  private String text;
  private int index;
  private Object logprobs;
  private String finish_reason;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public Object getLogprobs() {
    return logprobs;
  }

  public void setLogprobs(Object logprobs) {
    this.logprobs = logprobs;
  }

  public String getFinish_reason() {
    return finish_reason;
  }

  public void setFinish_reason(String finish_reason) {
    this.finish_reason = finish_reason;
  }
}
