package ee.carlrobert.codegpt

import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil
import ee.carlrobert.codegpt.completions.you.YouUserManager
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationHandler
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationError
import ee.carlrobert.codegpt.completions.you.auth.YouAuthenticationService
import ee.carlrobert.codegpt.completions.you.auth.response.YouAuthenticationResponse
import ee.carlrobert.codegpt.credentials.YouCredentialManager
import ee.carlrobert.codegpt.settings.service.you.YouSettings
import ee.carlrobert.codegpt.ui.OverlayUtil

class CodeGPTProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        EditorActionsUtil.refreshActions()

        if (YouUserManager.getInstance().authenticationResponse == null) {
            ApplicationManager.getApplication()
                .executeOnPooledThread { this.handleYouServiceAuthentication() }
        }
    }

    private fun handleYouServiceAuthentication() {
        val settings = YouSettings.getCurrentState()
        val password = YouCredentialManager.getInstance().credential
        if (settings.email.isNotEmpty() && password != null && password.isNotEmpty()) {
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
}