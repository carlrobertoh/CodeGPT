package ee.carlrobert.codegpt.codecompletions

import com.intellij.openapi.editor.VisualPosition
import ee.carlrobert.codegpt.CodeGPTKeys.REMAINING_EDITOR_COMPLETION
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent
import ee.carlrobert.llm.client.http.RequestEntity
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange
import ee.carlrobert.llm.client.util.JSONUtil.e
import ee.carlrobert.llm.client.util.JSONUtil.jsonMapResponse
import org.assertj.core.api.Assertions.assertThat
import testsupport.IntegrationTest

class CodeCompletionServiceTest : IntegrationTest() {

    private val cursorPosition = VisualPosition(3, 0)

    fun testFetchCodeCompletionLlama() {
        useLlamaService()
        LlamaSettings.getCurrentState().isCodeCompletionsEnabled = true
        myFixture.configureByText(
            "CompletionTest.java",
            getResourceContent("/codecompletions/code-completion-file.txt")
        )
        myFixture.editor.caretModel.moveToVisualPosition(cursorPosition)
        val expectedCompletion = "TEST_OUTPUT"
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
                .isEqualTo(
                    InfillPromptTemplate.CODE_LLAMA.buildPrompt(
                        InfillRequest.Builder(prefix, suffix).build()
                    )
                )
            listOf(
                jsonMapResponse(
                    e("content", expectedCompletion),
                    e("stop", true)
                )
            )
        })

        myFixture.type('c')

        waitExpecting { "TEST_OUTPUT" == REMAINING_EDITOR_COMPLETION[myFixture.editor] }
    }
}
