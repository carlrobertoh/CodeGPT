package ee.carlrobert.codegpt.ui.textarea.header.tag

import com.intellij.icons.AllIcons
import com.intellij.icons.AllIcons.Actions.Close
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import com.jetbrains.rd.util.UUID
import ee.carlrobert.codegpt.ui.textarea.header.PaintUtil
import ee.carlrobert.codegpt.util.EditorUtil.getSelectedEditor
import ee.carlrobert.codegpt.util.EditorUtil.getSelectedEditorFile
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities

abstract class TagPanel(
    var tagDetails: TagDetails,
    private val selectable: Boolean = true
) : JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)) {

    val id: UUID = tagDetails.id

    private val label = TagLabel(tagDetails)
    private val closeButton = CloseButton {
        isVisible = true
        onClose()
    }.apply {
        isVisible = tagDetails.selected
    }

    init {
        setupUI()
    }

    abstract fun onSelect(tagDetails: TagDetails)

    abstract fun onClose()

    fun update(text: String, icon: Icon? = null) {
        label.text = text
        icon?.let {
            label.icon = IconUtil.scale(it, null, 0.65f)
        }
        closeButton.isVisible = tagDetails.selected
        label.foreground = if (tagDetails.selected) {
            service<EditorColorsManager>().globalScheme.defaultForeground
        } else {
            JBUI.CurrentTheme.Label.disabledForeground(false)
        }
        revalidate()
        repaint()
    }

    override fun getPreferredSize(): Dimension {
        val size = super.getPreferredSize()
        return Dimension(size.width, 20)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        PaintUtil.drawRoundedBackground(g, this, tagDetails.selected)
    }

    private fun setupUI() {
        isOpaque = false
        border = JBUI.Borders.empty(2, 8)
        cursor = if (selectable) Cursor(Cursor.HAND_CURSOR) else Cursor(Cursor.DEFAULT_CURSOR)

        add(label)
        add(closeButton)
        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (!selectable) {
                    return
                }

                onSelect(tagDetails)
            }
        })
    }

    private class TagLabel(tagDetails: TagDetails) : JBLabel(tagDetails.name) {

        init {
            if (tagDetails.icon != null) {
                icon = IconUtil.scale(tagDetails.icon, null, 0.65f)
                horizontalAlignment = SwingUtilities.LEADING
                foreground = if (tagDetails.selected) {
                    service<EditorColorsManager>().globalScheme.defaultForeground
                } else {
                    JBUI.CurrentTheme.Label.disabledForeground(false)
                }
                font = JBUI.Fonts.miniFont()
            }
        }

        override fun getPreferredSize(): Dimension {
            val size = super.getPreferredSize()
            return Dimension(size.width, 16)
        }
    }

    private class CloseButton(onClose: () -> Unit) : JButton(Close) {
        init {
            addActionListener {
                onClose()
            }

            preferredSize = Dimension(Close.iconWidth, Close.iconHeight)
            border = JBUI.Borders.emptyLeft(4)
            isContentAreaFilled = false
            toolTipText = "Remove"
            rolloverIcon = AllIcons.Actions.CloseHovered
        }
    }
}

abstract class SelectedFileTagPanel(
    private val project: Project,
    virtualFile: VirtualFile? = getSelectedEditorFile(project)
) : TagPanel(
    if (virtualFile == null) TagDetails("")
    else FileTagDetails(virtualFile)
) {

    init {
        isVisible = getSelectedEditorFile(project) != null
    }

    override fun onSelect(tagDetails: TagDetails) {
        if (tagDetails is FileTagDetails) {
            tagDetails.selected = !tagDetails.selected
            update(tagDetails.virtualFile.name, tagDetails.virtualFile.fileType.icon)
        }
    }

    fun update(tagDetails: FileTagDetails) {
        this.tagDetails = tagDetails
        isVisible = true
        update(tagDetails.name, tagDetails.virtualFile.fileType.icon)
    }
}

class SelectionTagPanel(project: Project) : TagPanel(
    getDefaultSelectionTagDetails(project),
    false
) {

    companion object {
        fun getDefaultSelectionTagDetails(project: Project): TagDetails {
            val editor = getSelectedEditor(project)
            val selectionModel = editor?.selectionModel
            return if (selectionModel?.hasSelection() == true) {
                SelectionTagDetails(editor.virtualFile, selectionModel)
            } else {
                EmptyTagDetails()
            }
        }
    }

    init {
        isVisible = tagDetails !is EmptyTagDetails
    }

    override fun onSelect(tagDetails: TagDetails) {
    }

    override fun onClose() {
        (tagDetails as? SelectionTagDetails)?.selectionModel?.removeSelection()
    }

    fun update(virtualFile: VirtualFile, selectionModel: SelectionModel) {
        tagDetails = SelectionTagDetails(virtualFile, selectionModel)
        isVisible = selectionModel.hasSelection()
        update(
            "${virtualFile.name}:${selectionModel.selectionStart}-${selectionModel.selectionEnd}",
            virtualFile.fileType.icon
        )
    }
}