package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.session.InlineCompletionSession.Companion.getOrNull
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.util.TextRange
import com.intellij.testFramework.PlatformTestUtil
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.codegpt.util.file.FileUtil
import ee.carlrobert.llm.client.http.RequestEntity
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange
import ee.carlrobert.llm.client.util.JSONUtil.e
import ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse
import org.assertj.core.api.Assertions.assertThat
import testsupport.IntegrationTest

class CodeCompletionServiceTest : IntegrationTest() {

    fun testApplyCompletionNextWordInlay() {
        useLlamaService(true)
        myFixture.configureByText(
            "CompletionTest.java",
            FileUtil.getResourceContent("/codecompletions/code-completion-file.txt")
        )
        myFixture.editor.caretModel.moveToVisualPosition(VisualPosition(3, 0))
        val expectedCompletion = "public void main"
        val prefix = """
             ${"z".repeat(245)}
             [INPUT]
             c
             """.trimIndent() // 128 tokens
        val suffix = """
             
             [\INPUT]
             ${"z".repeat(247)}
             """.trimIndent() // 128 tokens
        expectLlama(StreamHttpExchange { request: RequestEntity ->
            assertThat(request.uri.path).isEqualTo("/completion")
            assertThat(request.method).isEqualTo("POST")
            assertThat(request.body)
                .extracting("prompt")
                .isEqualTo(InfillPromptTemplate.CODE_LLAMA.buildPrompt(prefix, suffix))
            listOf(
                jsonMapResponse(
                    e("content", expectedCompletion),
                    e("stop", true)
                )
            )
        })
        myFixture.type('c')
        assertInlineSuggestion("Failed to display initial inline suggestion.") {
            expectedCompletion == it
        }
        val offsetBeforeApply = myFixture.editor.caretModel.offset

        myFixture.performEditorAction("codegpt.applyInlaysNextWord")

        assertInlineSuggestion("Failed to display next partial inline suggestion.") {
            myFixture.run {
                val appliedText =
                    editor.document.getText(TextRange(offsetBeforeApply, editor.caretModel.offset))
                "public" == appliedText && " void main" == it
            }
        }
    }

    private fun assertInlineSuggestion(errorMessage: String, onAssert: (String) -> Boolean) {
        PlatformTestUtil.waitWithEventsDispatching(
            errorMessage,
            {
                val session = getOrNull(myFixture.editor) ?: return@waitWithEventsDispatching false
                onAssert(session.context.textToInsert())
            },
            5
        )
    }
}