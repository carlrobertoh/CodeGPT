package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletion
import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.InlineCompletionInsertEnvironment
import com.intellij.codeInsight.inline.completion.InlineCompletionInsertHandler
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import ee.carlrobert.codegpt.CodeGPTKeys.REMAINING_EDITOR_COMPLETION

class CodeCompletionInsertHandler : InlineCompletionInsertHandler {

    override fun afterInsertion(
        environment: InlineCompletionInsertEnvironment,
        elements: List<InlineCompletionElement>
    ) {
        val editor = environment.editor
        val remainingCompletion = REMAINING_EDITOR_COMPLETION.get(editor) ?: ""
        if (remainingCompletion.isNotEmpty()) {
            InlineCompletion.getHandlerOrNull(editor)?.invoke(
                InlineCompletionEvent.DirectCall(editor, editor.caretModel.currentCaret)
            )
        }
    }
}