package ee.carlrobert.codegpt.actions

import com.intellij.codeInsight.inline.completion.session.InlineCompletionContext
import com.intellij.openapi.actionSystem.ActionPromoter
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import ee.carlrobert.codegpt.codecompletions.AcceptNextLineInlayAction
import ee.carlrobert.codegpt.codecompletions.AcceptNextWordInlayAction

class InlayActionPromoter : ActionPromoter {
    override fun promote(actions: List<AnAction>, context: DataContext): List<AnAction> {
        val editor = CommonDataKeys.EDITOR.getData(context) ?: return emptyList()

        if (InlineCompletionContext.getOrNull(editor) == null) {
            return emptyList()
        }

        actions.filterIsInstance<AcceptNextWordInlayAction>().takeIf { it.isNotEmpty() }?.let { return it }
        actions.filterIsInstance<AcceptNextLineInlayAction>().takeIf { it.isNotEmpty() }?.let { return it }
        return emptyList()
    }
}