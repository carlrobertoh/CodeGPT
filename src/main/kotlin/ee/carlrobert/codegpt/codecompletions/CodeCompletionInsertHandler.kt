package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletion
import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.InlineCompletionInsertEnvironment
import com.intellij.codeInsight.inline.completion.InlineCompletionInsertHandler
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.openapi.application.readAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import ee.carlrobert.codegpt.CodeGPTKeys.REMAINING_EDITOR_COMPLETION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

private const val NEXT_COMPLETION_LINE_COUNT_THRESHOLD = 4

class CodeCompletionInsertHandler : InlineCompletionInsertHandler {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun afterInsertion(
        environment: InlineCompletionInsertEnvironment,
        elements: List<InlineCompletionElement>
    ) {
        val editor = environment.editor
        val appliedText = elements.joinToString("") { it.text }
        val existingCompletion = REMAINING_EDITOR_COMPLETION.get(editor) ?: ""
        val remainingCompletion =
            existingCompletion.substring(appliedText.length, existingCompletion.length)

        REMAINING_EDITOR_COMPLETION.set(editor, remainingCompletion)

        if (remainingCompletion.isNotEmpty()) {
            val handler = InlineCompletion.getHandlerOrNull(editor)
            handler?.invoke(
                InlineCompletionEvent.DirectCall(editor, editor.caretModel.currentCaret)
            )

            if (remainingCompletion.count { it == '\n' } <= NEXT_COMPLETION_LINE_COUNT_THRESHOLD) {
                scope.launch {
                    fetchNextCompletion(editor, remainingCompletion)
                }
            }
        }
    }

    private suspend fun fetchNextCompletion(editor: Editor, remainingCompletion: String) {
        val project = editor.project ?: return
        project.service<CodeCompletionService>().getCodeCompletionAsync(
            buildNextRequest(editor, remainingCompletion),
            object : CodeCompletionCompletionEventListener(editor) {
                override fun onComplete(fullMessage: String) {
                    val nextCompletion =
                        (REMAINING_EDITOR_COMPLETION.get(editor) ?: "") + fullMessage
                    REMAINING_EDITOR_COMPLETION.set(editor, nextCompletion)
                }
            }
        )
    }

    private suspend fun buildNextRequest(
        editor: Editor,
        remainingCompletion: String
    ): InfillRequest {
        val caretOffset = readAction { editor.caretModel.offset }
        val prefix =
            (editor.document.getText(TextRange(0, caretOffset)) + remainingCompletion)
                .truncateText(MAX_PROMPT_TOKENS, false)
        val suffix =
            editor.document.getText(
                TextRange(
                    caretOffset,
                    editor.document.textLength
                )
            ).truncateText(MAX_PROMPT_TOKENS)
        return InfillRequest.Builder(prefix, suffix).build()
    }
}