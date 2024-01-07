package ee.carlrobert.codegpt.settings.service;

import ee.carlrobert.codegpt.CodeGPTBundle;

public enum ServiceType {
//  OPENAI("OPENAI", CodeGPTBundle.get("service.openai.title"), "chat.completion", new FillInTheMiddle("<|fim_prefix|", "<|fim_suffix|>","<|fim_middle|>", "<|endoftext|>")),
  OPENAI("OPENAI", CodeGPTBundle.get("service.openai.title"), "chat.completion", new FillInTheMiddle("<PRE> ", "<SUF>"," <MID>", "<EOT>")),
  AZURE("AZURE", CodeGPTBundle.get("service.azure.title"), "azure.chat.completion", new FillInTheMiddle("<|fim_prefix|", "<|fim_suffix|>","<|fim_middle|>", "<|endoftext|>")),
  YOU("YOU", CodeGPTBundle.get("service.you.title"), "you.chat.completion", new FillInTheMiddle("", "TODO","", "")),
  LLAMA_CPP("LLAMA_CPP", CodeGPTBundle.get("service.llama.title"), "llama.chat.completion", new FillInTheMiddle("<PRE> ", "<SUF>"," <MID>", "<EOT"));

  private final String code;
  private final String label;
  private final String completionCode;
  private final FillInTheMiddle fillInTheMiddle;

  ServiceType(String code, String label, String completionCode, FillInTheMiddle fillInTheMiddle) {
    this.code = code;
    this.label = label;
    this.completionCode = completionCode;
    this.fillInTheMiddle = fillInTheMiddle;
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

  public FillInTheMiddle getFillInTheMiddle() {
    return fillInTheMiddle;
  }

  @Override
  public String toString() {
    return label;
  }
}
