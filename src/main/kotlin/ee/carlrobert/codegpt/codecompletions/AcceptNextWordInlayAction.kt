package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.hint.HintManagerImpl
import com.intellij.codeInsight.inline.completion.InlineCompletionHandler
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.codeInsight.inline.completion.session.InlineCompletionSession
import com.intellij.openapi.editor.actionSystem.EditorAction
import com.intellij.openapi.util.TextRange

class AcceptNextWordInlayAction : EditorAction(Handler()), HintManagerImpl.ActionToIgnore {

    companion object {
        const val ID = "codegpt.acceptNextInlayWord"
    }

    private class Handler : AbstractInlayActionHandler() {

        override fun InlineCompletionSession.getTextToInsert(): String {
            return CompletionSplitter.split(context.textToInsert())
        }

        override fun InlineCompletionHandler.invoke(
            request: InlineCompletionRequest,
            range: TextRange
        ) {
            invokeEvent(ApplyNextWordInlaySuggestionEvent(request, range))
        }
    }
}

internal class ApplyNextWordInlaySuggestionEvent(
    originalRequest: InlineCompletionRequest,
    textRange: TextRange
) : ApplyNextInlaySuggestionEvent(originalRequest, textRange)