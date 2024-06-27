package ee.carlrobert.codegpt.ui

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.application.readAction
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.ui.Messages
import com.jetbrains.rd.util.AtomicReference
import ee.carlrobert.codegpt.completions.CompletionClientProvider
import ee.carlrobert.codegpt.completions.CompletionRequestProvider
import ee.carlrobert.codegpt.util.EditorUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditCodeSubmissionHandler(
    private val editor: Editor,
    private val observableProperties: ObservableProperties,
) {
    private val originalSourceRef = AtomicReference<String?>(null)
    private val rangeHighlighters = mutableListOf<RangeHighlighter>()

    suspend fun handleSubmit(userPrompt: String) {
        val context = getContext()
        if (context.isEmpty()) {
            Messages.showWarningDialog(
                editor.project,
                "Highlight the block of code you want to modify and try again.",
                "No Code Selected"
            )
            return
        }
        try {
            observableProperties.loading.set(true)
            val modifiedContent = withContext(Dispatchers.IO) {
                getModifiedContent("$userPrompt\n\n$context")
            }
            invokeLater { handleResponse(modifiedContent, context) }
        } finally {
            observableProperties.loading.set(false)
            observableProperties.submitted.set(true)
        }
    }

    private suspend fun getContext(): String {
        return readAction {
            editor.selectionModel.selectedText ?: throw IllegalStateException("No code selected")
        }
    }

    fun handleAccept() {
        destroyHighlighters()
        observableProperties.accepted.set(true)
        observableProperties.submitted.set(false)
    }

    fun handleReject() {
        destroyHighlighters()
        val prevSource = originalSourceRef.get()
        if (!observableProperties.accepted.get() && prevSource != null) {
            revertAllChanges(prevSource)
        }
    }

    private fun getModifiedContent(prompt: String): String {
        val response = CompletionClientProvider.getCodeGPTClient().getChatCompletion(
            CompletionRequestProvider.buildModifySelectionRequest(prompt, "claude-3.5-sonnet")
        )
        if (response == null || response.choices == null || response.choices.isEmpty()) {
            runInEdt {
                Messages.showErrorDialog(
                    editor.project,
                    "Something went wrong while requesting completion. Please try again.",
                    "No Suggestion"
                )
            }
            throw RuntimeException("")
        }
        return response.choices.first().message.content
    }

    private fun handleResponse(
        modifiedContent: String,
        selectedText: String,
    ) {
        if (modifiedContent == "NULL") {
            runInEdt {
                Messages.showWarningDialog(
                    editor.project,
                    "No suggestion could be made. Please check the input prompt and try again.",
                    "No Suggestion"
                )
            }
            return
        }

        originalSourceRef.getAndSet(editor.document.text)
        EditorUtil.replaceEditorSelection(editor, modifiedContent)
        rangeHighlighters.addAll(
            EditorUtil.highlightDifferences(
                editor,
                selectedText,
                modifiedContent
            )
        )
    }

    private fun destroyHighlighters() {
        for (highlighter in rangeHighlighters) {
            highlighter.dispose()
        }
        rangeHighlighters.clear()
    }

    private fun revertAllChanges(prevSource: String) {
        runWriteAction {
            runWriteCommandAction(editor.project) {
                editor.document.replaceString(0, editor.document.textLength, prevSource)
            }
        }
    }
}
