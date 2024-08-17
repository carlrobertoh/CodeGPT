package ee.carlrobert.codegpt.ui.textarea.suggestion.renderer

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.UnscaledGaps
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.settings.persona.PersonaSettings
import ee.carlrobert.codegpt.ui.textarea.PromptTextField
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.*
import ee.carlrobert.codegpt.ui.textarea.suggestion.renderer.SuggestionItemRendererTextUtils.highlightSearchText
import ee.carlrobert.codegpt.ui.textarea.suggestion.renderer.SuggestionItemRendererTextUtils.searchText
import ee.carlrobert.codegpt.ui.textarea.suggestion.renderer.SuggestionItemRendererTextUtils.truncate
import java.awt.image.BufferedImage
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel

interface ItemRenderer {
    fun render(component: JLabel, value: SuggestionItem): JPanel
}

abstract class BaseItemRenderer(private val textField: PromptTextField) : ItemRenderer {
    protected fun createPanel(
        label: JLabel,
        icon: Icon,
        title: String,
        description: String?,
        toolTipText: String?,
        truncateFromStart: Boolean = false
    ): JPanel {
        val searchText = textField.text.searchText()
        label.apply {
            this.icon = icon
            disabledIcon = icon
            iconTextGap = 4
            text = searchText?.let { title.highlightSearchText(it) } ?: title
        }

        return panel {
            row {
                cell(label)
                if (description != null) {
                    text(description.truncate(480 - label.width - 32, truncateFromStart))
                        .customize(UnscaledGaps(left = 8))
                        .align(AlignX.RIGHT)
                        .applyToComponent {
                            font = JBUI.Fonts.smallFont()
                            foreground = JBColor.gray
                        }
                }
            }
        }.apply {
            this.toolTipText = toolTipText ?: description
        }
    }
}

class FileItemRenderer(textPane: PromptTextField) : BaseItemRenderer(textPane) {
    override fun render(component: JLabel, value: SuggestionItem): JPanel {
        val item = value as FileActionItem
        val icon =
            if (item.file.isDirectory) AllIcons.Nodes.Folder else service<FileTypeManager>().getFileTypeByFileName(
                item.file.name
            ).icon
        return createPanel(component, icon, item.file.name, item.file.path, null, true)
    }
}

class FolderItemRenderer(textPane: PromptTextField) : BaseItemRenderer(textPane) {
    override fun render(component: JLabel, value: SuggestionItem): JPanel {
        val item = value as FolderActionItem
        return createPanel(
            component,
            item.icon,
            item.displayName,
            item.folder.path,
            null,
            true
        )
    }
}

class DefaultItemRenderer(textPane: PromptTextField) : BaseItemRenderer(textPane) {
    companion object {
        private val EMPTY_ICON = ImageIcon(BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB))
    }

    override fun render(component: JLabel, value: SuggestionItem): JPanel {
        val label = component.apply {
            isEnabled = value.enabled
        }
        return createPanel(
            label,
            value.icon ?: EMPTY_ICON,
            getTitle(value),
            getDescription(value),
            if (value.enabled) null else "This action can only be used with CodeGPT provider"
        ).apply {
            isEnabled = value.enabled
        }
    }

    private fun getTitle(item: SuggestionItem) =
        if (item is SuggestionGroupItem) {
            "${item.displayName} →"
        } else {
            item.displayName
        }

    private fun getDescription(item: SuggestionItem) =
        if (item is PersonaSuggestionGroupItem) {
            service<PersonaSettings>().state.selectedPersona.name
        } else {
            null
        }
}

class PersonaItemRenderer(textPane: PromptTextField) : BaseItemRenderer(textPane) {
    override fun render(component: JLabel, value: SuggestionItem): JPanel {
        val item = value as PersonaActionItem
        return createPanel(
            component,
            AllIcons.General.User,
            item.displayName,
            item.personaDetails.instructions,
            null,
        )
    }
}

class DocumentationItemRenderer(textPane: PromptTextField) : BaseItemRenderer(textPane) {
    override fun render(component: JLabel, value: SuggestionItem): JPanel {
        val item = value as DocumentationActionItem
        return createPanel(
            component,
            AllIcons.Toolwindows.Documentation,
            item.displayName,
            item.documentationDetails.url,
            null,
        )
    }
}

class RendererFactory(private val textPane: PromptTextField) {
    fun getRenderer(item: SuggestionItem): ItemRenderer {
        return when (item) {
            is FileActionItem -> FileItemRenderer(textPane)
            is FolderActionItem -> FolderItemRenderer(textPane)
            is PersonaActionItem -> PersonaItemRenderer(textPane)
            is DocumentationActionItem -> DocumentationItemRenderer(textPane)
            else -> DefaultItemRenderer(textPane)
        }
    }
}