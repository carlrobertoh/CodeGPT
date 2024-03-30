package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletion
import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.InlineCompletionHandler
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.codeInsight.inline.completion.session.InlineCompletionContext
import com.intellij.codeInsight.inline.completion.session.InlineCompletionSession
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler
import com.intellij.openapi.util.TextRange

class ApplyNextWordInlayAction : EditorAction(Handler()) {

    private class Handler : EditorWriteActionHandler() {
        override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
            InlineCompletionSession.getOrNull(editor)?.let {
                if (!it.isActive() || !it.context.isCurrentlyDisplaying()) return

                InlineCompletion.getHandlerOrNull(editor)?.apply(
                    it.applyNextWordInlaySuggestion(it.context, editor)
                )
            }
        }

        private fun InlineCompletionSession.applyNextWordInlaySuggestion(
            context: InlineCompletionContext,
            editor: Editor
        ): InlineCompletionHandler.() -> Unit = {
            val startOffset = context.startOffset() ?: 0
            val endOffset = context.endOffset() ?: 0
            val textToInsert =
                CompletionSplitter.split(context.textToInsert(), startOffset, endOffset)
            withIgnoringDocumentChanges {
                runWriteAction {
                    editor.document.insertString(startOffset, textToInsert)
                    editor.caretModel.moveToOffset(startOffset + textToInsert.length)
                }
            }

            val suggestionTextRange =
                TextRange(startOffset + textToInsert.length, endOffset + textToInsert.length)
            invokeEvent(ApplyNextWordInlaySuggestionEvent(request, suggestionTextRange))
        }
    }
}

internal class ApplyNextWordInlaySuggestionEvent(
    private val originalRequest: InlineCompletionRequest,
    private val textRange: TextRange
) : InlineCompletionEvent {

    override fun toRequest(): InlineCompletionRequest {
        return InlineCompletionRequest(
            this,
            originalRequest.file,
            originalRequest.editor,
            originalRequest.document,
            textRange.startOffset,
            textRange.endOffset,
            originalRequest.lookupElement
        )
    }
}