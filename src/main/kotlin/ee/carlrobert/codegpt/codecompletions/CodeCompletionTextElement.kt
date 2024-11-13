package ee.carlrobert.codegpt.codecompletions

import com.intellij.codeInsight.inline.completion.InlineCompletionFontUtils
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionColorTextElement
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange

class CodeCompletionTextElement(
    text: String,
    private val insertOffset: Int,
    val textRange: TextRange,
    val offsetDelta: Int = 0,
    val originalText: String = text,
) : InlineCompletionColorTextElement(text, InlineCompletionFontUtils::color) {

    override fun toPresentable(): InlineCompletionElement.Presentable =
        Presentable(this, insertOffset, textRange)

    open class Presentable(
        element: InlineCompletionElement,
        private val insertOffset: Int,
        private val textRange: TextRange,
    ) : InlineCompletionColorTextElement.Presentable(element, InlineCompletionFontUtils::color) {

        override fun render(editor: Editor, offset: Int) {
            super.render(editor, insertOffset)
        }

        override fun startOffset(): Int {
            return textRange.startOffset
        }

        override fun endOffset(): Int {
            return textRange.endOffset
        }
    }
}