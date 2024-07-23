package ee.carlrobert.codegpt.settings.persona

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.util.ResourceUtil
import java.awt.Dimension
import javax.swing.UIManager
import javax.swing.event.DocumentEvent
import javax.swing.table.DefaultTableModel
import javax.swing.text.JTextComponent

class NonEditableTableModel(columnNames: Array<String>, rowCount: Int) :
    DefaultTableModel(columnNames, rowCount) {
    override fun isCellEditable(row: Int, column: Int) = false
}

class PersonasSettingsForm {

    private val tableModel =
        NonEditableTableModel(arrayOf("Id", "Name", "Instructions", "FromResource"), 0)
    private val table = JBTable(tableModel).apply {
        setupTableColumns()
        selectionModel.addListSelectionListener { populateEditArea() }
    }
    private val nameField = JBTextField().apply {
        addTextChangeListener { newText ->
            updateTableModelIfRowSelected(1, newText)
        }
    }
    private val instructionsTextArea = JBTextArea().apply {
        lineWrap = true
        wrapStyleWord = true
        font = UIManager.getFont("TextField.font")
        border = JBUI.Borders.empty(3, 6)
        addTextChangeListener { newText ->
            updateTableModelIfRowSelected(2, newText)
        }
    }
    private var initialItems = mutableListOf<PersonaDetails>()
    private val addedItems = mutableListOf<PersonaDetails>()

    init {
        setupForm()
    }

    private fun setupForm() {
        service<PersonaSettings>().state.let {
            val userPersonas = it.userCreatedPersonas.map { persona ->
                PersonaDetails(persona.id, persona.name!!, persona.description!!)
            }

            initialItems = (ResourceUtil.getPrompts() + userPersonas).toMutableList()
            initialItems.forEachIndexed { index, (id, act, prompt) ->
                tableModel.addRow(arrayOf(id, act, prompt, true))
                if (it.selectedPersona.id == id) {
                    table.setRowSelectionInterval(index, index)
                }
            }
        }
    }

    fun createPanel(): DialogPanel {
        return panel {
            row {
                val toolbarDecorator = ToolbarDecorator.createDecorator(table)
                    .setAddAction { handleAddItem() }
                    .setRemoveAction { handleRemoveItem() }
                    .addExtraAction(object :
                        AnAction("Duplicate", "Duplicate persona", AllIcons.Actions.Copy) {
                        override fun actionPerformed(e: AnActionEvent) {
                            handleDuplicateItem()
                        }
                    })
                    .setRemoveActionUpdater {
                        val selectedRow = table.selectedRow
                        selectedRow != -1 && !(tableModel.getValueAt(selectedRow, 3) as Boolean)
                    }
                    .disableUpDownActions()

                cell(toolbarDecorator.createPanel())
                    .align(Align.FILL)
                    .resizableColumn()
                    .applyToComponent {
                        preferredSize = Dimension(650, 250)
                    }
            }
            row {
                cell(nameField)
                    .label("Name:", LabelPosition.TOP)
                    .align(Align.FILL)
            }
            row {
                cell(JBScrollPane(instructionsTextArea).apply {
                    preferredSize = Dimension(650, 225)
                })
                    .label("Instructions:", LabelPosition.TOP)
                    .align(Align.FILL)
                    .resizableColumn()
            }
        }
    }

    private fun populateEditArea() {
        val selectedRow = table.selectedRow
        if (selectedRow != -1) {
            val userCreatedResource = !(tableModel.getValueAt(selectedRow, 3) as Boolean)
            nameField.text = tableModel.getValueAt(selectedRow, 1) as String
            nameField.isEnabled = userCreatedResource
            instructionsTextArea.text = tableModel.getValueAt(selectedRow, 2) as String
            instructionsTextArea.isEnabled = userCreatedResource
        } else {
            nameField.text = ""
            instructionsTextArea.text = ""
        }
    }

    private fun handleAddItem() {
        addPersonaToTable(createNewPersona("New Persona", "New Prompt"))
    }

    private fun handleDuplicateItem() {
        val selectedRow = table.selectedRow
        val originalName = tableModel.getValueAt(selectedRow, 1) as String
        val originalPrompt = tableModel.getValueAt(selectedRow, 2) as String
        addPersonaToTable(createNewPersona("$originalName Copy", originalPrompt))
    }

    private fun createNewPersona(name: String, prompt: String): PersonaDetails {
        val newId = findMaxId() + 1
        return PersonaDetails(newId, name, prompt)
    }

    private fun addPersonaToTable(persona: PersonaDetails) {
        addedItems.add(persona)
        tableModel.addRow(arrayOf(persona.id, persona.name, persona.description, false))
        selectLastRowAndUpdateUI()
    }

    private fun findMaxId(): Long {
        return (0 until table.rowCount).maxOf {
            tableModel.getValueAt(it, 0) as Long
        }
    }

    private fun selectLastRowAndUpdateUI() {
        val lastRow = table.rowCount - 1
        table.setRowSelectionInterval(lastRow, lastRow)
        populateEditArea()
        scrollToLastRow()
        nameField.requestFocus()
    }

    private fun handleRemoveItem() {
        val selectedRow = table.selectedRow
        if (selectedRow != -1 && !(tableModel.getValueAt(selectedRow, 3) as Boolean)) {
            addedItems.filter { it.id != tableModel.getValueAt(selectedRow, 0) as Long }
            tableModel.removeRow(selectedRow)
            populateEditArea()

            val newSelectedRow = if (selectedRow > 0) selectedRow - 1 else 0
            if (table.rowCount > 0) {
                table.setRowSelectionInterval(newSelectedRow, newSelectedRow)
            }
        }
    }

    fun isModified(): Boolean {
        if (table.selectedRow == -1) {
            return false
        }

        return service<PersonaSettings>().state.selectedPersona.let {
            it.id != tableModel.getValueAt(table.selectedRow, 0)
                    || it.name != nameField.text
                    || it.description != instructionsTextArea.text
        }
    }

    fun applyChanges() {
        if (table.selectedRow == -1) {
            return
        }

        val personaDetails = PersonaDetailsState().apply {
            id = tableModel.getValueAt(table.selectedRow, 0) as Long
            name = nameField.text
            description = instructionsTextArea.text
        }

        service<PersonaSettings>().state.apply {
            selectedPersona
            selectedPersona.apply {
                id = personaDetails.id
                name = personaDetails.name
                description = personaDetails.description
            }
            userCreatedPersonas.add(personaDetails)
            val userPersonas = service<PersonaSettings>().state.userCreatedPersonas.map {
                PersonaDetails(it.id, it.name!!, it.description!!)
            }

            initialItems = (ResourceUtil.getPrompts() + userPersonas).toMutableList()
            addedItems.clear()
        }
    }

    fun resetChanges() {
        addedItems.clear()
        tableModel.rowCount = 0
        setupForm()
    }

    private fun compareWithInitialState(): Boolean {
        if (tableModel.rowCount != initialItems.size) return true

        for (i in 0 until tableModel.rowCount) {
            val currentId = tableModel.getValueAt(i, 0) as Long
            val currentName = tableModel.getValueAt(i, 1) as String
            val currentInstructions = tableModel.getValueAt(i, 2) as String
            val currentFromResource = tableModel.getValueAt(i, 3) as Boolean

            val initialItem = initialItems.find { it.id == currentId } ?: return true
            if (initialItem.name != currentName || initialItem.description != currentInstructions || currentFromResource != true) {
                return true
            }
        }

        return false
    }

    private fun scrollToLastRow() {
        val lastRow = table.rowCount - 1
        table.scrollRectToVisible(table.getCellRect(lastRow, 0, true))
    }

    private fun JBTable.setupTableColumns() {
        columnModel.apply {
            getColumn(0).apply {
                minWidth = 0
                maxWidth = 0
                preferredWidth = 0
                resizable = false
            }
            getColumn(1).preferredWidth = 60
            getColumn(2).preferredWidth = 240
            getColumn(3).apply {
                minWidth = 0
                maxWidth = 0
                preferredWidth = 0
                resizable = false
            }
        }
    }

    private fun JTextComponent.addTextChangeListener(listener: (String) -> Unit) {
        document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                listener(e.document.getText(0, e.document.length))
            }
        })
    }

    private fun updateTableModelIfRowSelected(column: Int, newValue: Any) {
        if (table.selectedRow != -1) {
            tableModel.setValueAt(newValue, table.selectedRow, column)
        }
    }
}