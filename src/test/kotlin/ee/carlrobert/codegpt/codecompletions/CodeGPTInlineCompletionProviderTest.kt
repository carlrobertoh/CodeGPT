package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.codeInsight.inline.completion.TypingEvent
import com.intellij.openapi.util.TextRange
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import org.assertj.core.api.Assertions.assertThat
import testsupport.IntegrationTest

class CodeGPTInlineCompletionProviderTest : IntegrationTest() {

  private val provider = CodeGPTInlineCompletionProvider()

  fun testIsEnabled() {
    myFixture.configureByText("file", "")
    val event = InlineCompletionEvent.DocumentChange(TypingEvent.NewLine("a", TextRange(0, 0)), myFixture.editor)

    // Services which are supported
    useOpenAIService()
    assertThat(provider.isEnabled(event)).isTrue() // default: true
    OpenAISettings.getCurrentState().isCodeCompletionsEnabled = false
    assertThat(provider.isEnabled(event)).isFalse()

    useLlamaService()
    assertThat(provider.isEnabled(event)).isTrue() // default: true
    LlamaSettings.getCurrentState().isCodeCompletionsEnabled = false
    assertThat(provider.isEnabled(event)).isFalse()

    // Services which are not supported yet: Should be disabled
    useAzureService()
    assertThat(provider.isEnabled(event)).isFalse()

    useYouService()
    assertThat(provider.isEnabled(event)).isFalse()

    useCustomOpenAIService()
    assertThat(provider.isEnabled(event)).isFalse()

    useAnthropicService()
    assertThat(provider.isEnabled(event)).isFalse()

    // Disable all events which are not a DocumentChange
    assertThat(provider.isEnabled(NotDocumentChange())).isFalse()
  }

  class NotDocumentChange : InlineCompletionEvent {
    override fun toRequest(): InlineCompletionRequest? {
      return null
    }

  }

}
