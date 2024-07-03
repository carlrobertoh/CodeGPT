package ee.carlrobert.codegpt.actions.editor

import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.ui.EditCodePopover
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.Icon
import javax.swing.KeyStroke

class EditCodeAction : BaseEditorAction {

    constructor() : this(Icons.Sparkle)

    constructor(icon: Icon) : super(
        "Edit Code",
        "Allow LLM to edit code directly in your editor",
        icon
    ) {
        registerCustomShortcutSet(
            CustomShortcutSet(
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_E,
                    InputEvent.SHIFT_DOWN_MASK or InputEvent.META_DOWN_MASK
                )
            ), null
        )
        EditorActionsUtil.registerAction(this)
    }

    override fun actionPerformed(project: Project, editor: Editor, selectedText: String) {
        runInEdt {
            EditCodePopover(editor).show()
        }
    }
}
