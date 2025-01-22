package ee.carlrobert.codegpt.ui.textarea.header

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import com.jetbrains.rd.util.UUID
import ee.carlrobert.codegpt.EditorNotifier
import ee.carlrobert.codegpt.actions.IncludeFilesInContextNotifier
import ee.carlrobert.codegpt.ui.textarea.WrapLayout
import ee.carlrobert.codegpt.ui.textarea.suggestion.SuggestionsPopupManager
import ee.carlrobert.codegpt.util.EditorUtil
import ee.carlrobert.codegpt.util.EditorUtil.getSelectedEditorFile
import java.awt.FlowLayout
import javax.swing.JPanel
import kotlin.math.max

class UserInputHeaderPanel(
    private val project: Project,
    suggestionsPopupManager: SuggestionsPopupManager
) : JPanel(WrapLayout(FlowLayout.LEFT, 4, 4)) {

    companion object {
        private const val MAX_VISIBLE_TAGS = 3
        private const val INITIAL_VISIBLE_FILES = 2
        private const val TAG_INSERTION_OFFSET = 4
    }

    private val tags = mutableSetOf<HeaderTagDetails>()
    private val selectedFileTags = mutableSetOf<FileTagDetails>()
    private val selectedFileHeaderTag = object : SelectedFileHeaderTag(project) {
        override fun onClose() {
            this.isVisible = false
            if (tags.isEmpty()) {
                emptyText.isVisible = true
            }
        }
    }
    private val emptyText = JBLabel("No context included").apply {
        foreground = JBUI.CurrentTheme.Label.disabledForeground()
        font = JBUI.Fonts.smallFont()
        border = JBUI.Borders.emptyLeft(2)
        isVisible = project.getSelectedEditor() == null
    }
    private val selectionHeaderTag = SelectionHeaderTag(project)

    init {
        initializeUI(suggestionsPopupManager)
        initializeEventListeners()
    }

    fun getSelectedTags(): List<HeaderTagDetails> {
        val selectedTags: MutableList<HeaderTagDetails> =
            tags.filter { it.selected }.toMutableList()

        val selectedFile = selectedFileHeaderTag.virtualFile
        if (selectedFileHeaderTag.isVisible && selectedFile != null) {
            selectedTags.add(FileTagDetails(selectedFile))
        }

        selectionHeaderTag.selectedEditor?.let { editor ->
            val selectionFile = editor.virtualFile
            if (!editor.isDisposed && selectionHeaderTag.isVisible && selectionFile != null) {
                selectedTags.add(
                    runReadAction {
                        SelectionTagDetails(
                            selectionFile,
                            editor.selectionModel,
                            editor.selectionModel.selectedText
                        )
                    }
                )
            }
        }

        return selectedTags
    }

    fun addTag(tagDetails: HeaderTagDetails) {
        if (selectedFileHeaderTag.isVisible
            && tagDetails is FileTagDetails
            && selectedFileHeaderTag.virtualFile == tagDetails.virtualFile
        ) {
            return
        }

        val tag = object : HeaderTag(tagDetails, tagDetails is FileTagDetails) {
            override fun onSelect(tagDetails: HeaderTagDetails) {
                if (tagDetails is FileTagDetails) {
                    if (tagDetails.selected) {
                        project.service<FileEditorManager>().openFile(tagDetails.virtualFile)
                    } else {
                        selectedFileTags.add(tagDetails)
                    }
                }
            }

            override fun onClose() {
                removeTag(tagDetails.id)
            }

            override fun select() {
                updateTagPosition(this)
                super.select()
                if (tags.filterIsInstance<FileTagDetails>().filter { !it.selected }.size <= 2) {
                    addNextOpenFile()
                }
            }
        }

        if (tags.add(tagDetails)) {
            emptyText.isVisible = false

            if (tagDetails is FileTagDetails) {
                selectedFileTags.add(tagDetails)
            }

            val lastSelectionTagIndex = getLastSelectedTagIndex()
            if (lastSelectionTagIndex != -1) {
                add(tag, lastSelectionTagIndex + TAG_INSERTION_OFFSET + 1)
            } else {
                add(tag, TAG_INSERTION_OFFSET)
            }

            val unselectedTags = components
                .filter { it !is SelectedFileHeaderTag && it !is SelectionHeaderTag }
                .filterIsInstance<HeaderTag>()
                .filter { !it.tagDetails.selected }
            if (unselectedTags.size > 2) {
                removeTag(unselectedTags.last().tagDetails.id)
            }

            revalidate()
            repaint()
        }
    }

    private fun updateTagPosition(tag: HeaderTag) {
        remove(tag)
        val lastSelectionTagIndex = getLastSelectedTagIndex()
        if (lastSelectionTagIndex != -1) {
            add(tag, lastSelectionTagIndex + TAG_INSERTION_OFFSET + 1)
        } else {
            add(tag, max(getFirstUnselectedTagIndex(), TAG_INSERTION_OFFSET))
        }
    }

    private fun getFilteredHeaderTags(): List<HeaderTag> = components
        .filter { it !is SelectedFileHeaderTag && it !is SelectionHeaderTag }
        .filterIsInstance<HeaderTag>()

    private fun getLastSelectedTagIndex(): Int =
        getFilteredHeaderTags().indexOfLast { it.tagDetails.selected }

    private fun getFirstUnselectedTagIndex(): Int =
        getFilteredHeaderTags().indexOfFirst { !it.tagDetails.selected }

    private fun getSelectedFileTag(file: VirtualFile): FileTagDetails? =
        selectedFileTags.find { it.virtualFile == file }

    private fun getFileTag(file: VirtualFile): FileTagDetails? =
        tags.filterIsInstance<FileTagDetails>().find { it.virtualFile == file }

    private fun getSortedOpenFiles(project: Project): MutableList<FileTagDetails> =
        EditorUtil.getOpenLocalFiles(project)
            .filterNot { it == selectedFileHeaderTag.virtualFile }
            .map { FileTagDetails(it) }
            .toMutableList()

    private fun addNextOpenFile() {
        val file = EditorUtil.getOpenLocalFiles(project).firstOrNull {
            !tags.filterIsInstance<FileTagDetails>().any { tag -> tag.virtualFile == it }
                    && it != selectedFileHeaderTag.virtualFile
        } ?: return
        addTag(FileTagDetails(file, false))
    }

    private fun removeFileTag(virtualFile: VirtualFile) {
        getFileTag(virtualFile)?.let {
            removeTag(it.id)
        }
    }

    private fun removeTag(id: UUID) {
        val tagToRemove =
            tags.find { it.id == id } ?: throw IllegalArgumentException("Tag with id $id not found")
        if (tags.removeIf { it.id == tagToRemove.id }) {
            val componentToRemove = components.find { it is HeaderTag && it.id == id } ?: return
            remove(componentToRemove)

            if (tags.isEmpty() && !selectedFileHeaderTag.isVisible) {
                emptyText.isVisible = true
            }

            revalidate()
            repaint()
        }
    }

    private fun initializeUI(suggestionsPopupManager: SuggestionsPopupManager) {
        isOpaque = false
        border = JBUI.Borders.empty()

        add(AddButton {
            if (suggestionsPopupManager.isPopupVisible()) {
                suggestionsPopupManager.hidePopup()
            } else {
                suggestionsPopupManager.showPopup(this)
            }
        })
        add(emptyText)
        add(selectionHeaderTag)
        add(selectedFileHeaderTag)

        if (tags.size <= 2) {
            val selectedFile = EditorUtil.getSelectedEditor(project)?.virtualFile
            getSortedOpenFiles(project)
                .take(INITIAL_VISIBLE_FILES)
                .forEach {
                    addTag(FileTagDetails(it.virtualFile, selectedFile == it.virtualFile))
                }
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

    private inner class EditorSelectionChangeListener : EditorNotifier.SelectionChange {
        override fun selectionChanged(selectionModel: SelectionModel, virtualFile: VirtualFile) {
            handleSelectionChange(selectionModel, virtualFile)
        }

        private fun handleSelectionChange(
            selectionModel: SelectionModel,
            virtualFile: VirtualFile
        ) {
            selectionHeaderTag.update(virtualFile, selectionModel)
        }
    }

    private inner class EditorCreatedListener : EditorNotifier.Created {
        override fun editorCreated(editor: Editor) {
            editor.virtualFile?.let { editorFile ->
                if (selectedFileHeaderTag.isVisible && selectedFileHeaderTag.virtualFile == editorFile) {
                    return
                }

                addTag(FileTagDetails(editorFile, false))
            }
        }
    }

    private inner class EditorReleasedListener : EditorNotifier.Released {
        override fun editorReleased(editor: Editor) {
            removeFileTag(editor.virtualFile)

            if (tags.isEmpty() && project.getSelectedEditorFile() == null) {
                selectedFileHeaderTag.isVisible = false
                emptyText.isVisible = true
            }
        }
    }

    private inner class FileSelectionListener : FileEditorManagerListener {

        override fun selectionChanged(event: FileEditorManagerEvent) {
            val fileTags = tags.filterIsInstance<FileTagDetails>()
            event.newFile?.let { newFile ->
                if (fileTags.any { it.virtualFile == newFile }) {
                    removeFileTag(newFile)
                }
                selectedFileHeaderTag.update(newFile)
                emptyText.isVisible = false

                event.oldFile?.let { oldFile ->
                    val prevSelectedTag = getSelectedFileTag(oldFile)
                    addTag(prevSelectedTag ?: FileTagDetails(oldFile, false))
                }
            }
        }
    }

    private inner class IncludedFilesListener : IncludeFilesInContextNotifier {
        override fun filesIncluded(includedFiles: MutableList<VirtualFile>) {
            val selectedEditorFile = getSelectedEditorFile(project)
            includedFiles.forEach {
                if (getFileTag(it) == null && selectedEditorFile != it) {
                    addTag(FileTagDetails(it))
                }
            }
        }
    }
}