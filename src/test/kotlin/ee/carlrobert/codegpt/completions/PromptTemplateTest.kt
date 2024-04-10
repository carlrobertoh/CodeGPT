package ee.carlrobert.codegpt.completions

import ee.carlrobert.codegpt.completions.llama.PromptTemplate.ALPACA
import ee.carlrobert.codegpt.completions.llama.PromptTemplate.CHAT_ML
import ee.carlrobert.codegpt.completions.llama.PromptTemplate.LLAMA
import ee.carlrobert.codegpt.completions.llama.PromptTemplate.TORA
import ee.carlrobert.codegpt.conversations.message.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PromptTemplateTest {

  @Test
  fun shouldBuildLlamaPromptWithHistory() {
    val prompt = LLAMA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY)

    assertThat(prompt).isEqualTo("""
            <<SYS>>TEST_SYSTEM_PROMPT<</SYS>>
            [INST]TEST_PREV_PROMPT_1[/INST]
            TEST_PREV_RESPONSE_1
            [INST]TEST_PREV_PROMPT_2[/INST]
            TEST_PREV_RESPONSE_2
            [INST]TEST_USER_PROMPT[/INST]
            """.trimIndent())
  }

  @Test
  fun shouldBuildLlamaPromptWithoutHistory() {
    val prompt = LLAMA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, listOf())

    assertThat(prompt).isEqualTo("""
            <<SYS>>TEST_SYSTEM_PROMPT<</SYS>>
            [INST]TEST_USER_PROMPT[/INST]
            """.trimIndent())
  }

  @Test
  fun shouldBuildAlpacaPromptWithHistory() {
    val prompt = ALPACA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY)

    assertThat(prompt).isEqualTo("""
            Below is an instruction that describes a task. Write a response that appropriately completes the request.

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

            """.trimIndent())
  }

  @Test
  fun shouldBuildAlpacaPromptWithoutHistory() {
    val prompt = ALPACA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, listOf())

    assertThat(prompt).isEqualTo("""
            Below is an instruction that describes a task. Write a response that appropriately completes the request.

            ### Instruction
            TEST_USER_PROMPT

            ### Response:

            """.trimIndent())
  }

  @Test
  fun shouldBuildChatMLPromptWithHistory() {
    val prompt = CHAT_ML.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY)

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
            TEST_USER_PROMPT<|im_end|>
            """.trimIndent())
  }

  @Test
  fun shouldBuildChatMLPromptWithoutHistory() {
    val prompt = CHAT_ML.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, listOf())

    assertThat(prompt).isEqualTo("""
            <|im_start|>system
            TEST_SYSTEM_PROMPT<|im_end|>
            <|im_start|>user
            TEST_USER_PROMPT<|im_end|>
            """.trimIndent())
  }

  @Test
  fun shouldBuildToRAPromptWithHistory() {
    val prompt = TORA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY)

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
            <|assistant|>
            """.trimIndent())
  }

  @Test
  fun shouldBuildToRAPromptWithoutHistory() {
    val prompt = TORA.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, listOf())

    assertThat(prompt).isEqualTo("""
            <|user|>
            TEST_USER_PROMPT
            <|assistant|>
            """.trimIndent())
  }

  companion object {
    private const val SYSTEM_PROMPT = "TEST_SYSTEM_PROMPT"
    private const val USER_PROMPT = "TEST_USER_PROMPT"
    private val HISTORY: List<Message> = listOf(
      Message("TEST_PREV_PROMPT_1", "TEST_PREV_RESPONSE_1"),
      Message("TEST_PREV_PROMPT_2", "TEST_PREV_RESPONSE_2")
    )
  }
}
