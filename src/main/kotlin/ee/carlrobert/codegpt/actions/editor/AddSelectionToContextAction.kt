package ee.carlrobert.codegpt.actions.editor

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager

class AddSelectionToContextAction : BaseEditorAction(AllIcons.General.Add) {

    override fun actionPerformed(project: Project, editor: Editor, selectedText: String) {
        val chatToolWindowContentManager = project.service<ChatToolWindowContentManager>()
        val chatTabPanel = chatToolWindowContentManager
            .tryFindActiveChatTabPanel()
            .orElseThrow()
        chatTabPanel.addSelection(editor.virtualFile.name, editor.selectionModel)
    }
}
