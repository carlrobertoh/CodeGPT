package ee.carlrobert.codegpt

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CodeGptApiKey
import ee.carlrobert.codegpt.credentials.CredentialsStore.isCredentialSet
import ee.carlrobert.codegpt.predictions.PredictionService
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodeGPTCopyPastePreProcessor : CopyPastePreProcessor {


    private companion object {
        const val MAX_TOKEN_LIMIT = 4096
        const val FREE_TIER_TOKEN_LIMIT = 2048

        private val logger = thisLogger()
    }

    override fun preprocessOnCopy(
        file: PsiFile,
        startOffsets: IntArray,
        endOffsets: IntArray,
        text: String
    ): String {
        return text
    }

    override fun preprocessOnPaste(
        project: Project,
        file: PsiFile,
        editor: Editor,
        text: String,
        rawText: RawText?
    ): String {
        try {
            displayPrediction(editor.document.text) {
                service<PredictionService>().displayPastePrediction(editor, text)
            }
        } catch (e: Exception) {
            logger.error("Error displaying paste prediction")
        }

        return text
    }

    private fun displayPrediction(documentText: String, handleDisplay: () -> Unit = {}) {
        if (!isPredictionEnabled()) return

        val currentTokens = getDocumentTokenCount(documentText)
        if (currentTokens > MAX_TOKEN_LIMIT) return

        if (!isCredentialSet(CodeGptApiKey) && currentTokens > FREE_TIER_TOKEN_LIMIT) return

        CoroutineScope(Dispatchers.IO).launch {
            handleDisplay()
        }
    }

    private fun isPredictionEnabled(): Boolean =
        GeneralSettings.getSelectedService() == ServiceType.CODEGPT &&
                service<CodeGPTServiceSettings>().state.codeAssistantEnabled

    private fun getDocumentTokenCount(text: String): Int =
        EncodingManager.getInstance().countTokens(text)
}