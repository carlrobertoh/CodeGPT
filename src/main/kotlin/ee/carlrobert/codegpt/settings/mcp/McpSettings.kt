package ee.carlrobert.codegpt.settings.mcp

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.annotations.Property
import ee.carlrobert.codegpt.util.ApplicationUtil

@Service
@State(
    name = "CodeGPT_McpSettings",
    storages = [Storage("CodeGPT_McpSettings.xml")]
)
class McpSettings : SimplePersistentStateComponent<McpSettingsState>(McpSettingsState())

class McpSettingsState : BaseState() {
    @get:Property(alwaysWrite = true)
    var servers by list<McpServerDetailsState>()

    init {
        servers.add(McpServerDetailsState().apply {
            id = 1L
            name = "Test Server"
            command = "npx"
            arguments = mutableListOf("-y", "@modelcontextprotocol/server-everything")
        })
        servers.add(McpServerDetailsState().apply {
            id = 2L
            name = "File System Server"
            command = "npx"
            arguments = mutableListOf(
                "-y",
                "@modelcontextprotocol/server-filesystem",
                ApplicationUtil.findCurrentProject()?.basePath ?: "/"
            )
        })
        servers.add(McpServerDetailsState().apply {
            id = 3L
            name = "Git"
            command = "uvx"
            arguments = mutableListOf(
                "mcp-server-git",
                "--repository",
                ApplicationUtil.findCurrentProject()?.basePath ?: "/"
            )
        })
        servers.add(McpServerDetailsState().apply {
            id = 4L
            name = "Context7"
            command = "npx"
            arguments = mutableListOf("-y", "@upstash/context7-mcp")
        })
        servers.add(McpServerDetailsState().apply {
            id = 5L
            name = "ripgrep"
            command = "npx"
            arguments = mutableListOf("-y", "mcp-ripgrep@latest")
        })
        servers.add(McpServerDetailsState().apply {
            id = 6L
            name = "Parallel Search"
            command = "npx"
            arguments = mutableListOf(
                "-y",
                "mcp-remote@0.15.1",
                "https://search.parallel.ai/mcp",
                "--transport", "http-only",
                "--enable-proxy",
                "--header", "User-Agent:ProxyAI/1.0 (+https://tryproxy.io)"
            )
        })
    }
}

class McpServerDetailsState : BaseState() {
    var id by property(1L)
    var name by string("New MCP Server")
    var command by string("npx")
    var arguments by list<String>()
    var environmentVariables by map<String, String>()
}
