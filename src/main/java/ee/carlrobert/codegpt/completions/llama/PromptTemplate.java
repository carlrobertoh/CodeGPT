package ee.carlrobert.codegpt.completions.llama;

public enum PromptTemplate {

  CHAT_ML(
      "Chat Markup Language (ChatML)",
      "<|im_start|>system\n"
          + "{system_prompt}\n"
          + "<|im_start|>user\n"
          + "{prompt}<|im_end|>\n"),
  LLAMA("Llama", "[INST] {prompt} [/INST]"),
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
  private final String template;

  PromptTemplate(String label, String template) {
    this.label = label;
    this.template = template;
  }

  public String getLabel() {
    return label;
  }

  public String getTemplate() {
    return template;
  }

  public String buildPrompt(String systemPrompt, String userPrompt) {
    return template
        .replace("{system_prompt}", systemPrompt)
        .replace("{prompt}", userPrompt);
  }

  @Override
  public String toString() {
    return label;
  }
}
