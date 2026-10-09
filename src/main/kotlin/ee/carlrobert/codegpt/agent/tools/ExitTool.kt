package ee.carlrobert.codegpt.agent.tools

import ai.koog.agents.core.tools.SimpleTool
import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.serialization.typeToken
import kotlinx.serialization.Serializable

object ExitTool : SimpleTool<ExitTool.Args>(
    typeToken<Args>(),
    "__exit__",
    "Service tool, used by the agent to end conversation on user request or agent decision"
) {
    @Serializable
    data class Args(
        @property:LLMDescription("The reason for ending conversation")
        val message: String = ""
    )

    override suspend fun execute(args: Args): String = "Conversation ended: ${args.message}"
}
