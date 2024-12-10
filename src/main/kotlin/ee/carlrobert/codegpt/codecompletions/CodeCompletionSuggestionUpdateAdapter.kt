package ee.carlrobert.codegpt.codecompletions

import ai.grazie.nlp.utils.takeWhitespaces
import com.intellij.codeInsight.inline.completion.InlineCompletionEvent
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult.Changed
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionSuggestionUpdateManager.UpdateResult.Invalidated
import com.intellij.codeInsight.inline.completion.suggestion.InlineCompletionVariant
import com.intellij.openapi.editor.Editor
import ee.carlrobert.codegpt.CodeGPTKeys.REMAINING_EDITOR_COMPLETION

class CodeCompletionSuggestionUpdateAdapter :
    InlineCompletionSuggestionUpdateManager.Default() {

    override fun onDocumentChange(
        event: InlineCompletionEvent.DocumentChange,
        variant: InlineCompletionVariant.Snapshot
    ): UpdateResult {
        updateRemainingCompletion(event.editor, event.typing.typed)
        return super.onDocumentChange(event, variant)
    }

    override fun onCustomEvent(
        event: InlineCompletionEvent,
        variant: InlineCompletionVariant.Snapshot
    ): UpdateResult {
        if (variant.elements.isEmpty()) return Invalidated

        val completionText = variant.elements.joinToString("") { it.text }
        return when (event) {
            is ApplyNextWordInlaySuggestionEvent -> handleNextWordEvent(event, variant, completionText)
            is ApplyNextLineInlaySuggestionEvent -> handleNextLineEvent(event, variant, completionText)
            else -> Invalidated
        }
    }

    private fun handleNextWordEvent(
        event: ApplyNextWordInlaySuggestionEvent,
        variant: InlineCompletionVariant.Snapshot,
        completionText: String
    ): UpdateResult {
        val textToInsert = CompletionSplitter.split(completionText)
        return createUpdatedVariant(event.toRequest().editor, variant, completionText, textToInsert)
    }

    private fun handleNextLineEvent(
        event: ApplyNextLineInlaySuggestionEvent,
        variant: InlineCompletionVariant.Snapshot,
        completionText: String
    ): UpdateResult {
        val lineBreakIndex = completionText.indexOf('\n')
        if (lineBreakIndex == -1) return Invalidated

        val nextLineStart = lineBreakIndex + 1
        val whitespacesLength = completionText
            .substring(nextLineStart)
            .takeWhitespaces()
            .length
        val textToInsert = completionText.substring(0, nextLineStart + whitespacesLength)

        return createUpdatedVariant(event.toRequest().editor, variant, completionText, textToInsert)
    }

    private fun createUpdatedVariant(
        editor: Editor,
        variant: InlineCompletionVariant.Snapshot,
        completionText: String,
        textToInsert: String
    ): UpdateResult {
        updateRemainingCompletion(editor, textToInsert)
        return Changed(
            variant.copy(
                listOf(InlineCompletionGrayTextElement(completionText.removePrefix(textToInsert)))
            )
        )
    }

    private fun updateRemainingCompletion(editor: Editor, textToInsert: String) {
        val remainingCompletion = REMAINING_EDITOR_COMPLETION.get(editor) ?: ""
        if (remainingCompletion.isNotEmpty()) {
            REMAINING_EDITOR_COMPLETION.set(editor, remainingCompletion.removePrefix(textToInsert))
        }
    }
}