package ee.carlrobert.codegpt.settings.service.openai;

import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.util.Objects;

public class OpenAISettingsState {

  private String organization = "";
  private String model = OpenAIChatCompletionModel.GPT_3_5_0125_16k.getCode();
  private boolean codeCompletionsEnabled = true;
  private int codeCompletionMaxTokens = 128;

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public boolean isCodeCompletionsEnabled() {
    return codeCompletionsEnabled;
  }

  public void setCodeCompletionsEnabled(boolean codeCompletionsEnabled) {
    this.codeCompletionsEnabled = codeCompletionsEnabled;
  }

  public int getCodeCompletionMaxTokens() {
    return codeCompletionMaxTokens;
  }

  public void setCodeCompletionMaxTokens(int codeCompletionMaxTokens) {
    this.codeCompletionMaxTokens = codeCompletionMaxTokens;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenAISettingsState that = (OpenAISettingsState) o;
    return Objects.equals(organization, that.organization)
        && Objects.equals(model, that.model)
        && codeCompletionsEnabled == that.codeCompletionsEnabled
        && codeCompletionMaxTokens == that.codeCompletionMaxTokens;
  }

  @Override
  public int hashCode() {
    return Objects.hash(organization, model, codeCompletionsEnabled, codeCompletionMaxTokens);
  }
}
