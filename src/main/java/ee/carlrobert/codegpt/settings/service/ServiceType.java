package ee.carlrobert.codegpt.settings.service;

import ee.carlrobert.codegpt.CodeGPTBundle;

public enum ServiceType {
  OPENAI("OPENAI", CodeGPTBundle.get("service.openai.title"), "chat.completion", "openai"),
  AZURE("AZURE", CodeGPTBundle.get("service.azure.title"), "azure.chat.completion", "azure"),
  YOU("YOU", CodeGPTBundle.get("service.you.title"), "you.chat.completion", "you"),
  LLAMA("LLAMA", CodeGPTBundle.get("service.llama.title"), "llama.chat.completion",
      "llama");

  private final String code;
  private final String label;
  private final String completionCode;
  private final String bundlePrefix;

  ServiceType(String code, String label, String completionCode, String bundlePrefix) {
    this.code = code;
    this.label = label;
    this.completionCode = completionCode;
    this.bundlePrefix = bundlePrefix;
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

  public String getBundlePrefix() {
    return bundlePrefix;
  }

  @Override
  public String toString() {
    return label;
  }
}
