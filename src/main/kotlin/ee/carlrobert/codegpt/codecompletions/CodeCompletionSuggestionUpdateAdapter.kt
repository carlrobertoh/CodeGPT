package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult.Changed
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult.Invalidated
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionVariant

class CodeCompletionSuggestionUpdateAdapter :
    InlineCompletionSuggestionUpdateManager.Default() {

    override fun onCustomEvent(
        event: InlineCompletionEvent,
        variant: InlineCompletionVariant.Snapshot
    ): UpdateResult {
        if (event !is ApplyNextWordInlaySuggestionEvent || variant.elements.isEmpty()) {
            return Invalidated
        }

        val completionText = variant.elements.firstOrNull()?.text ?: return Invalidated
        val textToInsert = event.toRequest().run {
            CompletionSplitter.split(completionText)
        }
        return Changed(
            variant.copy(
                listOf(InlineCompletionGrayTextElement(completionText.removePrefix(textToInsert)))
            )
        )
    }
}