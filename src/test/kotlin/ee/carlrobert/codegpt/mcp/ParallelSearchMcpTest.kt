package ee.carlrobert.codegpt.mcp

import ai.koog.agents.core.tools.Tool
import com.intellij.openapi.components.service
import com.sun.net.httpserver.HttpServer
import ee.carlrobert.codegpt.agent.AgentMcpContext
import ee.carlrobert.codegpt.agent.tools.McpDynamicToolRegistry
import ee.carlrobert.codegpt.agent.tools.McpTool
import ee.carlrobert.codegpt.settings.mcp.McpSettings
import ee.carlrobert.codegpt.settings.mcp.McpSettingsState
import ee.carlrobert.codegpt.ui.textarea.header.tag.McpTagDetails
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assume.assumeTrue
import testsupport.IntegrationTest
import java.net.InetSocketAddress
import java.nio.file.Files
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

/** Opt-in transport integration test: requires Node.js 22+ and npm access to mcp-remote. */
class ParallelSearchMcpTest : IntegrationTest() {

    fun testAnonymousLiveSearchAndFetchThroughAgentTools() {
        assumeTrue(System.getenv("PROXYAI_TEST_PARALLEL_LIVE") == "1")
        val settings = service<McpSettings>()
        val savedState = settings.state
        val state = McpSettingsState()
        val preset = state.servers.single { it.name == "Parallel Search" }
        preset.environmentVariables = mutableMapOf(
            "MCP_REMOTE_CONFIG_DIR" to Files.createTempDirectory("parallel-live-auth").toString()
        )
        settings.loadState(state)
        val conversationId = UUID.randomUUID()
        val selected = McpTagDetails(preset.id.toString(), "Parallel Search").apply { this.selected = true }
        try {
            @Suppress("UNCHECKED_CAST")
            val tools = McpDynamicToolRegistry.createTools(
                AgentMcpContext(conversationId, setOf(preset.id.toString()), listOf(selected))
            ) { _, _ -> true }.associateBy { it.name } as Map<String, Tool<JsonObject, McpTool.Result>>
            assertThat(tools.keys).contains("web_search", "web_fetch")
            val calls = mapOf(
                "web_search" to """{"objective":"Find the official Kotlin coroutine guide","search_queries":["Kotlin coroutines official guide"]}""",
                "web_fetch" to """{"urls":["https://kotlinlang.org/docs/coroutines-overview.html"]}"""
            )
            calls.forEach { (name, args) ->
                val result = runBlocking { tools.getValue(name).execute(Json.parseToJsonElement(args).jsonObject) }
                println("Parallel live $name: $result")
                if (result.success) {
                    assertThat(result.output).contains("https://")
                } else {
                    // Free anonymous test quotas are an expected live condition.
                    assertThat(result.output.lowercase()).containsAnyOf("rate limit", "quota", "too many requests")
                }
            }
        } finally {
            service<McpSessionManager>().ensureClientConnected("$conversationId:${preset.id}").get()?.close()
            settings.loadState(savedState)
        }
    }

    fun testSelectedPresetSearchFetchAndApprovalThroughAgentTools() {
        assumeTrue(System.getenv("PROXYAI_TEST_MCP_REMOTE") == "1")
        val requests = CopyOnWriteArrayList<Pair<String?, JsonObject>>()
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.executor = Executors.newCachedThreadPool { task ->
            Thread(task, "parallel-mcp-fixture").apply { isDaemon = true }
        }
        server.createContext("/mcp") { exchange ->
            if (exchange.requestMethod != "POST") {
                exchange.sendResponseHeaders(405, -1)
                exchange.close()
                return@createContext
            }
            val request = Json.parseToJsonElement(exchange.requestBody.bufferedReader().readText()).jsonObject
            requests.add(exchange.requestHeaders.getFirst("User-Agent") to request)
            val id = request["id"]
            if (id == null) {
                exchange.sendResponseHeaders(202, -1)
                exchange.close()
                return@createContext
            }
            val method = request["method"]?.jsonPrimitive?.content
            val result = when (method) {
                "initialize" -> buildJsonObject {
                    put("protocolVersion", request["params"]!!.jsonObject["protocolVersion"]!!)
                    put("capabilities", buildJsonObject { put("tools", buildJsonObject {}) })
                    put("serverInfo", buildJsonObject { put("name", "fixture"); put("version", "1") })
                }
                "tools/list" -> Json.parseToJsonElement("""
                    {"tools":[
                      {"name":"web_search","description":"Search the web","inputSchema":{"type":"object","properties":{"objective":{"type":"string"},"search_queries":{"type":"array","items":{"type":"string"}}},"required":["objective","search_queries"]}},
                      {"name":"web_fetch","description":"Fetch pages","inputSchema":{"type":"object","properties":{"urls":{"type":"array","items":{"type":"string"}}},"required":["urls"]}}
                    ]}
                """.trimIndent())
                "resources/list" -> buildJsonObject { put("resources", buildJsonArray {}) }
                "tools/call" -> {
                    val params = request["params"]!!.jsonObject
                    val failed = params["arguments"]?.jsonObject?.get("objective")?.jsonPrimitive?.content == "error"
                    buildJsonObject {
                        put("isError", failed)
                        put("content", buildJsonArray {
                            add(buildJsonObject {
                                put("type", "text")
                                put("text", if (failed) "Fixture search failed" else "Source: https://example.com/docs\nUseful page excerpt")
                            })
                        })
                    }
                }
                else -> buildJsonObject {}
            }
            val body = buildJsonObject { put("jsonrpc", "2.0"); put("id", id); put("result", result) }.toString().toByteArray()
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
            exchange.close()
        }
        server.start()

        val settings = service<McpSettings>()
        val savedState = settings.state
        val state = McpSettingsState()
        val preset = state.servers.single { it.name == "Parallel Search" }
        assertThat(preset.arguments).contains("https://search.parallel.ai/mcp")
        preset.arguments = preset.arguments.map {
            if (it == "https://search.parallel.ai/mcp") "http://127.0.0.1:${server.address.port}/mcp" else it
        }.toMutableList().apply { add("--allow-http") }
        preset.environmentVariables = mutableMapOf(
            "MCP_REMOTE_CONFIG_DIR" to Files.createTempDirectory("parallel-mcp-auth").toString(),
            "HTTP_PROXY" to "", "HTTPS_PROXY" to "", "http_proxy" to "", "https_proxy" to ""
        )
        settings.loadState(state)
        val conversationId = UUID.randomUUID()
        val selected = McpTagDetails(preset.id.toString(), "Parallel Search").apply { this.selected = true }
        val context = AgentMcpContext(conversationId, setOf(preset.id.toString()), listOf(selected))
        try {
            @Suppress("UNCHECKED_CAST")
            fun tools(approved: Boolean) = McpDynamicToolRegistry.createTools(context) { _, _ -> approved }
                .associateBy { it.name } as Map<String, Tool<JsonObject, McpTool.Result>>

            val approvedTools = tools(true)
            assertThat(approvedTools.keys).contains("web_search", "web_fetch")
            val args = Json.parseToJsonElement("""{"objective":"Find docs","search_queries":["example docs"]}""").jsonObject
            val search = runBlocking { approvedTools.getValue("web_search").execute(args) }
            assertThat(search.success).isTrue()
            assertThat(search.output).contains("https://example.com/docs", "Useful page excerpt")
            val fetch = runBlocking {
                approvedTools.getValue("web_fetch").execute(
                    Json.parseToJsonElement("""{"urls":["https://example.com/docs"]}""").jsonObject
                )
            }
            assertThat(fetch.success).isTrue()
            assertThat(fetch.output).contains("Useful page excerpt")
            val beforeDenial = requests.size
            val denied = runBlocking { tools(false).getValue("web_search").execute(args) }
            assertThat(denied.success).isFalse()
            assertThat(requests).hasSize(beforeDenial)
            val failed = runBlocking {
                approvedTools.getValue("web_search").execute(
                    Json.parseToJsonElement("""{"objective":"error","search_queries":["example docs"]}""").jsonObject
                )
            }
            assertThat(failed.success).isFalse()
            assertThat(failed.output).contains("Fixture search failed")
            assertThat(requests.map { it.first }).allMatch { it == "ProxyAI/1.0 (+https://tryproxy.io)" }
            assertThat(requests.map { it.second["method"]?.jsonPrimitive?.content })
                .contains("initialize", "tools/list", "tools/call")
        } finally {
            service<McpSessionManager>().ensureClientConnected("$conversationId:${preset.id}").get()?.close()
            settings.loadState(savedState)
            server.stop(0)
        }
    }
}
