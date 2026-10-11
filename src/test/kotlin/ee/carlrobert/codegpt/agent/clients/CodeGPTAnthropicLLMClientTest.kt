package ee.carlrobert.codegpt.agent.clients

import ai.koog.http.client.ktor.KtorKoogHttpClient
import ai.koog.prompt.Prompt
import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.anthropic.AnthropicClientSettings
import ai.koog.prompt.executor.clients.anthropic.AnthropicParams
import ai.koog.prompt.executor.clients.anthropic.models.AnthropicThinking
import ai.koog.prompt.message.Message
import ai.koog.prompt.message.MessagePart
import ai.koog.prompt.message.ResponseMetaInfo
import ai.koog.prompt.params.LLMParams
import ai.koog.prompt.streaming.StreamFrame
import com.sun.net.httpserver.HttpServer
import ee.carlrobert.codegpt.settings.models.Fable_5_1
import ee.carlrobert.codegpt.settings.models.Haiku_5_5
import ee.carlrobert.codegpt.settings.models.Opus_4_6
import ee.carlrobert.codegpt.settings.models.Opus_5_5
import ee.carlrobert.codegpt.settings.models.Sonnet_5_5
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache5.Apache5
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.net.InetSocketAddress
import java.util.concurrent.CopyOnWriteArrayList

class CodeGPTAnthropicLLMClientTest {

    private lateinit var server: HttpServer
    private val requests = CopyOnWriteArrayList<JsonObject>()
    private var response: Pair<String, String> = TEXT_RESPONSE

    @Before
    fun setUp() {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/v1/messages") { exchange ->
            requests += Json.parseToJsonElement(
                exchange.requestBody.readAllBytes().decodeToString()
            ).jsonObject
            val (contentType, payload) = response
            val bytes = payload.encodeToByteArray()
            exchange.responseHeaders.add("Content-Type", contentType)
            val streaming = contentType == "text/event-stream"
            exchange.sendResponseHeaders(200, if (streaming) 0 else bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }
        server.start()
    }

    @After
    fun tearDown() {
        server.stop(0)
    }

    @Test
    fun adaptiveModelsOmitUnsupportedParamsWhenStreaming() = runBlocking<Unit> {
        for (model in listOf(Fable_5_1, Opus_5_5, Sonnet_5_5)) {
            requests.clear()
            response = THINKING_STREAM

            createClient().executeStreaming(legacyAgentPrompt(), model, emptyList()).toList()

            val body = requests.single()
            assertThat(body["model"]!!.jsonPrimitive.content).isEqualTo(model.id)
            assertThat(body).doesNotContainKeys(
                "thinking", "temperature", "top_p", "top_k", "tool_choice"
            )
            assertThat(body["max_tokens"]!!.jsonPrimitive.content).isEqualTo("128000")
        }
    }

    @Test
    fun configuredReasoningSettingsAreSent() = runBlocking<Unit> {
        response = THINKING_STREAM

        createClient(reasoningEffort = "xhigh", showReasoningSummary = true)
            .executeStreaming(legacyAgentPrompt(), Opus_5_5, emptyList()).toList()

        val body = requests.single()
        assertThat(body["output_config"].toString()).isEqualTo("""{"effort":"xhigh"}""")
        assertThat(body["thinking"].toString())
            .isEqualTo("""{"type":"adaptive","display":"summarized"}""")
    }

    @Test
    fun streamedThinkingKeepsTextAndSignature() = runBlocking<Unit> {
        response = THINKING_STREAM

        val reasoning = createClient()
            .executeStreaming(legacyAgentPrompt(), Opus_5_5, emptyList()).toList()
            .filterIsInstance<StreamFrame.ReasoningComplete>()
            .single()

        assertThat(reasoning.content).containsExactly("Checking the file.")
        assertThat(reasoning.encrypted).isEqualTo("sig")
    }

    @Test
    fun haikuKeepsForcedToolChoice() = runBlocking<Unit> {
        createClient().execute(legacyAgentPrompt(), Haiku_5_5, emptyList())

        val body = requests.single()
        assertThat(body).doesNotContainKeys("thinking", "temperature", "top_p", "top_k")
        assertThat(body["tool_choice"]!!.jsonObject["type"]!!.jsonPrimitive.content).isEqualTo("any")
        assertThat(body["max_tokens"]!!.jsonPrimitive.content).isEqualTo("16000")
    }

    @Test
    fun legacyModelsKeepRequestedParams() = runBlocking<Unit> {
        response = THINKING_STREAM

        createClient().executeStreaming(legacyAgentPrompt(), Opus_4_6, emptyList()).toList()

        val body = requests.single()
        assertThat(body["thinking"]!!.jsonObject["type"]!!.jsonPrimitive.content).isEqualTo("enabled")
        assertThat(body).containsKeys("temperature", "top_p", "top_k", "tool_choice")
    }

    @Test
    fun signedThinkingIsReplayed() = runBlocking<Unit> {
        createClient().execute(
            conversationWith(MessagePart.Reasoning(content = listOf(""), encrypted = "sig")),
            Opus_5_5,
            emptyList()
        )

        assertThat(assistantContentTypes(requests.single())).containsExactly("thinking", "text")
    }

    @Test
    fun unsignedThinkingFromStreamingIsDropped() = runBlocking<Unit> {
        createClient().execute(
            conversationWith(MessagePart.Reasoning(content = emptyList())),
            Opus_5_5,
            emptyList()
        )

        assertThat(assistantContentTypes(requests.single())).containsExactly("text")
    }

    private fun createClient(
        reasoningEffort: String? = null,
        showReasoningSummary: Boolean = false,
    ) = CodeGPTAnthropicLLMClient(
        apiKey = "TEST_API_KEY",
        settings = AnthropicClientSettings(
            modelVersionsMap = listOf(Fable_5_1, Opus_5_5, Sonnet_5_5, Haiku_5_5, Opus_4_6)
                .associateWith { it.id },
            baseUrl = "http://127.0.0.1:${server.address.port}"
        ),
        httpClientFactory = KtorKoogHttpClient.Factory(baseClient = HttpClient(Apache5)),
        reasoningEffort = { reasoningEffort },
        showReasoningSummary = { showReasoningSummary },
    )

    private fun legacyAgentPrompt(): Prompt {
        val params = AnthropicParams(
            temperature = 0.7,
            topP = 0.9,
            topK = 40,
            thinking = AnthropicThinking.Enabled(budgetTokens = 1024),
            toolChoice = LLMParams.ToolChoice.Required,
        )
        return prompt("test", params) { user("hi") }
    }

    private fun conversationWith(reasoning: MessagePart.Reasoning): Prompt {
        val first = prompt("test") { user("first") }
        val assistant = Message.Assistant(
            parts = listOf(reasoning, MessagePart.Text("answer")),
            metaInfo = ResponseMetaInfo.Empty,
        )
        val second = prompt("test") { user("second") }
        return Prompt(first.messages + assistant + second.messages, "test", first.params)
    }

    private fun assistantContentTypes(body: JsonObject): List<String> {
        return body["messages"]!!.jsonArray[1].jsonObject["content"]!!.jsonArray
            .map { it.jsonObject["type"]!!.jsonPrimitive.content }
    }

    private companion object {
        val TEXT_RESPONSE = "application/json" to """
            {"id":"msg_1","type":"message","role":"assistant","model":"claude-opus-5-5",
             "content":[{"type":"text","text":"ok"}],"stop_reason":"end_turn",
             "usage":{"input_tokens":1,"output_tokens":1}}
        """.trimIndent()

        // A thinking block arrives as text deltas followed by a signature delta.
        val THINKING_STREAM = "text/event-stream" to listOf(
            """{"type":"message_start","message":{"id":"msg_1","type":"message","role":"assistant","model":"claude-opus-5-5","content":[],"usage":{"input_tokens":1,"output_tokens":0}}}""",
            """{"type":"content_block_start","index":0,"content_block":{"type":"thinking","thinking":"","signature":""}}""",
            """{"type":"content_block_delta","index":0,"delta":{"type":"thinking_delta","thinking":"Checking "}}""",
            """{"type":"content_block_delta","index":0,"delta":{"type":"thinking_delta","thinking":"the file."}}""",
            """{"type":"content_block_delta","index":0,"delta":{"type":"signature_delta","signature":"sig"}}""",
            """{"type":"content_block_stop","index":0}""",
            """{"type":"content_block_start","index":1,"content_block":{"type":"text","text":""}}""",
            """{"type":"content_block_delta","index":1,"delta":{"type":"text_delta","text":"Hello"}}""",
            """{"type":"content_block_stop","index":1}""",
            """{"type":"message_delta","delta":{"stop_reason":"end_turn"},"usage":{"output_tokens":5}}""",
            """{"type":"message_stop"}""",
        ).joinToString("") { "data: $it\n\n" }
    }
}
