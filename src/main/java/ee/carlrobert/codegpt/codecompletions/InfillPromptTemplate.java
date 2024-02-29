package ee.carlrobert.codegpt.codecompletions;

import static java.lang.String.format;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public enum InfillPromptTemplate {

  OPENAI("OpenAI") {
    @Override
    public String buildPrompt(String prefix, String suffix) {
      return format("<|fim_prefix|> %s <|fim_suffix|>%s <|fim_middle|>", prefix, suffix);
    }
  },
  LLAMA("Llama", List.of("<EOT>")) {
    @Override
    public String buildPrompt(String prefix, String suffix) {
      return format("<PRE> %s <SUF>%s <MID>", prefix, suffix);
    }
  },
  STABILITY("Stability AI", List.of("<|endoftext|>")) {
    @Override
    public String buildPrompt(String prefix, String suffix) {
      return format("<fim_prefix>%s<fim_suffix>%s<fim_middle>", prefix, suffix);
    }
  },
  DEEPSEEK_CODER("DeepSeek Coder", List.of("<|EOT|>")) {
    @Override
    public String buildPrompt(String prefix, String suffix) {
      return format("<｜fim▁begin｜>%s<｜fim▁hole｜>%s<｜fim▁end｜>", prefix, suffix);
    }
  };

  private final String label;
  private final @Nullable List<String> stopTokens;

  InfillPromptTemplate(String label) {
    this(label, null);
  }

  InfillPromptTemplate(String label, @Nullable List<String> stopTokens) {
    this.label = label;
    this.stopTokens = stopTokens;
  }

  public abstract String buildPrompt(String prefix, String suffix);

  @Override
  public String toString() {
    return label;
  }

  public List<String> getStopTokens() {
    return stopTokens;
  }
}
