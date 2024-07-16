package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import java.awt.Component
import java.awt.Dimension
import java.awt.KeyboardFocusManager
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class SuggestionList(
    listModel: DefaultListModel<SuggestionItem>,
    private val onSelected: (SuggestionItem) -> Unit
) : JBList<SuggestionItem>(listModel) {

    init {
        border = JBUI.Borders.empty()
        preferredSize = Dimension(480, (30 * 6))
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = SuggestionsListCellRenderer()
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher { e ->
            if (e.keyCode == KeyEvent.VK_TAB && e.id == KeyEvent.KEY_PRESSED && isFocusOwner) {
                selectNext()
                e.consume()
                true
            } else {
                false
            }
        }
        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_ENTER -> {
                        onSelected(listModel.get(selectedIndex))
                        e.consume()
                    }
                }
            }
        })
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val index = locationToIndex(e.point)
                if (index >= 0) {
                    onSelected(listModel.getElementAt(index))
                }
            }

            override fun mouseExited(e: MouseEvent) {
                putClientProperty("hoveredIndex", -1)
                repaint()
            }
        })
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                val index = locationToIndex(e.point)
                if (index != getClientProperty("hoveredIndex")) {
                    putClientProperty("hoveredIndex", index)
                    repaint()
                }
            }
        })
    }

    fun selectNext() {
        val newIndex = if (selectedIndex < model.size - 1) selectedIndex + 1 else 0
        selectedIndex = newIndex
        ensureIndexIsVisible(newIndex)
    }
}

private class SuggestionsListCellRenderer : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus).apply {
        setOpaque(false)
    }.let { component ->
        if (component is JLabel && value is SuggestionItem) {
            renderSuggestionItem(component, value, list, index, isSelected, cellHasFocus)
        } else {
            component
        }
    }

    private fun renderSuggestionItem(
        component: JLabel,
        value: SuggestionItem,
        list: JList<*>?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): JPanel = when (value) {
        is SuggestionItem.FileItem -> renderFileItem(component, value)
        is SuggestionItem.ActionItem -> renderActionItem(component, value)
    }.apply {
        setupPanelProperties(list, index, isSelected, cellHasFocus)
    }

    private fun renderFileItem(component: JLabel, value: SuggestionItem.FileItem): JPanel {
        val file = value.file
        component.apply {
            text = file.name
            icon = when {
                file.isDirectory -> AllIcons.Nodes.Folder
                else -> FileTypeManager.getInstance().getFileTypeByFileName(file.name).icon
            }
            iconTextGap = 4
        }

        return panel {
            row {
                cell(component)
                text(truncatePath(480 - component.width - 28, file.path))
                    .align(AlignX.RIGHT)
                    .applyToComponent {
                        font = JBUI.Fonts.smallFont()
                        foreground = JBColor.gray
                    }
            }
        }
    }

    private fun renderActionItem(component: JLabel, value: SuggestionItem.ActionItem): JPanel {
        component.apply {
            text = value.action.displayName
            icon = value.action.icon
            iconTextGap = 4
        }
        return panel {
            row {
                cell(component)
            }
        }
    }

    private fun JPanel.setupPanelProperties(
        list: JList<*>?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ) {
        preferredSize = Dimension(preferredSize.width, 30)
        border = JBUI.Borders.empty(0, 4, 0, 4)

        val isHovered = list?.getClientProperty("hoveredIndex") == index
        if (isHovered || isSelected || cellHasFocus) {
            background = UIManager.getColor("List.selectionBackground")
            foreground = UIManager.getColor("List.selectionForeground")
        }
    }

    private fun truncatePath(maxWidth: Int, fullPath: String): String {
        val fontMetrics = getFontMetrics(JBUI.Fonts.smallFont())

        if (fontMetrics.stringWidth(fullPath) <= maxWidth) {
            return fullPath
        }

        val ellipsis = "..."
        var truncatedPath = fullPath
        while (truncatedPath.isNotEmpty() && fontMetrics.stringWidth(ellipsis + truncatedPath) > maxWidth) {
            truncatedPath = truncatedPath.substring(1)
        }
        return ellipsis + truncatedPath
    }
}