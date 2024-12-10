package ee.carlrobert.codegpt.codecompletions

import ai.grazie.nlp.utils.takeWhitespaces
import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.codeInsight.inline.completion.InlineCompletionHandler
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.codeInsight.inline.completion.session.InlineCompletionSession
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.util.TextRange

class AcceptNextLineInlayAction : EditorAction(Handler()), HintManagerImpl.ActionToIgnore {

    companion object {
        const val ID = "codegpt.acceptNextInlayLine"
    }

    private class Handler : AbstractInlayActionHandler() {

        override fun InlineCompletionSession.getTextToInsert(): String {
            val completionText = context.textToInsert()
            val lineBreakIndex = completionText.indexOfFirst { it == '\n' }

            return if (lineBreakIndex == -1) {
                completionText
            } else {
                val nextLineStart = lineBreakIndex + 1
                val whitespacesLength =
                    completionText.substring(nextLineStart).takeWhitespaces().length
                completionText.substring(0, nextLineStart + whitespacesLength)
            }
        }

        override fun InlineCompletionHandler.invoke(
            request: InlineCompletionRequest,
            range: TextRange
        ) {
            invokeEvent(ApplyNextLineInlaySuggestionEvent(request, range))
        }
    }
}

internal class ApplyNextLineInlaySuggestionEvent(
    originalRequest: InlineCompletionRequest,
    textRange: TextRange
) : ApplyNextInlaySuggestionEvent(originalRequest, textRange)