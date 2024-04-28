package ee.carlrobert.codegpt.completions

import ee.carlrobert.codegpt.completions.llama.PromptTemplate.ALPACA
import ee.carlrobert.codegpt.completions.llama.PromptTemplate.CHAT_ML
import ee.carlrobert.codegpt.completions.llama.PromptTemplate.LLAMA
import ee.carlrobert.codegpt.completions.llama.PromptTemplate.LLAMA_3
import ee.carlrobert.codegpt.completions.llama.PromptTemplate.PHI_3
import ee.carlrobert.codegpt.completions.llama.PromptTemplate.TORA
import ee.carlrobert.codegpt.conversations.message.Message
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource

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
  fun shouldBuildLlama3PromptWithoutHistory() {
    val prompt = LLAMA_3.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, listOf())

    assertThat(prompt).isEqualTo("""
      <|begin_of_text|><|start_header_id|>system<|end_header_id|>

      TEST_SYSTEM_PROMPT<|eot_id|><|start_header_id|>user<|end_header_id|>

      TEST_USER_PROMPT<|eot_id|><|start_header_id|>assistant<|end_header_id|>""".trimIndent()
    )
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = [" ", "\t", "\n"])
  fun shouldBuildLlama3PromptWithoutHistorySkippingBlankSystemPrompt(systemPrompt: String?) {
    val prompt = LLAMA_3.buildPrompt(systemPrompt, USER_PROMPT, listOf())

    assertThat(prompt).isEqualTo("""
      <|begin_of_text|><|start_header_id|>user<|end_header_id|>

      TEST_USER_PROMPT<|eot_id|><|start_header_id|>assistant<|end_header_id|>""".trimIndent()
    )
  }

  @Test
  fun shouldBuildLlama3PromptWithHistory() {
    val prompt = LLAMA_3.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY)

    assertThat(prompt).isEqualTo("""
      <|begin_of_text|><|start_header_id|>system<|end_header_id|>

      TEST_SYSTEM_PROMPT<|eot_id|><|start_header_id|>user<|end_header_id|>
  
      TEST_PREV_PROMPT_1<|eot_id|><|start_header_id|>assistant<|end_header_id|>

      TEST_PREV_RESPONSE_1<|eot_id|><|start_header_id|>user<|end_header_id|>

      TEST_PREV_PROMPT_2<|eot_id|><|start_header_id|>assistant<|end_header_id|>

      TEST_PREV_RESPONSE_2<|eot_id|><|start_header_id|>user<|end_header_id|>

      TEST_USER_PROMPT<|eot_id|><|start_header_id|>assistant<|end_header_id|>""".trimIndent())
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = [" ", "\t", "\n"])
  fun shouldBuildLlama3PromptWithHistorySkippingBlankSystemPrompt(systemPrompt: String?) {
    val prompt = LLAMA_3.buildPrompt(systemPrompt, USER_PROMPT, HISTORY)

    assertThat(prompt).isEqualTo("""
      <|begin_of_text|><|start_header_id|>user<|end_header_id|>
  
      TEST_PREV_PROMPT_1<|eot_id|><|start_header_id|>assistant<|end_header_id|>

      TEST_PREV_RESPONSE_1<|eot_id|><|start_header_id|>user<|end_header_id|>

      TEST_PREV_PROMPT_2<|eot_id|><|start_header_id|>assistant<|end_header_id|>

      TEST_PREV_RESPONSE_2<|eot_id|><|start_header_id|>user<|end_header_id|>

      TEST_USER_PROMPT<|eot_id|><|start_header_id|>assistant<|end_header_id|>""".trimIndent())
  }

  @Test
  fun shouldBuildPhi3PromptWithoutHistory() {
    val prompt = PHI_3.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, listOf())

    assertThat(prompt).isEqualTo("""
      <|user|>
      TEST_USER_PROMPT<|end|>
      <|assistant|>""".trimIndent()
    )
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = [" ", "\t", "\n"])
  fun shouldBuildPhi3PromptWithoutHistorySkippingBlankSystemPrompt(systemPrompt: String?) {
    val prompt = PHI_3.buildPrompt(systemPrompt, USER_PROMPT, listOf())

    assertThat(prompt).isEqualTo("""
      <|user|>
      TEST_USER_PROMPT<|end|>
      <|assistant|>""".trimIndent()
    )
  }

  @Test
  fun shouldBuildPhi3PromptWithHistory() {
    val prompt = PHI_3.buildPrompt(SYSTEM_PROMPT, USER_PROMPT, HISTORY)

    assertThat(prompt).isEqualTo("""
      <|user|>
      TEST_PREV_PROMPT_1<|end|>
      <|assistant|>
      TEST_PREV_RESPONSE_1<|end|>
      <|user|>
      TEST_PREV_PROMPT_2<|end|>
      <|assistant|>
      TEST_PREV_RESPONSE_2<|end|>
      <|user|>
      TEST_USER_PROMPT<|end|>
      <|assistant|>""".trimIndent())
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = [" ", "\t", "\n"])
  fun shouldBuildPhi3PromptWithHistorySkippingBlankSystemPrompt(systemPrompt: String?) {
    val prompt = PHI_3.buildPrompt(systemPrompt, USER_PROMPT, HISTORY)

    assertThat(prompt).isEqualTo("""
      <|user|>
      TEST_PREV_PROMPT_1<|end|>
      <|assistant|>
      TEST_PREV_RESPONSE_1<|end|>
      <|user|>
      TEST_PREV_PROMPT_2<|end|>
      <|assistant|>
      TEST_PREV_RESPONSE_2<|end|>
      <|user|>
      TEST_USER_PROMPT<|end|>
      <|assistant|>""".trimIndent())
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
