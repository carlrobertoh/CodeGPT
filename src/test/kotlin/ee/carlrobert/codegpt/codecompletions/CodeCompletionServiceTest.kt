package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.session.InlineCompletionSession.Companion.getOrNull
import com.intellij.openapi.editor.VisualPosition
import com.intellij.testFramework.PlatformTestUtil
import ee.carlrobert.codegpt.CodeGPTKeys.REMAINING_EDITOR_COMPLETION
import ee.carlrobert.codegpt.util.file.FileUtil
import ee.carlrobert.llm.client.http.RequestEntity
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange
import ee.carlrobert.llm.client.util.JSONUtil.*
import org.assertj.core.api.Assertions.assertThat
import testsupport.IntegrationTest

class CodeCompletionServiceTest : IntegrationTest() {

    fun `test code completion with CodeGPT provider`() {
        useCodeGPTService()
        myFixture.configureByText(
            "CompletionTest.java",
            FileUtil.getResourceContent("/codecompletions/code-completion-file.txt")
        )
        myFixture.editor.caretModel.moveToVisualPosition(VisualPosition(3, 0))
        val prefix = """
             ${"z".repeat(245)}
             [INPUT]
             p
             """.trimIndent() // 128 tokens
        val suffix = """
             
             [\INPUT]
             ${"z".repeat(247)}
             """.trimIndent() // 128 tokens
        expectCodeGPT(StreamHttpExchange { request: RequestEntity ->
            assertThat(request.uri.path).isEqualTo("/v1/code/completions")
            assertThat(request.method).isEqualTo("POST")
            assertThat(request.body)
                .extracting("model", "prefix", "suffix", "fileExtension")
                .containsExactly("TEST_CODE_MODEL", prefix, suffix, "java")
            listOf(
                jsonMapResponse("choices", jsonArray(jsonMap("text", "ublic "))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", "void"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", " main")))
            )
        })

        myFixture.type('p')

        assertInlineSuggestion("Failed to display initial inline suggestion.") {
            "ublic void main" == it
        }
    }

    fun `test code completion with OpenAI provider`() {
        useOpenAIService()
        myFixture.configureByText(
            "CompletionTest.java",
            FileUtil.getResourceContent("/codecompletions/code-completion-file.txt")
        )
        myFixture.editor.caretModel.moveToVisualPosition(VisualPosition(3, 0))
        val prefix = """
             ${"z".repeat(245)}
             [INPUT]
             p
             """.trimIndent() // 128 tokens
        val suffix = """
             
             [\INPUT]
             ${"z".repeat(247)}
             """.trimIndent() // 128 tokens
        expectOpenAI(StreamHttpExchange { request: RequestEntity ->
            assertThat(request.uri.path).isEqualTo("/v1/completions")
            assertThat(request.method).isEqualTo("POST")
            assertThat(request.body)
                .extracting("model", "prompt", "suffix", "max_tokens")
                .containsExactly("gpt-3.5-turbo-instruct", prefix, suffix, 128)
            listOf(
                jsonMapResponse("choices", jsonArray(jsonMap("text", "ublic "))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", "void"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", " main")))
            )
        })

        myFixture.type('p')

        assertInlineSuggestion("Failed to display initial inline suggestion.") {
            "ublic void main" == it
        }
    }

    fun `test fetching next code completion`() {
        useCodeGPTService()
        myFixture.configureByText("CompletionTest.java", "")
        expectCodeGPT(StreamHttpExchange { request: RequestEntity ->
            assertThat(request.uri.path).isEqualTo("/v1/code/completions")
            assertThat(request.method).isEqualTo("POST")
            assertThat(request.body)
                .extracting("model", "prefix", "suffix", "fileExtension")
                .containsExactly("TEST_CODE_MODEL", "p", "", "java")
            listOf(
                jsonMapResponse("choices", jsonArray(jsonMap("text", "ublic static"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", " void main(String"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", "[] args) {\n"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", "    System.out.print"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", "ln(\"Hello, Worl"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", "d!\");\n"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", "}\n"))),
                jsonMapResponse(
                    "choices",
                    jsonArray(jsonMap("text", "private static int getX() {\n"))
                )
            )
        })
        myFixture.type('p')
        assertInlineSuggestion("Failed to display initial inline suggestion.") {
            "ublic static void main(String[] args) {\n    System.out.println(\"Hello, World!\");" == it
        }
        assertThat(REMAINING_EDITOR_COMPLETION.get(myFixture.editor)).isEqualTo(
            "ublic static void main(String[] args) {\n" +
                    "    System.out.println(\"Hello, World!\");\n" +
                    "}\n" +
                    "private static int getX() {"
        )
        expectCodeGPT(StreamHttpExchange { request: RequestEntity ->
            assertThat(request.uri.path).isEqualTo("/v1/code/completions")
            assertThat(request.method).isEqualTo("POST")
            assertThat(request.body)
                .extracting("model", "prefix", "suffix", "fileExtension")
                .containsExactly(
                    "TEST_CODE_MODEL",
                    "public static void main(String[] args) {\n" +
                            "    System.out.println(\"Hello, World!\");\n" +
                            "}\n" +
                            "private static int getX() {",
                    "",
                    "java"
                )
            listOf(
                jsonMapResponse("choices", jsonArray(jsonMap("text", "\n    retur"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", "n 10;\n"))),
                jsonMapResponse("choices", jsonArray(jsonMap("text", "}\n"))),
            )
        })

        myFixture.type('\t')

        PlatformTestUtil.waitWithEventsDispatching(
            "Failed to retrieve next completion",
            {
                REMAINING_EDITOR_COMPLETION.get(myFixture.editor) == "\n}\nprivate static int getX() {"
            },
            10
        )
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