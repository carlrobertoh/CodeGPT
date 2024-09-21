package ee.carlrobert.codegpt.actions.editor

import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.util.ui.AsyncProcessIcon
import com.intellij.openapi.util.text.StringUtil
import com.jetbrains.rd.util.AtomicReference
import ee.carlrobert.codegpt.completions.CompletionRequestService
import ee.carlrobert.codegpt.ui.ObservableProperties
import javax.swing.JButton

data class EditCodeRequestParams(val prompt: String, val selectedText: String)

class EditCodeSubmissionHandler(
    private val editor: Editor,
    private val observableProperties: ObservableProperties,
) {

    private val previousSourceRef = AtomicReference<String?>(null)

    suspend fun handleSubmit(
        userPrompt: String,
        followUpButton: JButton,
        acceptButton: JButton,
        spinner: AsyncProcessIcon
    ) {
        followUpButton.isEnabled = false
        acceptButton.isEnabled = false
        spinner.isVisible = true

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

        service<CompletionRequestService>().getEditCodeCompletionAsync(
            EditCodeRequestParams(userPrompt, selectedText),
            EditCodeCompletionListener(
                editor,
                selectionTextRange,
                followUpButton,
                acceptButton,
                spinner
            )
        )
    }

    fun handleAccept() {
        observableProperties.accepted.set(true)
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
