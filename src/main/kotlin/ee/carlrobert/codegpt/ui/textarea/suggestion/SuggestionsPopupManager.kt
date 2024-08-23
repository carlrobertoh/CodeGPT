package ee.carlrobert.codegpt.ui.textarea.suggestion

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.vcsUtil.showAbove
import ee.carlrobert.codegpt.ui.textarea.PromptTextField
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.*
import kotlinx.coroutines.*
import java.awt.Dimension
import java.awt.Point
import javax.swing.DefaultListModel
import javax.swing.JComponent
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener

class SuggestionsPopupManager(
    private val project: Project,
    private val textField: PromptTextField,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var selectedActionGroup: SuggestionGroupItem? = null
    private var popup: JBPopup? = null
    private var originalLocation: Point? = null
    private val listModel = DefaultListModel<SuggestionItem>().apply {
        addListDataListener(object : ListDataListener {
            override fun intervalAdded(e: ListDataEvent) = adjustPopupSize()
            override fun intervalRemoved(e: ListDataEvent) {}
            override fun contentsChanged(e: ListDataEvent) {}
        })
    }
    private val list = SuggestionList(listModel, textField) {
        handleSuggestionItemSelection(it)
    }
    private val defaultActions: MutableList<SuggestionItem> = mutableListOf(
        FileSuggestionGroupItem(project),
        FolderSuggestionGroupItem(project),
        PersonaSuggestionGroupItem(),
        DocumentationSuggestionGroupItem(),
        WebSearchActionItem(),
    )

    fun showPopup(component: JComponent? = null) {
        popup = SuggestionsPopupBuilder()
            .setPreferableFocusComponent(component)
            .setOnCancel {
                originalLocation = null
                true
            }
            .build(list)
        popup?.showAbove(textField)
        originalLocation = textField.locationOnScreen
        reset(true)
        selectNext()
    }

    fun hidePopup() {
        popup?.cancel()
    }

    fun isPopupVisible(): Boolean {
        return popup?.isVisible ?: false
    }

    fun selectNext() {
        list.requestFocus()
        list.selectNext()
    }

    fun selectPrevious() {
        list.requestFocus()
        list.selectPrevious()
    }

    fun updateSuggestions(searchText: String? = null) {
        scope.launch {
            val suggestions = withContext(Dispatchers.Default) {
                selectedActionGroup?.getSuggestions(searchText) ?: emptyList()
            }

            withContext(Dispatchers.Main) {
                listModel.clear()
                listModel.addAll(suggestions)
                list.revalidate()
                list.repaint()
            }
        }
    }

    fun reset(clearPrevious: Boolean = true) {
        if (clearPrevious) {
            listModel.clear()
        }
        listModel.addAll(defaultActions)
    }

    private fun handleSuggestionItemSelection(item: SuggestionItem) {
        when (item) {
            is SuggestionActionItem -> {
                hidePopup()
                scope.launch {
                    withContext(Dispatchers.Main) {
                        item.execute(project, textField)
                    }
                }
            }

            is SuggestionGroupItem -> {
                selectedActionGroup = item
                updateSuggestions()
                textField.requestFocus()
            }
        }
    }

    private fun adjustPopupSize() {
        val maxVisibleRows = 15
        val newRowCount = minOf(listModel.size(), maxVisibleRows)
        list.setVisibleRowCount(newRowCount)
        list.revalidate()
        list.repaint()

        popup?.size = Dimension(list.preferredSize.width, list.preferredSize.height + 32)
        originalLocation?.let { original ->
            val newY = original.y - list.preferredSize.height - 32
            popup?.setLocation(Point(original.x, maxOf(newY, 0)))
        }
    }
}
