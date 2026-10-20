package ee.carlrobert.codegpt.settings.mcp

import com.intellij.util.xmlb.XmlSerializer
import ee.carlrobert.codegpt.agent.AgentMcpContext
import ee.carlrobert.codegpt.agent.tools.McpDynamicToolRegistry
import org.assertj.core.api.Assertions.assertThat
import testsupport.IntegrationTest
import java.util.UUID

class McpSettingsTest : IntegrationTest() {

    fun testSavedServersReplacePresetsWithoutChangingCommandsOrCredentials() {
        val saved = McpSettingsState().apply {
            servers.clear()
            servers.add(McpServerDetailsState().apply {
                id = 6L
                name = "My server"
                command = "custom-command"
                arguments = mutableListOf("--my-option")
                environmentVariables = mutableMapOf("API_KEY" to "saved-key")
            })
        }

        val restored = XmlSerializer.deserialize(
            XmlSerializer.serialize(saved),
            McpSettingsState::class.java
        )

        assertThat(restored.servers).hasSize(1)
        val server = restored.servers.single()
        assertThat(server.name).isEqualTo("My server")
        assertThat(server.command).isEqualTo("custom-command")
        assertThat(server.arguments).containsExactly("--my-option")
        assertThat(server.environmentVariables).containsEntry("API_KEY", "saved-key")
    }

    fun testEmptySavedServerListStaysEmpty() {
        val saved = McpSettingsState().apply { servers.clear() }

        val restored = XmlSerializer.deserialize(
            XmlSerializer.serialize(saved),
            McpSettingsState::class.java
        )

        assertThat(restored.servers).isEmpty()
    }

    fun testPresetsDoNotExposeToolsWithoutConversationSelection() {
        assertThat(McpSettingsState().servers.map { it.name }).contains("Parallel Search")

        val tools = McpDynamicToolRegistry.createTools(
            AgentMcpContext(UUID.randomUUID(), emptySet())
        ) { _, _ -> error("An unselected server must not ask for approval") }

        assertThat(tools).isEmpty()
    }
}
