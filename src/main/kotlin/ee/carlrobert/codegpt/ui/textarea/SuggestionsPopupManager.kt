package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.intellij.vcsUtil.showAbove
import ee.carlrobert.codegpt.settings.persona.PersonaDetails
import ee.carlrobert.codegpt.settings.persona.PersonaSettings
import ee.carlrobert.codegpt.settings.persona.PersonasConfigurable
import java.awt.Dimension
import java.awt.Point
import java.io.File
import java.nio.file.Paths
import javax.swing.DefaultListModel
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.ScrollPaneConstants
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

enum class DefaultAction(
    val displayName: String,
    val code: String,
    val icon: Icon,
    val enabled: Boolean = true
) {
    FILES("Files →", "file:", AllIcons.FileTypes.Any_type),
    FOLDERS("Folders →", "folder:", AllIcons.Nodes.Folder),
    PERSONAS("Personas →", "persona:", AllIcons.General.User),
    DOCS("Docs (coming soon) →", "docs:", AllIcons.Toolwindows.Documentation, false),
    SEARCH_WEB("Web (coming soon)", "", AllIcons.General.Web, false),
    CREATE_NEW_PERSONA("Create new persona", "", AllIcons.General.Add),
}

sealed class SuggestionItem {
    data class FileItem(val file: File) : SuggestionItem()
    data class FolderItem(val folder: File) : SuggestionItem()
    data class ActionItem(val action: DefaultAction) : SuggestionItem()
    data class PersonaItem(val personaDetails: PersonaDetails) : SuggestionItem()
}

val DEFAULT_ACTIONS = mutableListOf(
    SuggestionItem.ActionItem(DefaultAction.FILES),
    SuggestionItem.ActionItem(DefaultAction.FOLDERS),
    SuggestionItem.ActionItem(DefaultAction.PERSONAS),
    SuggestionItem.ActionItem(DefaultAction.DOCS),
    SuggestionItem.ActionItem(DefaultAction.SEARCH_WEB),
)

class SuggestionsPopupManager(
    private val project: Project,
    private val textPane: CustomTextPane,
) {

    private var currentActionStrategy: SuggestionUpdateStrategy = DefaultSuggestionActionStrategy()
    private val appliedActions: MutableList<SuggestionItem.ActionItem> = mutableListOf()
    private var popup: JBPopup? = null
    private var originalLocation: Point? = null
    private val listModel = DefaultListModel<SuggestionItem>().apply {
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(e: ListDataEvent) = adjustPopupSize()
            override fun intervalRemoved(e: ListDataEvent) {}
            override fun contentsChanged(e: ListDataEvent) {}
        })
    }
    private val list = SuggestionList(listModel, textPane) {
        when (it) {
            is SuggestionItem.ActionItem -> handleActionSelection(it)
            is SuggestionItem.FileItem -> handleFileSelection(it.file.path)
            is SuggestionItem.FolderItem -> handleFolderSelection(it.folder.path)
            is SuggestionItem.PersonaItem -> handlePersonaSelection(it.personaDetails)
        }
    }
    private val scrollPane: JBScrollPane = JBScrollPane(list).apply {
        border = JBUI.Borders.empty()
        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
    }

    fun showPopup(component: JComponent) {
        popup = createPopup(component)
        popup?.showAbove(component)
        originalLocation = component.locationOnScreen
        reset(true)
    }

    fun hidePopup() {
        popup?.cancel()
    }

    fun isPopupVisible(): Boolean {
        return popup?.isVisible ?: false
    }

    fun requestFocus() {
        list.requestFocus()
    }

    fun selectNext() {
        list.selectNext()
    }

    fun updateSuggestions(searchText: String) {
        currentActionStrategy.updateSuggestions(project, listModel, searchText)
    }

    fun reset(clearPrevious: Boolean = true) {
        if (clearPrevious) {
            listModel.clear()
        }
        listModel.addAll(DEFAULT_ACTIONS)
        popup?.content?.revalidate()
        popup?.content?.repaint()
    }

    private fun handleActionSelection(item: SuggestionItem.ActionItem) {
        if (item.action == DefaultAction.CREATE_NEW_PERSONA) {
            hidePopup()
            service<ShowSettingsUtil>().showSettingsDialog(
                project,
                PersonasConfigurable::class.java
            )
            return
        }

        appliedActions.add(item)
        currentActionStrategy = when (item.action) {
            DefaultAction.FILES -> {
                FileSuggestionActionStrategy()
            }

            DefaultAction.FOLDERS -> {
                FolderSuggestionActionStrategy()
            }

            DefaultAction.PERSONAS -> {
                PersonaSuggestionActionStrategy()
            }

            else -> {
                DefaultSuggestionActionStrategy()
            }
        }
        currentActionStrategy.populateSuggestions(project, listModel)
        textPane.appendHighlightedText(item.action.code, withWhitespace = false)
        textPane.requestFocus()
    }

    private fun handleFileSelection(filePath: String) {
        val selectedFile = service<VirtualFileManager>().findFileByNioPath(Paths.get(filePath))
        selectedFile?.let { file ->
            textPane.appendHighlightedText(file.name, ':')
            project.service<FileSearchService>().addFileToSession(file)
        }
        hidePopup()
    }

    private fun handleFolderSelection(folderPath: String) {
        textPane.appendHighlightedText(folderPath, ':')

        val folder = service<VirtualFileManager>().findFileByNioPath(Paths.get(folderPath))
        if (folder != null) {
            VfsUtilCore.visitChildrenRecursively(folder, object : VirtualFileVisitor<Any>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    if (!file.isDirectory) {
                        // TODO
                        println("Found file: ${file.path}")
                    }
                    return true
                }
            })
        }

        hidePopup()
    }

    private fun handlePersonaSelection(personaDetails: PersonaDetails) {
        service<PersonaSettings>().state.selectedPersona.apply {
            id = personaDetails.id
            name = personaDetails.name
            instructions = personaDetails.instructions
        }
        val reservedTextRange = textPane.appendHighlightedText(personaDetails.name, ':')
        println(reservedTextRange)
        hidePopup()
    }

    private fun adjustPopupSize() {
        val maxVisibleRows = 15
        val newRowCount = minOf(listModel.size(), maxVisibleRows)
        list.setVisibleRowCount(newRowCount)
        list.revalidate()
        list.repaint()

        popup?.size = list.preferredSize

        originalLocation?.let { original ->
            val newY = original.y - list.preferredSize.height
            popup?.setLocation(Point(original.x, maxOf(newY, 0)))
        }
    }

    private fun createPopup(
        preferableFocusComponent: JComponent? = null,
    ): JBPopup =
        service<JBPopupFactory>()
            .createComponentPopupBuilder(scrollPane, preferableFocusComponent)
            .setMovable(true)
            .setCancelOnClickOutside(false)
            .setCancelOnWindowDeactivation(false)
            .setRequestFocus(true)
            .setMinSize(Dimension(480, 30))
            .setCancelCallback {
                originalLocation = null
                currentActionStrategy = DefaultSuggestionActionStrategy()
                true
            }
            .setResizable(true)
            .createPopup()
}