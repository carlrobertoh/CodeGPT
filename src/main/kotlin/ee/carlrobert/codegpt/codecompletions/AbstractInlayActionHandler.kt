package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletion
import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.InlineCompletionHandler
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.codeInsight.inline.completion.session.InlineCompletionSession
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler
import com.intellij.openapi.util.TextRange

abstract class AbstractInlayActionHandler : EditorWriteActionHandler() {

    abstract fun InlineCompletionSession.getTextToInsert(): String
    abstract fun InlineCompletionHandler.invoke(request: InlineCompletionRequest, range: TextRange)

    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        InlineCompletionSession.getOrNull(editor)?.let { session ->
            if (!session.isActive() || !session.context.isCurrentlyDisplaying()) return

            // TODO: Implement brace matching
            InlineCompletion.getHandlerOrNull(editor)?.apply {
                val startOffset = editor.caretModel.offset
                val textToInsert = session.getTextToInsert()

                withIgnoringDocumentChanges {
                    val suggestionTextRange =
                        TextRange(startOffset - textToInsert.length, startOffset)
                    invoke(session.request, suggestionTextRange)

                    runWriteAction {
                        editor.document.insertString(startOffset, textToInsert)
                        editor.caretModel.moveToOffset(startOffset + textToInsert.length)
                    }
                }
            }
        }
    }

    override fun isEnabledForCaret(
        editor: Editor,
        caret: Caret,
        dataContext: DataContext?
    ): Boolean = InlineCompletionSession.getOrNull(editor)?.let {
        it.isActive() && it.context.isCurrentlyDisplaying()
    } ?: false
}

internal abstract class ApplyNextInlaySuggestionEvent(
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