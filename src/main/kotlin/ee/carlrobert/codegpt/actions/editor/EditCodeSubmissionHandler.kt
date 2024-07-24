package ee.carlrobert.codegpt.actions.editor

import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.jetbrains.rd.util.AtomicReference
import ee.carlrobert.codegpt.completions.CompletionClientProvider
import ee.carlrobert.codegpt.completions.CompletionRequestProvider
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.ui.ObservableProperties

class EditCodeSubmissionHandler(
    private val editor: Editor,
    private val observableProperties: ObservableProperties,
) {

    private val previousSourceRef = AtomicReference<String?>(null)

    suspend fun handleSubmit(userPrompt: String) {
        observableProperties.loading.set(true)
        observableProperties.submitted.set(true)

        previousSourceRef.getAndSet(editor.document.text)
        val (selectionTextRange, selectedText) = readAction {
            editor.selectionModel.run {
                Pair(
                    TextRange(selectionStart, selectionEnd),
                    editor.selectionModel.selectedText ?: ""
                )
            }
        }
        runInEdt { editor.selectionModel.removeSelection() }

        // TODO: Support other providers
        CompletionClientProvider.getCodeGPTClient().getChatCompletionAsync(
            CompletionRequestProvider.buildEditCodeRequest(
                "$userPrompt\n\n$selectedText",
                service<CodeGPTServiceSettings>().state.chatCompletionSettings.model
            ),
            EditCodeCompletionListener(editor, observableProperties, selectionTextRange)
        )
    }

    fun handleAccept() {
        observableProperties.accepted.set(true)
        observableProperties.submitted.set(false)
    }

    fun handleReject() {
        val prevSource = previousSourceRef.get()
        if (!observableProperties.accepted.get() && prevSource != null) {
            revertAllChanges(prevSource)
        }
    }

    private fun revertAllChanges(prevSource: String) {
        runWriteCommandAction(editor.project) {
            editor.document.replaceString(
                0,
                editor.document.textLength,
                StringUtil.convertLineSeparators(prevSource)
            )
        }
    }
}
