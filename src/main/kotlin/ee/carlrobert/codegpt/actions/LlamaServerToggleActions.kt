package ee.carlrobert.codegpt.actions

import com.intellij.notification.Notification
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAwareAction
import ee.carlrobert.codegpt.CodeGPTBundle
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
        if (startServer) {
            notification = stickyNotification(CodeGPTBundle.get("settingsConfigurable.service.llama.progress.startingServer"))
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
                    notification = showNotification(CodeGPTBundle.get("settingsConfigurable.service.llama.progress.serverRunning"))
                },
                {
                    Consumer<ServerProgressPanel> { _: ServerProgressPanel ->
                        notification?.expire()
                        notification = showNotification(CodeGPTBundle.get("settingsConfigurable.service.llama.progress.serverStopped"))
                    }
                })
        } else {
            notification = showNotification(CodeGPTBundle.get("settingsConfigurable.service.llama.progress.stoppingServer"))
            llamaServerAgent.stopAgent()
            notification?.expire()
            notification = showNotification(CodeGPTBundle.get("settingsConfigurable.service.llama.progress.serverStopped"))
        }
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
