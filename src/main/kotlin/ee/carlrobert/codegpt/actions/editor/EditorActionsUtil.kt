package ee.carlrobert.codegpt.actions.editor

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.CodeGPTKeys
import ee.carlrobert.codegpt.ReferencedFile
import ee.carlrobert.codegpt.actions.IncludeFilesInContextAction
import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager
import ee.carlrobert.codegpt.util.file.FileUtil.getFileExtension
import org.apache.commons.text.CaseUtils
import java.util.stream.Stream

object EditorActionsUtil {
  @JvmField
  val DEFAULT_ACTIONS: Map<String, String> = LinkedHashMap(mapOf(
      "Find Bugs" to "Find bugs and output code with bugs fixed in the following code: {{selectedCode}}",
      "Write Tests" to "Write Tests for the selected code {{selectedCode}}",
      "Explain" to "Explain the selected code {{selectedCode}}",
      "Refactor" to "Refactor the selected code {{selectedCode}}",
      "Optimize" to "Optimize the selected code {{selectedCode}}"))

  @JvmField
  val DEFAULT_ACTIONS_ARRAY = toArray(DEFAULT_ACTIONS)

  @JvmStatic
  fun toArray(actionsMap: Map<String, String>): Array<Array<String>> {
    return actionsMap.entries.stream()
      .map { arrayOf(it.key, it.value) }
      .toArray { arrayOfNulls<Array<String>>(it) }
  }

  @JvmStatic
  fun refreshActions() {
    val actionGroup = ActionManager.getInstance()
      .getAction("action.editor.group.EditorActionGroup") as? DefaultActionGroup ?: return
    actionGroup.removeAll()
    actionGroup.add(AskAction())
    actionGroup.add(CustomPromptAction())
    actionGroup.addSeparator()

    val configuredActions = ConfigurationSettings.getCurrentState().tableData
    configuredActions.forEach { (label: String?, prompt: String) ->
      // using label as action description to prevent com.intellij.diagnostic.PluginException
      // https://github.com/carlrobertoh/CodeGPT/issues/95
      val action: BaseEditorAction = object : BaseEditorAction(label, label) {
        override fun actionPerformed(project: Project, editor: Editor, selectedText: String?) {
          val fileExtension = getFileExtension(editor.virtualFile.name)
          val message = Message(prompt.replace(
              "{{selectedCode}}",
              String.format("%n```%s%n%s%n```", fileExtension, selectedText)))
          message.userMessage = prompt.replace("{{selectedCode}}", "")
          val toolWindowContentManager =
            project.getService(ChatToolWindowContentManager::class.java)
          toolWindowContentManager.toolWindow.show()

          message.referencedFilePaths = Stream.ofNullable(project.getUserData(CodeGPTKeys.SELECTED_FILES))
            .flatMap(MutableList<ReferencedFile>::stream)
            .map { it.filePath }
            .toList()
          toolWindowContentManager.sendMessage(message)
        }
      }
      actionGroup.add(action)
    }
    actionGroup.addSeparator()
    actionGroup.add(IncludeFilesInContextAction("action.includeFileInContext.title"))
  }

  @JvmStatic
  fun registerAction(action: AnAction) {
    val actionManager = ActionManager.getInstance()
    val actionId = convertToId(action.templateText)
    if (actionManager.getAction(actionId) == null) {
      actionManager.registerAction(actionId, action, PluginId.getId("ee.carlrobert.chatgpt"))
    }
  }

  @JvmStatic
  fun convertToId(label: String?): String {
    return "codegpt." + CaseUtils.toCamelCase(label, true)
  }
}
