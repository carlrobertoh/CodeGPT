package ee.carlrobert.codegpt.actions

import com.intellij.notification.Notification
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.completions.llama.LlamaModel.findByHuggingFaceModel
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent
import ee.carlrobert.codegpt.completions.llama.LlamaServerStartupParams
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA_CPP
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings.getAdditionalParametersList
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings.isRunnable
import ee.carlrobert.codegpt.settings.service.llama.form.ServerProgressPanel
import ee.carlrobert.codegpt.ui.OverlayUtil.showNotification
import ee.carlrobert.codegpt.ui.OverlayUtil.stickyNotification
import java.util.function.Consumer

/**
 * Start or stop server (if selected model exists) showing notifications
 */
abstract class LlamaServerToggleActions(
    private val startServer: Boolean
) : DumbAwareAction() {
    companion object {
        fun expireOtherNotification(start: Boolean) {
            (ActionManager.getInstance().getAction(
                if (start) "statusbar.stopServer" else "statusbar.startServer"
            ) as LlamaServerToggleActions).apply {
                this.notification?.expire()
                this.notification = null
            }
        }
    }

    var notification: Notification? = null

    override fun actionPerformed(e: AnActionEvent) {
        (GeneralSettings.getCurrentState().selectedService == LLAMA_CPP).takeIf { it } ?: return
        notification?.expire()
        expireOtherNotification(startServer)
        val llamaServerAgent = service<LlamaServerAgent>()
        val serverName = LlamaSettings.getInstance().state.huggingFaceModel.let { findByHuggingFaceModel(it).toString(it) }
        if (startServer) {
            notification = stickyNotification(formatMsg("settingsConfigurable.service.llama.progress.startingServer", serverName))
            val serverProgressPanel = ServerProgressPanel()
            llamaServerAgent.setActiveServerProgressPanel(serverProgressPanel)
            val settings = LlamaSettings.getInstance().state
            llamaServerAgent.startAgent(
                LlamaServerStartupParams(
                    LlamaSettings.getInstance().actualModelPath,
                    settings.contextSize,
                    settings.threads,
                    settings.serverPort,
                    getAdditionalParametersList(settings.additionalParameters),
                    getAdditionalParametersList(settings.additionalBuildParameters)
                ),
                serverProgressPanel,
                {
                    notification?.expire()
                    notification = showNotification(formatMsg("settingsConfigurable.service.llama.progress.serverRunning", serverName))
                },
                {
                    Consumer<ServerProgressPanel> { _: ServerProgressPanel ->
                        notification?.expire()
                        notification = showNotification(formatMsg("settingsConfigurable.service.llama.progress.serverStopped", serverName))
                    }
                })
        } else {
            notification = showNotification(formatMsg("settingsConfigurable.service.llama.progress.stoppingServer", serverName))
            llamaServerAgent.stopAgent()
            notification?.expire()
            notification = showNotification(formatMsg("settingsConfigurable.service.llama.progress.serverStopped", serverName))
        }
    }

    // "Starting server..." -> "Starting server: CodeLlama 7B 4-bit ..."
    // "Stopped server"     -> "Stopped server: CodeLlama 7B 4-bit"
    private fun formatMsg(id: String, serverName: String): String {
        val msg = CodeGPTBundle.get(id)
        val points = msg.endsWith("...")
        return msg.let { if (points) it.substringBeforeLast("...") else it } + ": " + serverName + (if (points) " ..." else "")
    }

    override fun update(e: AnActionEvent) {
        val llamaRunnable = isRunnable(LlamaSettings.getInstance().state.huggingFaceModel)
        val serverRunning = llamaRunnable && service<LlamaServerAgent>().isServerRunning
        val toggle = llamaRunnable && serverRunning != startServer
        e.presentation.isVisible = toggle
        e.presentation.isEnabled = toggle
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}

class StartServerAction : LlamaServerToggleActions(true)

class StopServerAction : LlamaServerToggleActions(false)
