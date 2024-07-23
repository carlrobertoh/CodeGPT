package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
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
import javax.swing.table.DefaultTableModel

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
    private val list = SuggestionList(listModel) {
        handleSelection(it)
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
        list.revalidate()
        list.repaint()
        scrollPane.revalidate()
        scrollPane.repaint()
        popup?.content?.revalidate()
        popup?.content?.repaint()
    }

    private fun handleSelection(item: SuggestionItem) {
        when (item) {
            is SuggestionItem.ActionItem -> {
                if (item.action == DefaultAction.CREATE_NEW_PERSONA) {
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
                val reservedTextRange =
                    textPane.appendHighlightedText(item.action.code, withSpace = false)
                println(reservedTextRange)
                textPane.requestFocus()
            }

            is SuggestionItem.FileItem -> handleFileSelection(item.file.path)
            is SuggestionItem.FolderItem -> handleFolderSelection(item.folder.path)
            is SuggestionItem.PersonaItem -> handlePersonaSelection(item.personaDetails)
        }
    }

    private fun handleFileSelection(filePath: String) {
        val selectedFile = service<VirtualFileManager>().findFileByNioPath(Paths.get(filePath))
        selectedFile?.let { file ->
            val reservedTextRange = textPane.appendHighlightedText(file.name, ':')
            println(reservedTextRange)
            project.service<FileSearchService>().addFileToSession(file)
        }
        hidePopup()
    }

    private fun handleFolderSelection(folderPath: String) {
        // TODO
        val reservedTextRange = textPane.appendHighlightedText(folderPath, ':')
        println(reservedTextRange)
        hidePopup()
    }

    private fun handlePersonaSelection(personaDetails: PersonaDetails) {
        service<PersonaSettings>().state.selectedPersona.apply {
            id = personaDetails.id
            name = personaDetails.name
            description = personaDetails.description
        }
        val reservedTextRange = textPane.appendHighlightedText(personaDetails.name, ':')
        println(reservedTextRange)
        hidePopup()
    }

    private fun adjustPopupSize() {
        val maxVisibleRows = 15 // or any other number you prefer
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
            .setShowShadow(true)
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

class CreatePersonaPopup(private val existingPrompts: List<PersonaDetails>) {

    private val tableModel = object : DefaultTableModel(arrayOf("Persona", "Instructions"), 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = true
    }.apply {
        existingPrompts.forEach { addRow(arrayOf(it.name, it.description)) }
    }

    private lateinit var table: JBTable
    private lateinit var editActField: Cell<JBTextField>
    private lateinit var editPromptArea: Cell<JBScrollPane>

    fun createPanel(): DialogPanel {
        return panel {
            row {
                table = JBTable(tableModel).apply {
                    setShowGrid(true)
                    columnModel.getColumn(0).preferredWidth = 100
                    columnModel.getColumn(1).preferredWidth = 400
                    selectionModel.addListSelectionListener { updateEditArea() }
                }

                val toolbarDecorator = ToolbarDecorator.createDecorator(table)
                    .setAddAction { addNewPrompt() }
                    .setRemoveAction { removeSelectedPrompt() }
                    .disableUpDownActions()

                cell(toolbarDecorator.createPanel())
                    .align(Align.FILL)
                    .resizableColumn()
                    .applyToComponent {
                        preferredSize = Dimension(650, 300)
                    }
            }

            row {
                editActField = cell(JBTextField())
                    .label("Persona:", LabelPosition.TOP)
                    .align(Align.FILL)
                    .resizableColumn()
            }

            row {
                val promptTextArea = JBTextArea().apply {
                    lineWrap = true
                    wrapStyleWord = true
                }
                val promptScrollPane = JBScrollPane(promptTextArea).apply {
                    preferredSize = Dimension(650, 300)
                }

                editPromptArea = cell(promptScrollPane)
                    .label("Instructions:", LabelPosition.TOP)
                    .align(Align.FILL)
                    .resizableColumn()
            }

            row {
                button("Save Changes") { saveChanges() }
                button("Cancel") { cancel() }
            }
        }
    }

    private fun updateEditArea() {
        val selectedRow = table.selectedRow
        if (selectedRow != -1) {
            val act = tableModel.getValueAt(selectedRow, 0) as String
            val prompt = tableModel.getValueAt(selectedRow, 1) as String
            editActField.component.text = act
            (editPromptArea.component.viewport.view as JBTextArea).text = prompt
        } else {
            editActField.component.text = ""
            (editPromptArea.component.viewport.view as JBTextArea).text = ""
        }
    }

    private fun addNewPrompt() {
        tableModel.addRow(arrayOf("New Act", "New Prompt"))
        table.selectLastRow()
        updateEditArea()
    }

    private fun removeSelectedPrompt() {
        val selectedRow = table.selectedRow
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow)
            updateEditArea()
        }
    }

    private fun saveChanges() {
        val selectedRow = table.selectedRow
        if (selectedRow != -1) {
            val editedAct = editActField.component.text
            val editedPrompt = (editPromptArea.component.viewport.view as JBTextArea).text
            tableModel.setValueAt(editedAct, selectedRow, 0)
            tableModel.setValueAt(editedPrompt, selectedRow, 1)
        }
        // Implement additional save logic here
    }

    private fun cancel() {
        // Implement cancel logic here
    }
}

// Extension function to select the last row of a JBTable
fun JBTable.selectLastRow() {
    val lastRow = rowCount - 1
    setRowSelectionInterval(lastRow, lastRow)
}