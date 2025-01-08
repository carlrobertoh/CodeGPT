package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.ActionPromoter
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DataContext
import ee.carlrobert.codegpt.predictions.OpenPredictionAction
import ee.carlrobert.codegpt.predictions.TriggerCustomPredictionAction

class InlayActionPromoter : ActionPromoter {
    override fun promote(actions: List<AnAction>, context: DataContext): List<AnAction> {
        actions.filterIsInstance<TriggerCustomPredictionAction>().takeIf { it.isNotEmpty() }?.let { return it }
        actions.filterIsInstance<OpenPredictionAction>().takeIf { it.isNotEmpty() }?.let { return it }
        return emptyList()
    }
}