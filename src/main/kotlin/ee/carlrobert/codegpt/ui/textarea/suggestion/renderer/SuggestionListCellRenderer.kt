package ee.carlrobert.codegpt.ui.textarea.suggestion.renderer

import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.ui.textarea.PromptTextField
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.SuggestionItem
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class SuggestionListCellRenderer(textPane: PromptTextField) : DefaultListCellRenderer() {
    private val rendererFactory = RendererFactory(textPane)

    override fun getListCellRendererComponent(
        list: JList<*>?,
        value: Any?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component =
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus).apply {
            setOpaque(false)
        }.let { component ->
            if (component is JLabel && value is SuggestionItem) {
                rendererFactory.getRenderer(value)
                    .render(component, value)
                    .apply {
                        setupPanelProperties(list, index, isSelected, cellHasFocus)
                    }
            } else {
                component
            }
        }

    private fun JPanel.setupPanelProperties(
        list: JList<*>?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ) {
        preferredSize = Dimension(480, 30)
        border = JBUI.Borders.empty(0, 4, 0, 4)

        val isHovered = list?.getClientProperty("hoveredIndex") == index
        if (isHovered || isSelected || cellHasFocus) {
            background = UIManager.getColor("List.selectionBackground")
            foreground = UIManager.getColor("List.selectionForeground")
        }
    }
}