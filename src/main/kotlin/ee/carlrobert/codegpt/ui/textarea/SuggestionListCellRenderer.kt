package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.ui.ColorUtil
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.settings.persona.PersonaSettings
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class SuggestionListCellRenderer(
    private val textPane: CustomTextPane
) : DefaultListCellRenderer() {

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
        is SuggestionItem.FolderItem -> renderFolderItem(component, value)
        is SuggestionItem.ActionItem -> renderActionItem(component, value)
        is SuggestionItem.PersonaItem -> renderPersonaItem(component, value)
    }.apply {
        setupPanelProperties(list, index, isSelected, cellHasFocus)
    }

    private fun renderFileItem(component: JLabel, item: SuggestionItem.FileItem): JPanel {
        val icon = when {
            item.file.isDirectory -> AllIcons.Nodes.Folder
            else -> service<FileTypeManager>().getFileTypeByFileName(item.file.name).icon
        }
        return createDefaultPanel(component, icon, item.file.name, item.file.path, true)
    }

    private fun renderFolderItem(component: JLabel, item: SuggestionItem.FolderItem): JPanel {
        return createDefaultPanel(
            component,
            AllIcons.Nodes.Folder,
            item.folder.name,
            item.folder.path,
            true
        )
    }

    private fun renderActionItem(component: JLabel, item: SuggestionItem.ActionItem): JPanel {
        val description = if (item.action == DefaultAction.PERSONAS)
            service<PersonaSettings>().state.selectedPersona.name
        else null

        return createDefaultPanel(
            component.apply {
                disabledIcon = item.action.icon
                isEnabled = item.action.enabled
            },
            item.action.icon,
            item.action.displayName,
            description
        )
    }

    private fun renderPersonaItem(component: JLabel, item: SuggestionItem.PersonaItem): JPanel {
        component.apply {
            icon = AllIcons.General.User
            iconTextGap = 4
            val searchText = getSearchText(textPane.text)
            text = if (searchText != null) {
                generateHighlightedHtml(item.personaDetails.name, searchText)
            } else {
                item.personaDetails.name
            }
        }

        return JPanel(BorderLayout()).apply {
            add(component, BorderLayout.LINE_START)
            add(
                JBLabel(item.personaDetails.instructions)
                    .apply {
                        font = JBUI.Fonts.smallFont()
                        foreground = JBColor.gray
                        border = JBUI.Borders.emptyLeft(22)
                    },
                BorderLayout.SOUTH
            )
        }
    }

    private fun getSearchText(text: String): String? {
        val lastAtIndex = text.lastIndexOf('@')
        if (lastAtIndex == -1) return null

        val lastColonIndex = text.lastIndexOf(':')
        if (lastColonIndex == -1) return null

        return text.substring(lastColonIndex + 1).takeIf { it.isNotEmpty() }
    }

    private fun generateHighlightedHtml(title: String, searchText: String): String {
        val searchIndex = title.indexOf(searchText, ignoreCase = true)
        if (searchIndex == -1) return title

        val prefix = title.substring(0, searchIndex)
        val highlight = title.substring(
            searchIndex,
            (searchIndex + searchText.length).coerceAtMost(title.length)
        )
        val suffix = title.substring((searchIndex + searchText.length).coerceAtMost(title.length))

        val foregroundHex = ColorUtil.toHex(JBColor.foreground())
        val backgroundHex = ColorUtil.toHex(JBColor.background())

        return "<html>$prefix<span style=\"color: $foregroundHex;background-color: $backgroundHex;\">$highlight</span>$suffix</html>"
    }

    private fun createDefaultPanel(
        label: JLabel,
        labelIcon: Icon,
        title: String,
        description: String? = null,
        truncateFromStart: Boolean = false
    ): JPanel {
        val searchText = getSearchText(textPane.text)
        label.apply {
            icon = labelIcon
            iconTextGap = 4
            text = if (searchText != null) {
                generateHighlightedHtml(title, searchText)
            } else {
                title
            }
        }

        return panel {
            row {
                cell(label)
                if (description != null) {
                    text(description.truncate(480 - label.width - 32, truncateFromStart))
                        .align(AlignX.RIGHT)
                        .applyToComponent {
                            font = JBUI.Fonts.smallFont()
                            foreground = JBColor.gray
                        }
                }
            }
        }.apply {
            toolTipText = description
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

    private fun String.truncate(maxWidth: Int, truncateFromStart: Boolean = false): String {
        val fontMetrics = getFontMetrics(JBUI.Fonts.smallFont())
        if (fontMetrics.stringWidth(this) <= maxWidth) return this

        val ellipsis = "..."
        var truncated = this
        while (fontMetrics.stringWidth(ellipsis + truncated) > maxWidth && truncated.isNotEmpty()) {
            truncated = if (truncateFromStart) {
                truncated.drop(1)
            } else {
                truncated.dropLast(1)
            }
        }
        return if (truncateFromStart) ellipsis + truncated else truncated + ellipsis
    }
}