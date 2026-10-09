package ee.carlrobert.codegpt.agent

import ai.koog.agents.core.tools.Tool
import ai.koog.agents.core.tools.ToolRegistry
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.agent.tools.*
import ee.carlrobert.codegpt.agent.tools.ide.*
import ee.carlrobert.codegpt.settings.hooks.HookManager
import ee.carlrobert.codegpt.settings.models.ModelSelection
import ee.carlrobert.codegpt.toolwindow.agent.ui.approval.BashPayload
import ee.carlrobert.codegpt.toolwindow.agent.ui.approval.ToolApprovalRequest
import ee.carlrobert.codegpt.toolwindow.agent.ui.approval.ToolApprovalType

internal object BuiltInToolRegistry {

    fun createMainAgentRegistry(
        project: Project,
        events: AgentEvents,
        sessionId: String,
        parentModelSelection: ModelSelection,
        hookManager: HookManager,
        standardApproval: suspend (String, String) -> Boolean,
        writeApproval: suspend (String, String) -> Boolean,
        genericApproval: suspend (String, String) -> Boolean,
    ): ToolRegistry {
        return ToolRegistry.builder().tools(
            createTools(
                ToolName.entries,
                RegistrationContext(
                    project = project,
                    sessionId = sessionId,
                    hookManager = hookManager,
                    standardApproval = standardApproval,
                    writeApproval = writeApproval,
                    genericApproval = genericApproval,
                    bashConfirmationHandler = { args ->
                        try {
                            val approved = events.approveToolCall(
                                ToolApprovalRequest(
                                    ToolApprovalType.BASH,
                                    "Run shell command?",
                                    args.command,
                                    BashPayload(args.command, args.description)
                                )
                            )
                            if (approved) ShellCommandConfirmation.Approved
                            else ShellCommandConfirmation.Denied("User rejected the command")
                        } catch (_: Exception) {
                            ShellCommandConfirmation.Approved
                        }
                    },
                    askUserEvents = events,
                    parentModelSelection = parentModelSelection
                )
            )
        ).build()
    }

    fun createSubagentRegistry(
        project: Project,
        sessionId: String,
        selected: Set<ToolName>,
        bashConfirmationHandler: BashCommandConfirmationHandler,
        hookManager: HookManager,
        approveToolCall: suspend (String, String) -> Boolean
    ): ToolRegistry {
        val selectedTools = buildList {
            addAll(selected)
            addAll(ToolName.entries.filter { it.alwaysIncludeInSubagent })
        }.distinct()
        return ToolRegistry.builder().tools(
            createTools(
                selectedTools,
                RegistrationContext(
                    project = project,
                    sessionId = sessionId,
                    hookManager = hookManager,
                    standardApproval = approveToolCall,
                    writeApproval = approveToolCall,
                    genericApproval = approveToolCall,
                    bashConfirmationHandler = bashConfirmationHandler
                )
            )
        ).build()
    }

    private fun createTools(
        selected: Collection<ToolName>,
        context: RegistrationContext
    ): List<Tool<*, *>> {
        return selected.flatMap { tool ->
            when (tool) {
                ToolName.READ -> listOf(
                    ReadTool(
                        context.project,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.WRITE -> listOf(
                    ConfirmingWriteTool(
                        WriteTool(context.project, context.sessionId, context.hookManager),
                        context.writeApproval
                    )
                )

                ToolName.EDIT -> listOf(
                    ConfirmingEditTool(
                        EditTool(context.project, context.sessionId, context.hookManager),
                        context.standardApproval
                    )
                )

                ToolName.BASH -> listOf(
                    BashTool(
                        project = context.project,
                        confirmationHandler = context.bashConfirmationHandler,
                        sessionId = context.sessionId,
                        hookManager = context.hookManager
                    )
                )

                ToolName.BASH_OUTPUT -> listOf(
                    BashOutputTool(
                        context.workingDirectory,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.KILL_SHELL -> listOf(
                    KillShellTool(
                        context.workingDirectory,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.INTELLIJ_SEARCH -> listOf(
                    IntelliJSearchTool(
                        context.project,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.DIAGNOSTICS -> listOf(
                    DiagnosticsTool(
                        context.project,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.WEB_SEARCH -> listOf(
                    WebSearchTool(
                        context.workingDirectory,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.WEB_FETCH -> listOf(
                    WebFetchTool(
                        context.workingDirectory,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.MCP -> createMcpTools(context)
                ToolName.RESOLVE_LIBRARY_ID -> listOf(
                    ResolveLibraryIdTool(
                        context.workingDirectory,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.GET_LIBRARY_DOCS -> listOf(
                    GetLibraryDocsTool(
                        context.workingDirectory,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.LOAD_SKILL -> listOf(
                    ConfirmingLoadSkillTool(
                        LoadSkillTool(context.project, context.sessionId, context.hookManager),
                        context.project,
                        context.genericApproval
                    )
                )

                ToolName.TASK -> {
                    val events = context.askUserEvents
                    val modelSelection = context.parentModelSelection
                    if (events != null && modelSelection != null) {
                        listOf(
                            TaskTool(
                                context.project,
                                context.sessionId,
                                modelSelection,
                                events,
                                context.hookManager
                            )
                        )
                    } else {
                        emptyList()
                    }
                }

                ToolName.ASK_USER_QUESTION -> {
                    val events = context.askUserEvents
                    if (events != null) {
                        listOf(
                            AskUserQuestionTool(
                                context.workingDirectory,
                                context.sessionId,
                                context.hookManager,
                                events
                            )
                        )
                    } else {
                        emptyList()
                    }
                }

                ToolName.TODO_WRITE -> listOf(
                    TodoWriteTool(
                        context.project,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.GET_RUN_CONFIGURATIONS -> listOf(
                    GetRunConfigurationsTool(
                        context.project,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.EXECUTE_RUN_CONFIGURATION -> listOf(
                    ConfirmingExecuteRunConfigurationTool(
                        ExecuteRunConfigurationTool(
                            context.project,
                            context.sessionId,
                            context.hookManager
                        ),
                        context.genericApproval
                    )
                )

                ToolName.GET_RUN_OUTPUT -> listOf(
                    GetRunOutputTool(
                        context.project,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.GET_DEBUG_SESSIONS -> listOf(
                    GetDebugSessionsTool(
                        context.project,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.GET_BREAKPOINTS -> listOf(
                    GetBreakpointsTool(
                        context.project,
                        context.sessionId,
                        context.hookManager
                    )
                )

                ToolName.BREAKPOINT -> listOf(
                    ConfirmingBreakpointTool(
                        BreakpointTool(
                            context.project,
                            context.sessionId,
                            context.hookManager
                        ),
                        context.genericApproval
                    )
                )

                ToolName.DEBUG_SESSION_CONTROL -> listOf(
                    ConfirmingDebugSessionControlTool(
                        DebugSessionControlTool(
                            context.project,
                            context.sessionId,
                            context.hookManager
                        ),
                        context.genericApproval
                    )
                )

                ToolName.EXIT -> listOf(ExitTool)
            }
        }
    }

    private fun createMcpTools(context: RegistrationContext): List<Tool<*, *>> {
        val mcpContext = context.project.service<AgentMcpContextService>().get(context.sessionId)
            ?: return emptyList()
        if (!mcpContext.hasSelection() || mcpContext.conversationId == null) {
            return emptyList()
        }
        return McpDynamicToolRegistry.createTools(mcpContext, context.genericApproval)
    }

    private data class RegistrationContext(
        val project: Project,
        val sessionId: String,
        val hookManager: HookManager,
        val standardApproval: suspend (String, String) -> Boolean,
        val writeApproval: suspend (String, String) -> Boolean,
        val genericApproval: suspend (String, String) -> Boolean,
        val bashConfirmationHandler: BashCommandConfirmationHandler,
        val askUserEvents: AgentEvents? = null,
        val parentModelSelection: ModelSelection? = null
    ) {
        val workingDirectory: String = project.basePath ?: System.getProperty("user.dir")
    }
}
