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

    assertThat(prompt).isEqualTo("""
            <<SYS>>TEST_SYSTEM_PROMPT<</SYS>>
            [INST]TEST_PREV_PROMPT_1[/INST]
            TEST_PREV_RESPONSE_1
            [INST]TEST_PREV_PROMPT_2[/INST]
            TEST_PREV_RESPONSE_2
            [INST]TEST_USER_PROMPT[/INST]""");
  }

  @Test
  public void shouldBuildLlamaPromptWithoutHistory() {
    var prompt = LLAMA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, List.of());

    assertThat(prompt).isEqualTo("""
            <<SYS>>TEST_SYSTEM_PROMPT<</SYS>>
            [INST]TEST_USER_PROMPT[/INST]""");
  }

  @Test
  public void shouldBuildAlpacaPromptWithHistory() {
    var prompt = ALPACA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY);

    assertThat(prompt).isEqualTo("""
            Below is an instruction that describes a task. \
            Write a response that appropriately completes the request.

            ### Instruction
            TEST_PREV_PROMPT_1

            ### Response:
            TEST_PREV_RESPONSE_1

            ### Instruction
            TEST_PREV_PROMPT_2

            ### Response:
            TEST_PREV_RESPONSE_2

            ### Instruction
            TEST_USER_PROMPT

            ### Response:
            """);
  }

  @Test
  public void shouldBuildAlpacaPromptWithoutHistory() {
    var prompt = ALPACA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, List.of());

    assertThat(prompt).isEqualTo("""
            Below is an instruction that describes a task. \
            Write a response that appropriately completes the request.

            ### Instruction
            TEST_USER_PROMPT

            ### Response:
            """);
  }

  @Test
  public void shouldBuildChatMLPromptWithHistory() {
    var prompt = CHAT_ML.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY);

    assertThat(prompt).isEqualTo("""
            <|im_start|>system
            TEST_SYSTEM_PROMPT<|im_end|>
            <|im_start|>user
            TEST_PREV_PROMPT_1<|im_end|>
            <|im_start|>assistant
            TEST_PREV_RESPONSE_1<|im_end|>
            <|im_start|>user
            TEST_PREV_PROMPT_2<|im_end|>
            <|im_start|>assistant
            TEST_PREV_RESPONSE_2<|im_end|>
            <|im_start|>user
            TEST_USER_PROMPT<|im_end|>"""
    );
  }

  @Test
  public void shouldBuildChatMLPromptWithoutHistory() {
    var prompt = CHAT_ML.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, List.of());

    assertThat(prompt).isEqualTo("""
            <|im_start|>system
            TEST_SYSTEM_PROMPT<|im_end|>
            <|im_start|>user
            TEST_USER_PROMPT<|im_end|>""");
  }

  @Test
  public void shouldBuildToRAPromptWithHistory() {
    var prompt = TORA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY);

    assertThat(prompt).isEqualTo("""
            <|user|>
            TEST_PREV_PROMPT_1
            <|assistant|>
            TEST_PREV_RESPONSE_1
            <|user|>
            TEST_PREV_PROMPT_2
            <|assistant|>
            TEST_PREV_RESPONSE_2
            <|user|>
            TEST_USER_PROMPT
            <|assistant|>"""
    );
  }

  @Test
  public void shouldBuildToRAPromptWithoutHistory() {
    var prompt = TORA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, List.of());

    assertThat(prompt).isEqualTo("""
            <|user|>
            TEST_USER_PROMPT
            <|assistant|>"""
    );
  }
}
