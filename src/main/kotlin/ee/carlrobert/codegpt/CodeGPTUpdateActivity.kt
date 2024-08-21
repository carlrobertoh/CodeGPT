package ee.carlrobert.codegpt

import com.intellij.ide.plugins.InstalledPluginsState
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.updateSettings.impl.UpdateChecker.updateAndShowResult
import com.intellij.openapi.updateSettings.impl.UpdateSettings
import com.intellij.util.concurrency.AppExecutorUtil
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.ui.OverlayUtil
import java.util.concurrent.TimeUnit

class CodeGPTUpdateActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            return
        }

        schedulePluginUpdateChecks(project)
    }

    private fun schedulePluginUpdateChecks(project: Project) {
        val command = {
            if (service<ConfigurationSettings>().state.checkForPluginUpdates) {
                CheckForUpdatesTask(project).queue()
            }
        }
        AppExecutorUtil.getAppScheduledExecutorService()
            .scheduleWithFixedDelay(command, 0, 4L, TimeUnit.HOURS)
    }

    private class CheckForUpdatesTask(project: Project) :
        Task.Backgroundable(project, CodeGPTBundle.get("checkForUpdatesTask.title"), true) {
        override fun run(indicator: ProgressIndicator) {
            val isLatestVersion =
                !InstalledPluginsState.getInstance().hasNewerVersion(CodeGPTPlugin.CODEGPT_ID)
            if (project.isDisposed || isLatestVersion) {
                return
            }

            OverlayUtil.getDefaultNotification(
                CodeGPTBundle.get("checkForUpdatesTask.notification.message"),
                NotificationType.IDE_UPDATE
            )
                .addAction(NotificationAction.createSimpleExpiring(
                    CodeGPTBundle.get("checkForUpdatesTask.notification.installButton")
                ) {
                    ApplicationManager.getApplication()
                        .executeOnPooledThread { installCodeGPTUpdate(project) }
                })
                .addAction(NotificationAction.createSimpleExpiring(
                    CodeGPTBundle.get("shared.notification.doNotShowAgain")
                ) {
                    service<ConfigurationSettings>().state.checkForPluginUpdates = false
                })
                .notify(project)
        }

        companion object {
            private fun installCodeGPTUpdate(project: Project) {
                val settingsCopy = UpdateSettings()
                val settingsState = settingsCopy.state
                settingsState.copyFrom(UpdateSettings.getInstance().state)
                settingsState.isCheckNeeded = true
                settingsState.isPluginsCheckNeeded = true
                settingsState.isShowWhatsNewEditor = true
                settingsState.isThirdPartyPluginsAllowed = true
                updateAndShowResult(project, settingsCopy)
            }
        }
    }
}
