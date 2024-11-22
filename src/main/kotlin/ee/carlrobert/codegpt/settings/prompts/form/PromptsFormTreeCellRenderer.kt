package ee.carlrobert.codegpt.settings.prompts.form

import com.intellij.ui.render.LabelBasedRenderer
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.settings.prompts.form.details.PersonaPromptDetails
import java.awt.Component
import java.awt.Font
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

class PromptsFormTreeCellRenderer(
    private val root: DefaultMutableTreeNode
) : LabelBasedRenderer.Tree() {
    override fun getTreeCellRendererComponent(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        focused: Boolean
    ): Component {
        super.getTreeCellRendererComponent(
            tree,
            value,
            selected,
            expanded,
            leaf,
            row,
            focused
        )

        val node = value as? DefaultMutableTreeNode
        font = if (node?.parent == root) {
            font.deriveFont(Font.BOLD)
        } else {
            font.deriveFont(Font.PLAIN)
        }

        if (node is PromptDetailsTreeNode && node.category == PromptCategory.PERSONAS) {
            val personaDetails = node.details as PersonaPromptDetails
            icon = if (personaDetails.selected.get()) Icons.GreenCheckmark else null
            iconTextGap = 6
        } else {
            icon = null
        }
        horizontalTextPosition = LEFT

        return this
    }
}