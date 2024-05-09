package ee.carlrobert.codegpt

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil
import ee.carlrobert.codegpt.completions.you.YouUserManager
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationHandler
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationError
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationService
import ee.carlrobert.codegpt.completions.you.auth.response.YouAuthenticationResponse
import ee.carlrobert.codegpt.credentials.CredentialsStore
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.settings.service.you.YouSettings
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.AttachImageNotifier
import ee.carlrobert.codegpt.ui.OverlayUtil
import io.ktor.util.*
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class CodeGPTProjectActivity : StartupActivity.Background {

    private val watchExtensions = listOf("jpg", "jpeg", "png")

    override fun runActivity(project: Project) {
        EditorActionsUtil.refreshActions()

        if (YouUserManager.getInstance().authenticationResponse == null) {
            handleYouServiceAuthenticationAsync()
        }

        if (!ApplicationManager.getApplication().isUnitTestMode
            && ConfigurationSettings.getCurrentState().isCheckForNewScreenshots
        ) {
            val desktopPath = Paths.get(System.getProperty("user.home"), "Desktop")
            project.service<FileWatcher>()
                .watch(desktopPath) {
                    if (watchExtensions.contains(it.extension.lowercase())) {
                        showImageAttachmentNotification(
                            project,
                            desktopPath.resolve(it).absolutePathString()
                        )
                    }
                }
        }
    }

    private fun handleYouServiceAuthenticationAsync() {
        val settings = YouSettings.getCurrentState()
        val password = getCredential(CredentialKey.YOU_ACCOUNT_PASSWORD)
        if (settings.email.isNotEmpty() && !password.isNullOrEmpty()) {
            YouAuthenticationService.getInstance()
                .signInAsync(settings.email, password, object : AuthenticationHandler {
                    override fun handleAuthenticated(authenticationResponse: YouAuthenticationResponse) {
                        OverlayUtil.showNotification(
                            "Authentication successful.",
                            NotificationType.INFORMATION
                        )
                    }

                    override fun handleGenericError() {
                        OverlayUtil.showNotification(
                            "Something went wrong while trying to authenticate.",
                            NotificationType.ERROR
                        )
                    }

                    override fun handleError(youAuthenticationError: YouAuthenticationError) {
                        OverlayUtil.showNotification(
                            youAuthenticationError.errorMessage,
                            NotificationType.ERROR
                        )
                    }
                })
        }
    }

    private fun showImageAttachmentNotification(project: Project, filePath: String) {
        OverlayUtil.getDefaultNotification(
            CodeGPTBundle.get("imageAttachmentNotification.content"),
            NotificationType.INFORMATION
        )
            .addAction(NotificationAction.createSimpleExpiring(
                CodeGPTBundle.get("imageAttachmentNotification.action")
            ) {
                CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH.set(project, filePath)
                project.messageBus
                    .syncPublisher<AttachImageNotifier>(
                        AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC
                    )
                    .imageAttached(filePath)
            })
            .addAction(NotificationAction.createSimpleExpiring(
                CodeGPTBundle.get("shared.notification.doNotShowAgain")
            ) {
                ConfigurationSettings.getCurrentState().isCheckForNewScreenshots = false
            })
            .notify(project)
    }
}
