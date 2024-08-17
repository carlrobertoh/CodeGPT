package ee.carlrobert.codegpt.ui.textarea.suggestion

import com.intellij.ui.components.JBList
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.ui.textarea.PromptTextField
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.SuggestionItem
import ee.carlrobert.codegpt.ui.textarea.suggestion.renderer.SuggestionListCellRenderer
import java.awt.KeyboardFocusManager
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.DefaultListModel
import javax.swing.ListSelectionModel

class SuggestionList(
    listModel: DefaultListModel<SuggestionItem>,
    private val textPane: PromptTextField,
    private val onSelected: (SuggestionItem) -> Unit
) : JBList<SuggestionItem>(listModel) {

    init {
        setupUI()
        setupKeyboardFocusManager()
        setupKeyListener()
        setupMouseListener()
        setupMouseMotionListener()
    }

    fun selectNext() {
        updateSelectedIndex(if (selectedIndex < model.size - 1) selectedIndex + 1 else 0)
    }

    fun selectPrevious() {
        updateSelectedIndex(if (selectedIndex > 0) selectedIndex - 1 else model.size - 1)
    }

    private fun updateSelectedIndex(newIndex: Int) {
        selectedIndex = newIndex
        ensureIndexIsVisible(newIndex)
    }

    private fun setupUI() {
        border = JBUI.Borders.empty()
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = SuggestionListCellRenderer(textPane)
    }

    private fun setupKeyboardFocusManager() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher { e ->
            if (isTabKeyPressed(e) && isFocusOwner) {
                selectNext()
                e.consume()
                true
            } else {
                false
            }
        }
    }

    private fun isTabKeyPressed(e: KeyEvent) =
        e.keyCode == KeyEvent.VK_TAB && e.id == KeyEvent.KEY_PRESSED

    private fun setupKeyListener() {
        addKeyListener(object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_ENTER) {
                    handleEnterKey()
                    e.consume()
                }
            }
        })
    }

    private fun handleEnterKey() {
        val item = model.getElementAt(selectedIndex)
        if (item.enabled) {
            onSelected(item)
        }
    }

    private fun setupMouseListener() {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                handleMouseClick(e)
            }

            override fun mouseExited(e: MouseEvent) {
                putClientProperty("hoveredIndex", -1)
                repaint()
            }
        })
    }

    private fun handleMouseClick(e: MouseEvent) {
        val index = locationToIndex(e.point)
        if (index >= 0) {
            val item = model.getElementAt(index)
            if (item.enabled) {
                onSelected(item)
            }
            e.consume()
        }
    }

    private fun setupMouseMotionListener() {
        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                val index = locationToIndex(e.point)
                if (index != getClientProperty("hoveredIndex")) {
                    putClientProperty("hoveredIndex", index)
                    repaint()
                }
            }
        })
    }
}