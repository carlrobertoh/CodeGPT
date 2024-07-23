package ee.carlrobert.codegpt.settings.persona

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.DialogPanel
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
import javax.swing.table.DefaultTableModel

class NonEditableTableModel(columnNames: Array<String>, rowCount: Int) :
    DefaultTableModel(columnNames, rowCount) {
    override fun isCellEditable(row: Int, column: Int): Boolean {
        return false
    }
}

class PersonasSettingsForm {

    private val tableModel =
        NonEditableTableModel(arrayOf("Id", "Persona", "Instructions", "FromResource"), 0)
    private val table = JBTable(tableModel).apply {
        columnModel.getColumn(0).apply {
            minWidth = 0
            maxWidth = 0
            preferredWidth = 0
            resizable = false
        }
        columnModel.getColumn(1).preferredWidth = 60
        columnModel.getColumn(2).preferredWidth = 240
        columnModel.getColumn(3).apply {
            minWidth = 0
            maxWidth = 0
            preferredWidth = 0
            resizable = false
        }
        selectionModel.addListSelectionListener { populateEditArea() }
    }
    private val personaField = JBTextField()
    private val promptTextArea = JBTextArea().apply {
        lineWrap = true
        wrapStyleWord = true
        font = UIManager.getFont("TextField.font")
        border = JBUI.Borders.empty(3, 6)
    }
    private val addedItems = mutableListOf<PersonaDetails>()

    init {
        val selectedPersonId = service<PersonaSettings>().state.selectedPersona.id

        val userPersonas: List<PersonaDetails> =
            service<PersonaSettings>().state.userCreatedPersonas.map {
                PersonaDetails(it.id, it.name!!, it.description!!)
            }

        (ResourceUtil.getPrompts() + userPersonas).forEachIndexed { index, (id, act, prompt) ->
            tableModel.addRow(arrayOf(id, act, prompt, true))
            if (selectedPersonId == id) {
                table.setRowSelectionInterval(index, index)
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
                cell(personaField)
                    .label("Persona:", LabelPosition.TOP)
                    .align(Align.FILL)
            }
            row {
                cell(JBScrollPane(promptTextArea).apply {
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
            personaField.text = tableModel.getValueAt(selectedRow, 1) as String
            personaField.isEnabled = userCreatedResource
            promptTextArea.text = tableModel.getValueAt(selectedRow, 2) as String
            promptTextArea.isEnabled = userCreatedResource
        } else {
            personaField.text = ""
            promptTextArea.text = ""
        }
    }

    private fun handleAddItem() {
        var maxId = 0L
        for (i in 0 until table.rowCount) {
            maxId = maxId.coerceAtLeast(tableModel.getValueAt(i, 0) as Long)
        }
        val personaDetails = PersonaDetails(maxId + 1, "New Persona", "New Prompt")
        addedItems.add(personaDetails)
        tableModel.addRow(
            arrayOf(
                personaDetails.id,
                personaDetails.name,
                personaDetails.description,
                false
            )
        )
        val lastRow = table.rowCount - 1
        table.setRowSelectionInterval(lastRow, lastRow)
        populateEditArea()
        scrollToLastRow()
    }

    private fun handleDuplicateItem() {
        var maxId = 0L
        for (i in 0 until table.rowCount) {
            maxId = maxId.coerceAtLeast(tableModel.getValueAt(i, 0) as Long)
        }
        val name = tableModel.getValueAt(table.selectedRow, 1) as String + " Copy"
        val prompt = tableModel.getValueAt(table.selectedRow, 2) as String
        val personaDetails = PersonaDetails(maxId + 1, name, prompt)
        addedItems.add(personaDetails)
        tableModel.addRow(
            arrayOf(
                personaDetails.id,
                personaDetails.name,
                personaDetails.description,
                false
            )
        )

        val lastRow = table.rowCount - 1
        table.setRowSelectionInterval(lastRow, lastRow)
        populateEditArea()
        scrollToLastRow()
    }

    private fun handleRemoveItem() {
        val selectedRow = table.selectedRow
        if (selectedRow != -1 && !(tableModel.getValueAt(selectedRow, 3) as Boolean)) {
            tableModel.removeRow(selectedRow)
            addedItems.filter { it.id != tableModel.getValueAt(selectedRow, 0) as Long }
            populateEditArea()
        }
    }

    private fun saveChanges() {
        val selectedRow = table.selectedRow
        if (selectedRow != -1) {
            val editedAct = personaField.text
            val editedPrompt = promptTextArea.text
            tableModel.setValueAt(editedAct, selectedRow, 0)
            tableModel.setValueAt(editedPrompt, selectedRow, 1)
        }
    }

    fun isModified(): Boolean {
        if (table.selectedRow == -1) {
            return false
        }

        return service<PersonaSettings>().state.selectedPersona.let {
            it.id != tableModel.getValueAt(table.selectedRow, 0)
                    || it.name != personaField.text
                    || it.description != promptTextArea.text
        }
    }

    fun applyChanges() {
        if (table.selectedRow == -1) {
            return
        }

        val personaDetails = PersonaDetailsState().apply {
            id = tableModel.getValueAt(table.selectedRow, 0) as Long
            name = personaField.text
            description = promptTextArea.text
        }

        service<PersonaSettings>().state.apply {
            selectedPersona
            selectedPersona.apply {
                id = personaDetails.id
                name = personaDetails.name
                description = personaDetails.description
            }
            userCreatedPersonas.add(personaDetails)
        }
    }

    fun resetChanges() {

    }

    private fun scrollToLastRow() {
        val lastRow = table.rowCount - 1
        table.scrollRectToVisible(table.getCellRect(lastRow, 0, true))
    }
}