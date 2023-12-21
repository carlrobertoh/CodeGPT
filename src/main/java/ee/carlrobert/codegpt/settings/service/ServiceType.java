package ee.carlrobert.codegpt.settings.service;

import ee.carlrobert.codegpt.CodeGPTBundle;

public enum ServiceType {
  OPENAI("OPENAI", CodeGPTBundle.get("service.openai.title"), "chat.completion"),
  AZURE("AZURE", CodeGPTBundle.get("service.azure.title"), "azure.chat.completion"),
  YOU("YOU", CodeGPTBundle.get("service.you.title"), "you.chat.completion"),
  LLAMA_CPP("LLAMA_CPP", CodeGPTBundle.get("service.llama.title"), "llama.chat.completion");

  private final String code;
  private final String label;
  private final String completionCode;

  ServiceType(String code, String label, String completionCode) {
    this.code = code;
    this.label = label;
    this.completionCode = completionCode;
  }

  public String getCode() {
    return code;
  }

  public String getLabel() {
    return label;
  }

  public String getCompletionCode() {
    return completionCode;
  }

  @Override
  public String toString() {
    return label;
  }
}
