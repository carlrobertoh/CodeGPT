package ee.carlrobert.chatgpt.client.response;

import java.util.List;

public class ApiResponse {

  private String id;
  private String object;
  private long created;
  private String model;
  private List<ApiResponseChoice> choices;
  private ApiResponseUsage usage;
  private ApiError error;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public long getCreated() {
    return created;
  }

  public void setCreated(long created) {
    this.created = created;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public List<ApiResponseChoice> getChoices() {
    return choices;
  }

  public void setChoices(List<ApiResponseChoice> choices) {
    this.choices = choices;
  }

  public ApiResponseUsage getUsage() {
    return usage;
  }

  public void setUsage(ApiResponseUsage usage) {
    this.usage = usage;
  }

  public ApiError getError() {
    return error;
  }

  public void setError(ApiError error) {
    this.error = error;
  }
}
