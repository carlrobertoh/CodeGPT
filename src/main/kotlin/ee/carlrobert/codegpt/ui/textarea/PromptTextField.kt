package ee.carlrobert.codegpt.ui.textarea

import com.intellij.ide.IdeEventQueue
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.Project
import com.intellij.ui.ComponentUtil.findParentByCondition
import com.intellij.ui.EditorTextField
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.ui.textarea.suggestion.SuggestionsPopupManager
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.SuggestionActionItem
import ee.carlrobert.codegpt.ui.textarea.suggestion.item.SuggestionItem
import java.awt.AWTEvent
import java.awt.Dimension
import java.awt.KeyboardFocusManager
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.*

data class AppliedActionInlay(
    val suggestion: SuggestionItem,
    val inlay: Inlay<PromptTextFieldInlayRenderer?>,
)

const val AT_CHAR = '@'

class PromptTextField(
    project: Project,
    onTextChanged: (String) -> Unit,
    private val onSubmit: (String, List<AppliedActionInlay>) -> Unit
) : EditorTextField(project, FileTypes.PLAIN_TEXT), Disposable {

    val dispatcherId: UUID = UUID.randomUUID()

    private val appliedInlays: MutableList<AppliedActionInlay> = mutableListOf()
    private val suggestionsPopupManager = SuggestionsPopupManager(project, this)

    init {
        isOneLineMode = false
        isOpaque = false
        minimumSize = Dimension(100, 40)
        document.addDocumentListener(getDocumentListener(onTextChanged))
        setPlaceholder(CodeGPTBundle.get("toolwindow.chat.textArea.emptyText"))
        IdeEventQueue.getInstance().addDispatcher(
            PromptTextFieldEventDispatcher(this, suggestionsPopupManager, appliedInlays) {
                onSubmit(text, appliedInlays)
                clear()
            },
            this
        )
    }

    fun addInlayElement(actionPrefix: String, text: String?, actionItem: SuggestionActionItem) {
        editor?.let {
            val startOffset = it.document.text.lastIndexOf(AT_CHAR)
            if (startOffset == -1) {
                throw IllegalStateException("No '@' symbol found in the text")
            }

            runUndoTransparentWriteAction {
                it.document.deleteString(startOffset, it.document.textLength)
                it.document.setText(it.document.text + " ")
                appliedInlays.add(
                    AppliedActionInlay(
                        actionItem,
                        it.inlayModel.addInlineElement(
                            startOffset,
                            true,
                            PromptTextFieldInlayRenderer(actionPrefix, text) { inlay ->
                                inlay.dispose()
                            })!!,
                    )
                )
                it.caretModel.moveToOffset(it.document.textLength)
            }
        }
    }

    override fun createEditor(): EditorEx {
        val editorEx = super.createEditor()
        editorEx.settings.isUseSoftWraps = true
        return editorEx
    }

    override fun updateBorder(editor: EditorEx) {
        editor.setBorder(JBUI.Borders.empty(4, 8))
    }

    override fun dispose() {
        clear()
    }

    private fun clear() {
        runInEdt { text = "" }
        clearInlays()
    }

    private fun clearInlays() {
        runUndoTransparentWriteAction {
            appliedInlays.forEach { it.inlay.dispose() }
            appliedInlays.clear()
        }
    }

    private fun getDocumentListener(onTextChanged: (String) -> Unit): DocumentListener {
        return object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                onTextChanged(event.document.text)

                if (event.document.text.isEmpty()) {
                    suggestionsPopupManager.hidePopup()
                    clearInlays()
                    return
                }
            }
        }
    }
}

class PromptTextFieldEventDispatcher(
    private val textField: PromptTextField,
    private val suggestionsPopupManager: SuggestionsPopupManager,
    private val appliedInlays: MutableList<AppliedActionInlay>,
    private val onSubmit: () -> Unit
) : IdeEventQueue.EventDispatcher {

    override fun dispatch(e: AWTEvent): Boolean {
        val owner =
            findParentByCondition(KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner) { component ->
                component is PromptTextField
            }

        if ((e is KeyEvent || e is MouseEvent)
            && owner is PromptTextField
            && owner.dispatcherId == textField.dispatcherId
        ) {
            if (e is KeyEvent) {
                if (e.id == KeyEvent.KEY_PRESSED) {
                    when (e.keyCode) {
                        KeyEvent.VK_BACK_SPACE -> {
                            if (textField.text.let { it.isNotEmpty() && it.last() == AT_CHAR }) {
                                suggestionsPopupManager.reset()
                            }

                            val appliedInlay = appliedInlays.find {
                                it.inlay.offset == owner.caretModel.offset - 1
                            }
                            if (appliedInlay != null) {
                                appliedInlay.inlay.dispose()
                                appliedInlays.remove(appliedInlay)
                            }
                        }

                        KeyEvent.VK_TAB -> {
                            selectNextSuggestion(e)
                        }

                        KeyEvent.VK_ENTER -> {
                            if (e.modifiersEx and InputEvent.SHIFT_DOWN_MASK == 0
                                && e.modifiersEx and InputEvent.ALT_DOWN_MASK == 0
                                && e.modifiersEx and InputEvent.CTRL_DOWN_MASK == 0
                            ) {
                                onSubmit()
                                e.consume()
                            }
                        }

                        KeyEvent.VK_UP -> selectPreviousSuggestion(e)
                        KeyEvent.VK_DOWN -> selectNextSuggestion(e)
                    }
                    when (e.keyChar) {
                        AT_CHAR -> showPopup(e)
                        else -> {
                            if (suggestionsPopupManager.isPopupVisible()) {
                                updateSuggestions()
                            }
                        }
                    }
                }
                return e.isConsumed
            }
        }
        return false
    }

    private fun selectNextSuggestion(event: KeyEvent) {
        if (suggestionsPopupManager.isPopupVisible()) {
            suggestionsPopupManager.selectNext()
            event.consume()
        }
    }

    private fun selectPreviousSuggestion(event: KeyEvent) {
        if (suggestionsPopupManager.isPopupVisible()) {
            suggestionsPopupManager.selectPrevious()
            event.consume()
        }
    }

    private fun showPopup(event: KeyEvent) {
        suggestionsPopupManager.showPopup()
        event.consume()
    }

    private fun updateSuggestions() {
        val lastAtIndex = textField.text.lastIndexOf(AT_CHAR)
        if (lastAtIndex != -1) {
            val searchText = textField.text.substring(lastAtIndex + 1)
            if (searchText.isNotEmpty()) {
                suggestionsPopupManager.updateSuggestions(searchText)
            }
        } else {
            suggestionsPopupManager.hidePopup()
        }
    }
}
