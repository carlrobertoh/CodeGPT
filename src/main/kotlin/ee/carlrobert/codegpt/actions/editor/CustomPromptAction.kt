package ee.carlrobert.codegpt.actions.editor

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UI
import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager
import ee.carlrobert.codegpt.ui.UIUtil
import ee.carlrobert.codegpt.util.file.FileUtil.getFileExtension
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JComponent
import javax.swing.JTextArea
import javax.swing.SwingUtilities

class CustomPromptAction internal constructor() :
  BaseEditorAction("Custom Prompt", "Custom prompt description", AllIcons.Actions.Run_anything) {
  init {
    EditorActionsUtil.registerAction(this)
  }

  override fun actionPerformed(project: Project, editor: Editor, selectedText: String?) {
    if (!selectedText.isNullOrBlank()) {
      val fileExtension = getFileExtension(editor.virtualFile.name)
      val dialog = CustomPromptDialog(previousUserPrompt)
      if (dialog.showAndGet()) {
        previousUserPrompt = dialog.userPrompt
        val message = Message(
          String.format("%s%n```%s%n%s%n```", previousUserPrompt, fileExtension, selectedText))
        message.userMessage = previousUserPrompt
        SwingUtilities.invokeLater {
          project.getService(ChatToolWindowContentManager::class.java).sendMessage(message)
        }
      }
    }
  }

  class CustomPromptDialog(previousUserPrompt: String) : DialogWrapper(true) {
    private val userPromptTextArea = JTextArea(previousUserPrompt)

    init {
      userPromptTextArea.caretPosition = previousUserPrompt.length
      title = "Custom Prompt"
      setSize(400, rootPane.preferredSize.height)
      init()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
      return userPromptTextArea
    }

    override fun createCenterPanel(): JComponent? {
      userPromptTextArea.lineWrap = true
      userPromptTextArea.wrapStyleWord = true
      userPromptTextArea.margin = JBUI.insets(5)
      UIUtil.addShiftEnterInputMap(userPromptTextArea, object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) {
          clickDefaultButton()
        }
      })

      return FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.panel(userPromptTextArea)
            .withLabel("Prefix:")
            .moveLabelOnTop()
            .withComment("Example: Find bugs in the following code")
            .createPanel())
        .panel
    }

    val userPrompt: String
      get() = userPromptTextArea.text
  }

  companion object {
    private var previousUserPrompt = ""
  }
}
