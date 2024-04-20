package ee.carlrobert.codegpt.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import ee.carlrobert.codegpt.telemetry.TelemetryAction
import javax.swing.Icon

abstract class TrackableAction(
  @JvmField protected val editor: Editor,
  text: String?,
  description: String?,
  icon: Icon?,
  private val actionType: ActionType
) : AnAction(text, description, icon) {

  abstract fun handleAction(e: AnActionEvent)

  override fun actionPerformed(e: AnActionEvent) {
    try {
      handleAction(e)
      TelemetryAction.IDE_ACTION
        .createActionMessage()
        .property("group", null)
        .property("action", actionType.name)
        .send()
    } catch (ex: Exception) {
      TelemetryAction.IDE_ACTION_ERROR
        .createActionMessage()
        .property("group", null)
        .property("action", actionType.name)
        .error(ex)
        .send()
      throw ex
    }
  }
}
