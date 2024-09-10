package ee.carlrobert.codegpt.actions.editor

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.Icon
import javax.swing.KeyStroke

class AddSelectionToContextAction : BaseEditorAction {

    constructor() : this(AllIcons.General.Add)

    constructor(icon: Icon) : super(
        "Add to Chat Context",
        "Adds the current selection to the chat context for generating code",
        icon
    ) {
        registerCustomShortcutSet(
            CustomShortcutSet(
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_I,
                    InputEvent.SHIFT_DOWN_MASK or InputEvent.META_DOWN_MASK
                )
            ), null
        )
        EditorActionsUtil.registerAction(this)
    }

    override fun actionPerformed(project: Project, editor: Editor, selectedText: String) {
        val chatToolWindowContentManager = project.service<ChatToolWindowContentManager>()
        val chatTabPanel = chatToolWindowContentManager
            .tryFindActiveChatTabPanel()
            .orElseThrow()
        chatTabPanel.addSelection(editor.virtualFile.name, editor.selectionModel)

    }
}
