package ee.carlrobert.codegpt

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.Disposer
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil
import ee.carlrobert.codegpt.completions.you.YouUserManager
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationHandler
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationError
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationService
import ee.carlrobert.codegpt.completions.you.auth.response.YouAuthenticationResponse
import ee.carlrobert.codegpt.credentials.CredentialsStore
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.getCredential
import ee.carlrobert.codegpt.settings.service.you.YouSettings
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.UploadImageNotifier
import ee.carlrobert.codegpt.ui.OverlayUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Paths

class CodeGPTProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        EditorActionsUtil.refreshActions()
        CredentialsStore.loadAll()

        if (!ApplicationManager.getApplication().isUnitTestMode) {
            val pathToWatch = Paths.get(System.getProperty("user.home"), "Desktop")
            val fileWatcher = FileWatcher(pathToWatch)
            fileWatcher.watch { showImageAttachmentNotification(project, it.absolutePath) }
            Disposer.register(project, fileWatcher)
        }

        if (YouUserManager.getInstance().authenticationResponse == null) {
            withContext(Dispatchers.IO) {
                handleYouServiceAuthentication()
            }
        }
    }

    private fun handleYouServiceAuthentication() {
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
            "New image detected on desktop. Would you like to attach it to your current conversation?",
            NotificationType.INFORMATION
        )
            .addAction(
                NotificationAction.createSimpleExpiring(
                    "Attach image"
                ) {
                    CodeGPTKeys.UPLOADED_FILE_PATH.set(project, filePath)
                    project.messageBus
                        .syncPublisher<UploadImageNotifier>(UploadImageNotifier.UPLOADED_FILE_PATH_TOPIC)
                        .fileUploaded(filePath)
                })
            .addAction(
                NotificationAction.createSimpleExpiring(
                    CodeGPTBundle.get("checkForUpdatesTask.notification.hideButton")
                ) {
                    TODO("Not implemented")
                })
            .notify(project)
    }
}