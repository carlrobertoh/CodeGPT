package ee.carlrobert.codegpt.actions.editor

import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.ui.EditCodePopover
import javax.swing.Icon

class EditCodeAction : BaseEditorAction {

    constructor() : this(Icons.Sparkle)

    constructor(icon: Icon) : super(
        "Edit Code",
        "Allow LLM to edit code directly in your editor",
        icon
    ) {
        EditorActionsUtil.registerAction(this)
    }

    override fun actionPerformed(project: Project, editor: Editor, selectedText: String) {
        runInEdt {
            EditCodePopover(editor).show()
        }
    }
}
