package ee.carlrobert.codegpt.ui.textarea.suggestion

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupListener
import com.intellij.openapi.ui.popup.LightweightWindowEvent
import com.intellij.ui.awt.RelativePoint
import ee.carlrobert.codegpt.ui.textarea.UserInputPanel
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
    private val userInputPanel: UserInputPanel,
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
    private val list = SuggestionList(listModel, userInputPanel) {
        handleSuggestionItemSelection(it)
    }
    private val defaultActions: MutableList<SuggestionItem> = mutableListOf(
        FileSuggestionGroupItem(project),
        FolderSuggestionGroupItem(project),
        GitSuggestionGroupItem(project),
        PersonaSuggestionGroupItem(project),
        DocumentationSuggestionGroupItem(project),
        WebSearchActionItem(project),
    )

    fun showPopup(component: JComponent? = null) {
        popup = SuggestionsPopupBuilder()
            .setPreferableFocusComponent(component)
            .setOnCancel {
                originalLocation = null
                true
            }
            .build(list)
        popup?.showAbove(userInputPanel)
        originalLocation = userInputPanel.locationOnScreen
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
        list.selectNext()
        list.requestFocus()
    }

    fun selectPrevious() {
        list.selectPrevious()
        list.requestFocus()
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
                        item.execute(project, userInputPanel)
                    }
                }
            }

            is SuggestionGroupItem -> {
                selectedActionGroup = item
                updateSuggestions()
                userInputPanel.requestFocus()
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

fun JBPopup.showAbove(component: JComponent) {
    val northWest = RelativePoint(component, Point())

    addListener(object : JBPopupListener {
        override fun beforeShown(event: LightweightWindowEvent) {
            val location = Point(locationOnScreen).apply {
                y = northWest.screenPoint.y - size.height
            }

            setLocation(location)
            removeListener(this)
        }
    })
    show(northWest)
}
