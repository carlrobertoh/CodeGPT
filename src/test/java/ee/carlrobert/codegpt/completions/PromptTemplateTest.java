package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.completions.llama.PromptTemplate.ALPACA;
import static ee.carlrobert.codegpt.completions.llama.PromptTemplate.CHAT_ML;
import static ee.carlrobert.codegpt.completions.llama.PromptTemplate.LLAMA;
import static ee.carlrobert.codegpt.completions.llama.PromptTemplate.TORA;
import static org.assertj.core.api.Assertions.assertThat;

import ee.carlrobert.codegpt.conversations.message.Message;
import java.util.List;
import org.junit.Test;

public class PromptTemplateTest {

  private static final String SYSTEM_PROMPT = "TEST_SYSTEM_PROMPT";
  private static final String USER_PROMPT = "TEST_USER_PROMPT";
  private static final List<Message> HISTORY = List.of(
      new Message("TEST_PREV_PROMPT_1", "TEST_PREV_RESPONSE_1"),
      new Message("TEST_PREV_PROMPT_2", "TEST_PREV_RESPONSE_2"));

  @Test
  public void shouldBuildLlamaPromptWithHistory() {
    var prompt = LLAMA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY);

    assertThat(prompt).isEqualTo(
        "<<SYS>>TEST_SYSTEM_PROMPT<</SYS>>\n"
            + "[INST]TEST_PREV_PROMPT_1[/INST]\n"
            + "TEST_PREV_RESPONSE_1\n"
            + "[INST]TEST_PREV_PROMPT_2[/INST]\n"
            + "TEST_PREV_RESPONSE_2\n"
            + "[INST]TEST_USER_PROMPT[/INST]");
  }

  @Test
  public void shouldBuildLlamaPromptWithoutHistory() {
    var prompt = LLAMA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, List.of());

    assertThat(prompt).isEqualTo(
        "<<SYS>>TEST_SYSTEM_PROMPT<</SYS>>\n"
            + "[INST]TEST_USER_PROMPT[/INST]");
  }

  @Test
  public void shouldBuildAlpacaPromptWithHistory() {
    var prompt = ALPACA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY);

    assertThat(prompt).isEqualTo(
        "Below is an instruction that describes a task. "
            + "Write a response that appropriately completes the request.\n"
            + "\n"
            + "### Instruction\n"
            + "TEST_PREV_PROMPT_1\n"
            + "\n"
            + "### Response:\n"
            + "TEST_PREV_RESPONSE_1\n"
            + "\n"
            + "### Instruction\n"
            + "TEST_PREV_PROMPT_2\n"
            + "\n"
            + "### Response:\n"
            + "TEST_PREV_RESPONSE_2\n"
            + "\n"
            + "### Instruction\n"
            + "TEST_USER_PROMPT\n"
            + "\n"
            + "### Response:\n");
  }

  @Test
  public void shouldBuildAlpacaPromptWithoutHistory() {
    var prompt = ALPACA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, List.of());

    assertThat(prompt).isEqualTo(
        "Below is an instruction that describes a task. "
            + "Write a response that appropriately completes the request.\n"
            + "\n"
            + "### Instruction\n"
            + "TEST_USER_PROMPT\n"
            + "\n"
            + "### Response:\n");
  }

  @Test
  public void shouldBuildChatMLPromptWithHistory() {
    var prompt = CHAT_ML.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY);

    assertThat(prompt).isEqualTo(
        "<|im_start|>system\n"
            + "TEST_SYSTEM_PROMPT<|im_end|>\n"
            + "<|im_start|>user\n"
            + "TEST_PREV_PROMPT_1<|im_end|>\n"
            + "<|im_start|>assistant\n"
            + "TEST_PREV_RESPONSE_1<|im_end|>\n"
            + "<|im_start|>user\n"
            + "TEST_PREV_PROMPT_2<|im_end|>\n"
            + "<|im_start|>assistant\n"
            + "TEST_PREV_RESPONSE_2<|im_end|>\n"
            + "<|im_start|>user\n"
            + "TEST_USER_PROMPT<|im_end|>"
    );
  }

  @Test
  public void shouldBuildChatMLPromptWithoutHistory() {
    var prompt = CHAT_ML.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, List.of());

    assertThat(prompt).isEqualTo(
        "<|im_start|>system\n"
            + "TEST_SYSTEM_PROMPT<|im_end|>\n"
            + "<|im_start|>user\n"
            + "TEST_USER_PROMPT<|im_end|>");
  }

  @Test
  public void shouldBuildToRAPromptWithHistory() {
    var prompt = TORA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY);

    assertThat(prompt).isEqualTo(
        "<|user|>\n"
            + "TEST_PREV_PROMPT_1\n"
            + "<|assistant|>\n"
            + "TEST_PREV_RESPONSE_1\n"
            + "<|user|>\n"
            + "TEST_PREV_PROMPT_2\n"
            + "<|assistant|>\n"
            + "TEST_PREV_RESPONSE_2\n"
            + "<|user|>\n"
            + "TEST_USER_PROMPT\n"
            + "<|assistant|>"
    );
  }

  @Test
  public void shouldBuildToRAPromptWithoutHistory() {
    var prompt = TORA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, List.of());

    assertThat(prompt).isEqualTo(
        "<|user|>\n"
            + "TEST_USER_PROMPT\n"
            + "<|assistant|>"
    );
  }
}
