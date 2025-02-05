package ee.carlrobert.codegpt.predictions

import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.EncodingManager
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CodeGptApiKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.isCredentialSet
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.ui.OverlayUtil

class TriggerCustomPredictionAction : EditorAction(Handler()), HintManagerImpl.ActionToIgnore {

    companion object {
        const val ID = "codegpt.triggerCustomPrediction"
    }

    private class Handler : EditorWriteActionHandler() {

        override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
            if (GeneralSettings.getSelectedService() != ServiceType.CODEGPT) {
                return
            }

            if (!service<CodeGPTServiceSettings>().state.codeAssistantEnabled) {
                val notification = OverlayUtil.getDefaultNotification(
                    "Please enable Code Assistant before using this feature.",
                    NotificationType.WARNING,
                )
                notification.addAction(object : AnAction("Enable Code Assistant") {
                    override fun actionPerformed(e: AnActionEvent) {
                        service<CodeGPTServiceSettings>().state.codeAssistantEnabled = true
                        notification.hideBalloon()
                    }
                })
                OverlayUtil.notify(notification)

                return
            }

            val encodingManager = service<EncodingManager>()
            if (!isCredentialSet(CodeGptApiKey) && encodingManager.countTokens(editor.document.text) > 2048) {
                OverlayUtil.showNotification("The file exceeds the token limit of 2,048. Please upgrade your plan to access higher limits.")
                return
            }
            if (encodingManager.countTokens(editor.document.text) > 4096) {
                OverlayUtil.showNotification("The file exceeds the token limit of 4,096.")
                return
            }

            ApplicationManager.getApplication().executeOnPooledThread {
                service<PredictionService>().displayDirectPrediction(editor)
            }
        }

        override fun isEnabledForCaret(
            editor: Editor,
            caret: Caret,
            dataContext: DataContext
        ): Boolean {
            return editor.getUserData(CodeGPTKeys.EDITOR_PREDICTION_DIFF_VIEWER) == null
        }
    }
}