package ee.carlrobert.codegpt.ui.textarea.header

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorKind
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.EditorNotifier
import ee.carlrobert.codegpt.actions.IncludeFilesInContextNotifier
import ee.carlrobert.codegpt.ui.WrapLayout
import ee.carlrobert.codegpt.ui.textarea.header.tag.*
import ee.carlrobert.codegpt.ui.textarea.suggestion.SuggestionsPopupManager
import ee.carlrobert.codegpt.util.EditorUtil
import ee.carlrobert.codegpt.util.EditorUtil.getSelectedEditor
import ee.carlrobert.codegpt.util.EditorUtil.getSelectedEditorFile
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Graphics
import javax.swing.JButton
import javax.swing.JPanel
import kotlin.math.min

class UserInputHeaderPanel(
    private val project: Project,
    suggestionsPopupManager: SuggestionsPopupManager
) : JPanel(WrapLayout(FlowLayout.LEFT, 4, 4)), TagManagerListener {

    companion object {
        private const val INITIAL_VISIBLE_FILES = 2
        private const val TAG_INSERTION_OFFSET = 1
    }

    private val tagManager = TagManager()
    private val selectedFileTagPanel = object : SelectedFileTagPanel(project) {
        override fun onClose() {
            this.isVisible = false
            if (tagManager.getTags().isEmpty()) {
                emptyText.isVisible = true
            }
        }
    }
    private val emptyText = JBLabel("No context included").apply {
        foreground = JBUI.CurrentTheme.Label.disabledForeground()
        font = JBUI.Fonts.smallFont()
        border = JBUI.Borders.empty(4, 4, 4, 0)
        isVisible = getSelectedEditor(project) == null
    }
    private val selectionTagPanel = SelectionTagPanel(project)
    private val defaultHeaderTagsPanel = CustomFlowPanel().apply {
        add(AddButton {
            if (suggestionsPopupManager.isPopupVisible()) {
                suggestionsPopupManager.hidePopup()
            } else {
                suggestionsPopupManager.showPopup(this)
            }
        })
        add(emptyText)
        add(selectionTagPanel)
        add(selectedFileTagPanel)
    }

    init {
        tagManager.addListener(this)
        initializeUI()
        initializeEventListeners()
    }

    fun getSelectedTags(): List<TagDetails> {
        val selectedTags = tagManager.getSelectedTags().toMutableList()

        val selectedFile = getSelectedFile()
        if (selectedFileTagPanel.isVisible && selectedFileTagPanel.tagDetails.selected && selectedFile != null) {
            selectedTags.add(FileTagDetails(selectedFile))
        }

        (selectionTagPanel.tagDetails as? SelectionTagDetails)?.let {
            selectedTags.add(it)
        }

        return selectedTags
    }

    fun addTag(tagDetails: TagDetails) {
        tagManager.addTag(tagDetails)
    }

    override fun onTagAdded(tag: TagDetails) {
        emptyText.isVisible = false
        add(createTag(tag), getNextInsertionIndex())
        revalidate()
        repaint()
    }

    private fun getNextInsertionIndex(): Int {
        val lastSelectionTagIndex = getLastSelectedTagIndex()
        return if (lastSelectionTagIndex != -1) {
            min(lastSelectionTagIndex + TAG_INSERTION_OFFSET + 1, components.size)
        } else {
            TAG_INSERTION_OFFSET
        }
    }

    override fun onTagRemoved(tag: TagDetails) {
        val componentToRemove =
            components.find { it is TagPanel && it.id == tag.id } ?: return
        remove(componentToRemove)

        if (getSelectedEditorFile(project) == null) {
            selectedFileTagPanel.isVisible = false
        }

        if (tagManager.getTags().isEmpty() && !selectedFileTagPanel.isVisible) {
            emptyText.isVisible = true
        }

        revalidate()
        repaint()
    }

    override fun onTagSelectionChanged(tag: TagDetails) {
        val existingTagComponent =
            components.filterIsInstance<TagPanel>().find { it.id == tag.id }
                ?: return
        existingTagComponent.update(tag.name, tag.icon)
        updateTagPosition(existingTagComponent)
    }

    private fun createTag(tagDetails: TagDetails) =
        object : TagPanel(tagDetails, tagDetails is FileTagDetails) {
            override fun onSelect(tagDetails: TagDetails) {
                if (tagDetails is FileTagDetails) {
                    if (tagDetails.selected) {
                        project.service<FileEditorManager>().openFile(tagDetails.virtualFile, true)
                        return
                    }

                    tagDetails.selected = true

                    val canAddNewTag = tagManager.getTags()
                        .filterIsInstance<FileTagDetails>()
                        .count { !it.selected } <= 2
                    if (canAddNewTag) {
                        addNextOpenFile()
                    }
                    update(tagDetails.name, tagDetails.icon)
                }
            }

            override fun onClose() {
                tagManager.removeTag(tagDetails.id)
            }
        }

    private fun updateTagPosition(tag: TagPanel) {
        remove(tag)
        add(tag, getNextInsertionIndex())
        revalidate()
        repaint()
    }

    private fun getLastSelectedTagIndex(): Int =
        components
            .filter { it !is SelectedFileTagPanel && it !is SelectionTagPanel }
            .filterIsInstance<TagPanel>()
            .indexOfLast { it.tagDetails.selected }

    private fun getSortedOpenFileTags(): MutableList<FileTagDetails> =
        EditorUtil.getOpenLocalFiles(project)
            .filterNot { tagManager.isFileTagExists(it) }
            .map { FileTagDetails(it) }
            .toMutableList()

    private fun addNextOpenFile() {
        getSortedOpenFileTags()
            .firstOrNull { it.virtualFile != getSelectedFile() }
            ?.let {
                tagManager.addTag(it.apply { selected = false })
            }
    }

    private fun initializeUI() {
        isOpaque = false
        border = JBUI.Borders.empty()

        add(defaultHeaderTagsPanel)
        addInitialTags()
    }

    private fun addInitialTags() {
        val selectedFile = (EditorUtil.getSelectedEditor(project) as? EditorEx)?.virtualFile
        getSortedOpenFileTags()
            .filterNot { it.virtualFile == selectedFile }
            .take(INITIAL_VISIBLE_FILES)
            .forEach {
                tagManager.addTag(it.apply { selected = false })
            }
    }

    private fun initializeEventListeners() {
        project.messageBus.connect().apply {
            subscribe(EditorNotifier.SelectionChange.TOPIC, EditorSelectionChangeListener())
            subscribe(EditorNotifier.Created.TOPIC, EditorCreatedListener())
            subscribe(EditorNotifier.Released.TOPIC, EditorReleasedListener())
            subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, FileSelectionListener())
            subscribe(
                IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC,
                IncludedFilesListener()
            )
        }
    }

    private class AddButton(onAdd: () -> Unit) : JButton() {
        init {
            addActionListener {
                onAdd()
            }

            cursor = Cursor(Cursor.HAND_CURSOR)
            preferredSize = Dimension(20, 20)
            isContentAreaFilled = false
            isOpaque = false
            border = null
            toolTipText = "Add Context"
            icon = IconUtil.scale(AllIcons.General.InlineAdd, null, 0.75f)
            rolloverIcon = IconUtil.scale(AllIcons.General.InlineAddHover, null, 0.75f)
            pressedIcon = IconUtil.scale(AllIcons.General.InlineAddHover, null, 0.75f)
        }

        override fun paintComponent(g: Graphics) {
            PaintUtil.drawRoundedBackground(g, this, true)
            super.paintComponent(g)
        }
    }

    private inner class EditorSelectionChangeListener : EditorNotifier.SelectionChange {
        override fun selectionChanged(selectionModel: SelectionModel, virtualFile: VirtualFile) {
            handleSelectionChange(selectionModel, virtualFile)
        }

        private fun handleSelectionChange(
            selectionModel: SelectionModel,
            virtualFile: VirtualFile
        ) {
            selectionTagPanel.update(virtualFile, selectionModel)
        }
    }

    private inner class EditorCreatedListener : EditorNotifier.Created {
        override fun editorCreated(editor: Editor) {
            (editor as? EditorEx)?.virtualFile?.let { editorFile ->
                if (selectedFileTagPanel.isVisible && getSelectedFile() == editorFile) {
                    return
                }

                tagManager.addTag(FileTagDetails(editorFile, false))
            }
        }
    }

    private inner class EditorReleasedListener : EditorNotifier.Released {
        override fun editorReleased(editor: Editor) {
            if (editor.editorKind == EditorKind.MAIN_EDITOR && !editor.isDisposed && (editor as? EditorEx)?.virtualFile != null) {
                tagManager.removeFileTag(editor.virtualFile)
            }
        }
    }

    private fun getSelectedFile(): VirtualFile? {
        return (selectedFileTagPanel.tagDetails as? FileTagDetails)?.virtualFile
    }

    private inner class FileSelectionListener : FileEditorManagerListener {
        override fun selectionChanged(event: FileEditorManagerEvent) {
            val currentTagDetails = (selectedFileTagPanel.tagDetails as? FileTagDetails) ?: return
            event.newFile?.let { newFile ->
                if (event.oldFile == currentTagDetails.virtualFile) {
                    tagManager.removeFileTag(newFile)
                    tagManager.addTag(currentTagDetails)
                }

                val existingTag = tagManager.getFileTag(newFile)
                runInEdt {
                    if (existingTag == null) {
                        selectedFileTagPanel.update(FileTagDetails(newFile))
                    } else {
                        selectedFileTagPanel.update(existingTag)
                    }
                    emptyText.isVisible = false
                }
            }
        }
    }

    private inner class IncludedFilesListener : IncludeFilesInContextNotifier {
        override fun filesIncluded(includedFiles: MutableList<VirtualFile>) {
            includedFiles
                .filterNot { tagManager.isFileTagExists(it) }
                .forEach {
                    tagManager.addTag(FileTagDetails(it))
                }
        }
    }
}