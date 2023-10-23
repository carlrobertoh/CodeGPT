package ee.carlrobert.codegpt.completions.llama;

public enum PromptTemplate {

  CHAT_ML(
      "Chat Markup Language (ChatML)",
      "<|im_start|>system\n"
          + "{system_prompt}\n"
          + "<|im_start|>user\n"
          + "{prompt}<|im_end|>\n"),
  LLAMA("Llama", "<s>[INST] {prompt} [/INST]</s>"),
  TORA(
      "ToRA",
      "<|user|>\n"
          + "{prompt}\n"
          + "<|assistant|>"),
  ALPACA(
      "Alpaca/Vicuna",
      "### System Prompt\n"
          + "{system_prompt}\n"
          + "\n"
          + "### User Message\n"
          + "{prompt}\n"
          + "\n"
          + "### Assistant");

  private final String label;
  private final String promptTemplate;

  PromptTemplate(String label, String promptTemplate) {
    this.label = label;
    this.promptTemplate = promptTemplate;
  }

  public String getLabel() {
    return label;
  }

  public String getPromptTemplate() {
    return promptTemplate;
  }

  public String buildPrompt(String systemPrompt, String userPrompt) {
    return promptTemplate
        .replace("{system_prompt}", systemPrompt)
        .replace("{prompt}", userPrompt);
  }

  @Override
  public String toString() {
    return label;
  }
}
