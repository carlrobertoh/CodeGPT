package ee.carlrobert.codegpt.settings.state;

import ee.carlrobert.codegpt.completions.PromptTemplate;

public class CommonSettings {

  private PromptTemplate promptTemplate = PromptTemplate.LLAMA;

  public CommonSettings() {
  }

  public CommonSettings(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }

  public boolean isModified(CommonSettings commonSettings){
    return !promptTemplate.equals(commonSettings.getPromptTemplate());
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplate;
  }

  public void setPromptTemplate(PromptTemplate promptTemplate) {
    this.promptTemplate = promptTemplate;
  }
}
