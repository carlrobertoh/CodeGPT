package ee.carlrobert.codegpt.ui.textarea.header

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import com.jetbrains.rd.util.UUID
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.settings.prompts.PersonaDetails
import ee.carlrobert.codegpt.ui.DocumentationDetails
import ee.carlrobert.codegpt.util.EditorUtil
import java.awt.Cursor
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JPanel
import javax.swing.SwingUtilities
import git4idea.GitCommit as Git4IdeaGitCommit

open class HeaderTagDetails(
    open val name: String,
    val icon: Icon? = null,
    open var selected: Boolean = true
) {
    val id: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HeaderTagDetails) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class FileTagDetails(var virtualFile: VirtualFile, override var selected: Boolean = true) :
    HeaderTagDetails(virtualFile.name, virtualFile.fileType.icon)

data class SelectionTagDetails(
    var virtualFile: VirtualFile?,
    var selectionModel: SelectionModel?,
    var selectedText: String?
) :
    HeaderTagDetails(
        "${virtualFile?.name} (${selectionModel?.selectionStartPosition?.line}:${selectionModel?.selectionEndPosition?.line})",
        Icons.InSelection
    )

data class DocumentationTagDetails(var documentationDetails: DocumentationDetails) :
    HeaderTagDetails(documentationDetails.name, AllIcons.Toolwindows.Documentation)

data class PersonaTagDetails(var personaDetails: PersonaDetails) :
    HeaderTagDetails(personaDetails.name, AllIcons.General.User)

data class GitCommitTagDetails(var gitCommit: Git4IdeaGitCommit) :
    HeaderTagDetails(gitCommit.id.asString().take(6), AllIcons.Vcs.CommitNode)

class CurrentGitChangesTagDetails :
    HeaderTagDetails("Current Git Changes", AllIcons.Vcs.CommitNode)

data class FolderTagDetails(var folder: VirtualFile) :
    HeaderTagDetails(folder.name, AllIcons.Nodes.Folder)

class WebTagDetails : HeaderTagDetails("Web", AllIcons.General.Web)

abstract class HeaderTag(val tagDetails: HeaderTagDetails, private val selectable: Boolean = true) :
    JPanel() {

    val id: UUID = tagDetails.id

    private val label = createLabel(tagDetails)
    private val closeButton = CloseButton {
        isVisible = true
        onClose()
    }.apply {
        isVisible = tagDetails.selected
    }

    init {
        setupUI()
    }

    abstract fun onClose()

    abstract fun onSelect(tagDetails: HeaderTagDetails)

    fun updateLabel(text: String, icon: Icon) {
        label.text = text
        label.icon = IconUtil.scale(icon, null, 0.65f)
    }

    open fun select() {
        onSelect(tagDetails)

        if (!tagDetails.selected) {
            tagDetails.selected = true
            closeButton.isVisible = true
            label.foreground = service<EditorColorsManager>().globalScheme.defaultForeground
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        PaintUtil.drawRoundedBackground(g, this, tagDetails.selected)
    }

    private fun createLabel(tagDetails: HeaderTagDetails): JBLabel {
        return (if (tagDetails.icon == null) {
            JBLabel(tagDetails.name)
        } else {
            JBLabel(tagDetails.name, tagDetails.icon.scale(), SwingUtilities.LEADING)
        }).apply {
            foreground = if (tagDetails.selected) {
                service<EditorColorsManager>().globalScheme.defaultForeground
            } else {
                JBUI.CurrentTheme.Label.disabledForeground(false)
            }
            font = JBUI.Fonts.miniFont()
        }
    }

    private fun setupUI() {
        isOpaque = false
        layout = FlowLayout(FlowLayout.LEFT, 0, 2)
        border = JBUI.Borders.empty(0, 6, 0, 4)
        cursor = if (selectable) Cursor(Cursor.HAND_CURSOR) else Cursor(Cursor.DEFAULT_CURSOR)

        add(label)
        add(closeButton)
        if (selectable) {
            addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    select()
                    repaint()
                }
            })
        }
    }
}

abstract class SelectedFileHeaderTag(
    private val project: Project,
    var virtualFile: VirtualFile? = project.getSelectedEditorFile()
) : HeaderTag(
    HeaderTagDetails(
        virtualFile?.name ?: "",
        virtualFile?.fileType?.icon
    )
) {

    init {
        isVisible = project.getSelectedEditorFile() != null
    }

    override fun onSelect(tagDetails: HeaderTagDetails) {
        if (tagDetails is FileTagDetails) {
            project.service<FileEditorManager>().openFile(tagDetails.virtualFile)
        }
    }

    fun update(virtualFile: VirtualFile) {
        this.virtualFile = virtualFile
        isVisible = true
        updateLabel(virtualFile.name, virtualFile.fileType.icon)
    }
}

class SelectionHeaderTag(
    private val project: Project,
    var selectedEditor: Editor? = project.getSelectedEditor()
) : HeaderTag(
    SelectionTagDetails(
        selectedEditor?.virtualFile,
        selectedEditor?.selectionModel,
        selectedEditor?.selectionModel?.selectedText
    ),
    false
) {

    init {
        isVisible = selectedEditor?.selectionModel?.hasSelection() ?: false
    }

    override fun onSelect(tagDetails: HeaderTagDetails) {
    }

    override fun onClose() {
        selectedEditor?.selectionModel?.removeSelection()
    }

    fun update(virtualFile: VirtualFile, selectionModel: SelectionModel) {
        isVisible = selectionModel.hasSelection()
        updateLabel(
            "${virtualFile.name}:${selectionModel.selectionStart}-${selectionModel.selectionEnd}",
            virtualFile.fileType.icon
        )
    }
}

fun Icon.scale() = IconUtil.scale(this, null, 0.65f)

fun Project.getSelectedEditorFile(): VirtualFile? {
    return this.getSelectedEditor()?.virtualFile
}

fun Project.getSelectedEditor(): Editor? {
    return EditorUtil.getSelectedEditor(this)
}