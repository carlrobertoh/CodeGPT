package ee.carlrobert.codegpt.settings.service;

import ee.carlrobert.codegpt.CodeGPTBundle;

public enum ServiceType {
  OPENAI("OPENAI", CodeGPTBundle.get("service.openai.title")),
  AZURE("AZURE", CodeGPTBundle.get("service.azure.title")),
  YOU("YOU", CodeGPTBundle.get("service.you.title")),
  LLAMA_CPP("LLAMA_CPP", CodeGPTBundle.get("service.llama.title"));

  private final String code;
  private final String label;

  ServiceType(String code, String label) {
    this.code = code;
    this.label = label;
  }

  public String getCode() {
    return code;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }
}
