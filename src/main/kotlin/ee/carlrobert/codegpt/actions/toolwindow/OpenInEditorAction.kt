package ee.carlrobert.codegpt.actions.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.testFramework.LightVirtualFile
import ee.carlrobert.codegpt.actions.ActionType
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil.registerAction
import ee.carlrobert.codegpt.conversations.ConversationsState.getCurrentConversation
import ee.carlrobert.codegpt.telemetry.TelemetryAction
import java.time.format.DateTimeFormatter
import java.util.Objects.requireNonNull
import java.util.stream.Collectors

class OpenInEditorAction : AnAction("Open In Editor", "Open conversation in editor", AllIcons.Actions.SplitVertically) {
  init {
    registerAction(this)
  }

  override fun update(event: AnActionEvent) {
    super.update(event)
    event.presentation.isEnabled = getCurrentConversation()?.messages?.isNotEmpty() == true
  }

  override fun actionPerformed(e: AnActionEvent) {
    try {
      val project = e.project ?: return
      val currentConversation = getCurrentConversation() ?: return
      val dateTimeStamp = currentConversation.updatedOn
        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
      val fileName = String.format("%s_%s.md", currentConversation.model, dateTimeStamp)
      val fileContent = currentConversation.messages.stream()
        .map { String.format("### User:%n%s%n### CodeGPT:%n%s%n", it.prompt, it.response) }
        .collect(Collectors.joining())
      val file: VirtualFile = LightVirtualFile(fileName, fileContent)
      FileEditorManager.getInstance(project).openFile(file, true)
      requireNonNull(ToolWindowManager.getInstance(project).getToolWindow("CodeGPT"))!!
        .hide()
    } finally {
      TelemetryAction.IDE_ACTION.createActionMessage()
        .property("action", ActionType.OPEN_CONVERSATION_IN_EDITOR.name)
        .send()
    }
  }

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.BGT
  }
}
