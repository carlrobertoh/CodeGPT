package ee.carlrobert.codegpt.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.settings.service.ServiceConfigurable

class OpenSettingsAction : AnAction(
  CodeGPTBundle.get("action.openSettings.title"),
  CodeGPTBundle.get("action.openSettings.description"),
  AllIcons.General.Settings) {
  override fun actionPerformed(e: AnActionEvent) {
    ShowSettingsUtil.getInstance().showSettingsDialog(e.project, ServiceConfigurable::class.java)
  }
}
