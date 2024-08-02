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
    private val addedItems = mutableListOf<PersonaDetails>()
    private val removedItemIds = mutableListOf<Long>()

    init {
        setupForm()
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

    fun applyChanges() {
        val persona = getSelectedPersona()
        service<PersonaSettings>().state.run {
            if (persona != null) {
                selectedPersona = persona.toPersonaDetailsState()
            }

            userCreatedPersonas.removeIf { removedItemIds.contains(it.id) }
            userCreatedPersonas.forEach {
                if (it.id == persona?.id) {
                    it.name = persona.name
                    it.instructions = persona.instructions
                }
            }
            userCreatedPersonas.addAll(findMatchingRows(addedItems.map { it.id }).map { it.toPersonaDetailsState() })
        }
        clear()
    }

    fun isModified(): Boolean {
        service<PersonaSettings>().state.let {
            val (id, name, description) = getSelectedPersona() ?: return false
            return it.selectedPersona.id != id
                    || it.selectedPersona.name != name
                    || it.selectedPersona.instructions != description
                    || removedItemIds.size > 0
                    || addedItems.size > 0
        }
    }

    fun resetChanges() {
        clear()
        tableModel.rowCount = 0
        setupForm()
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
        return PersonaDetails(findMaxId() + 1, name, prompt)
    }

    private fun addPersonaToTable(persona: PersonaDetails) {
        addedItems.add(persona)
        tableModel.addRow(arrayOf(persona.id, persona.name, persona.instructions, false))
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
            val id = tableModel.getValueAt(selectedRow, 0) as Long
            if (addedItems.none { it.id == id }) {
                removedItemIds.add(id)
            }
            addedItems.filter { it.id != id }
            tableModel.removeRow(selectedRow)
            populateEditArea()

            val newSelectedRow = if (selectedRow > 0) selectedRow - 1 else 0
            if (table.rowCount > 0) {
                table.setRowSelectionInterval(newSelectedRow, newSelectedRow)
            }
        }
    }

    private fun setupForm() {
        service<PersonaSettings>().state.run {
            userCreatedPersonas.forEachIndexed { index, persona ->
                tableModel.addPersonaRow(
                    PersonaDetails(
                        persona.id,
                        persona.name!!,
                        persona.instructions!!
                    ),
                    selectedPersona.id,
                    index
                )
            }
            ResourceUtil.getDefaultPersonas().forEachIndexed { index, persona ->
                tableModel.addPersonaRow(persona, selectedPersona.id, index, true)
            }
        }
    }

    private fun DefaultTableModel.addPersonaRow(
        persona: PersonaDetails,
        selectedPersonaId: Long,
        rowIndex: Int,
        fromResource: Boolean = false
    ) {
        val (id, name, instructions) = persona
        addRow(arrayOf(id, name, instructions, fromResource))
        if (selectedPersonaId == id) {
            table.setRowSelectionInterval(rowIndex, rowIndex)
        }
    }

    private fun scrollToLastRow() {
        table.scrollRectToVisible(table.getCellRect(table.rowCount - 1, 0, true))
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

    private fun JBTable.getSelectedPersona(): PersonaDetails? {
        if (selectedRow == -1) {
            return null
        }

        return PersonaDetails(
            tableModel.getValueAt(selectedRow, 0) as Long,
            tableModel.getValueAt(selectedRow, 1) as String,
            tableModel.getValueAt(selectedRow, 2) as String
        )
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

    private fun getSelectedPersona(): PersonaDetails? {
        return table.getSelectedPersona()
    }

    private fun clear() {
        addedItems.clear()
        removedItemIds.clear()
    }

    private fun findMatchingRows(ids: List<Long>): List<PersonaDetails> {
        val matchingRows = mutableListOf<PersonaDetails>()

        for (rowIndex in 0 until tableModel.rowCount) {
            val personaId = tableModel.getValueAt(rowIndex, 0) as Long
            if (ids.contains(personaId)) {
                val name = tableModel.getValueAt(rowIndex, 1) as String
                val instructions = tableModel.getValueAt(rowIndex, 2) as String
                matchingRows.add(PersonaDetails(personaId, name, instructions))
            }
        }

        return matchingRows
    }
}