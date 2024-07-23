package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.UnscaledGaps
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.settings.persona.PersonaSettings
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class SuggestionListCellRenderer : DefaultListCellRenderer() {

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
        return createDefaultPanel(component, icon, item.file.name, item.file.path)
    }

    private fun renderFolderItem(component: JLabel, item: SuggestionItem.FolderItem): JPanel {
        return createDefaultPanel(
            component,
            AllIcons.Nodes.Folder,
            item.folder.name,
            item.folder.path
        )
    }

    private fun renderActionItem(component: JLabel, item: SuggestionItem.ActionItem): JPanel {
        return createDefaultPanel(
            component.apply {
                disabledIcon = item.action.icon
                isEnabled = item.action.enabled
            },
            item.action.icon,
            item.action.displayName,
            if (item.action == DefaultAction.PERSONAS)
                service<PersonaSettings>().state.selectedPersona.persona
            else null
        )
    }

    private fun renderPersonaItem(component: JLabel, item: SuggestionItem.PersonaItem): JPanel {
        return createDefaultPanel(
            component,
            AllIcons.General.User,
            item.personaDetails.persona,
            item.personaDetails.prompt,
        )
    }

    private fun createDefaultPanel(
        label: JLabel,
        labelIcon: Icon,
        title: String,
        description: String? = null
    ): JPanel {
        label.apply {
            text = title
            icon = labelIcon
            iconTextGap = 4
        }

        if (description != null) {
            return panel {
                row {
                    cell(label)
                    text(description.truncate(480 - label.width - 28, false))
                        .customize(UnscaledGaps(left = 8))
                        .align(AlignX.RIGHT)
                        .applyToComponent {
                            font = JBUI.Fonts.smallFont()
                            foreground = JBColor.gray
                        }
                }
            }
        }

        return panel {
            row {
                cell(label)
            }
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

    private fun String.truncate(maxWidth: Int, fromEnd: Boolean = true): String {
        val fontMetrics = getFontMetrics(JBUI.Fonts.smallFont())
        if (fontMetrics.stringWidth(this) <= maxWidth) return this

        val ellipsis = "..."
        return if (fromEnd) {
            var truncated = this
            while (fontMetrics.stringWidth(ellipsis + truncated) > maxWidth && truncated.isNotEmpty()) {
                truncated = truncated.drop(1)
            }
            ellipsis + truncated
        } else {
            var truncated = this
            while (fontMetrics.stringWidth(truncated + ellipsis) > maxWidth && truncated.isNotEmpty()) {
                truncated = truncated.dropLast(1)
            }
            truncated + ellipsis
        }
    }
}