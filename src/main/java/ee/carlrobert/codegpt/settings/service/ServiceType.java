package ee.carlrobert.codegpt.settings.service;

import ee.carlrobert.codegpt.CodeGPTBundle;

public enum ServiceType {
  OPENAI("OPENAI", "service.openai.title", "chat.completion"),
  CUSTOM_OPENAI("CUSTOM_OPENAI", "service.custom.openai.title", "custom.openai.chat.completion"),
  AZURE("AZURE", "service.azure.title", "azure.chat.completion"),
  YOU("YOU", "service.you.title", "you.chat.completion"),
  LLAMA_CPP("LLAMA_CPP", "service.llama.title", "llama.chat.completion");

  private final String code;
  private final String label;
  private final String completionCode;

  ServiceType(String code, String messageKey, String completionCode) {
    this.code = code;
    this.label = CodeGPTBundle.get(messageKey);
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
