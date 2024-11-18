package ee.carlrobert.codegpt.ui.textarea

import com.intellij.ide.IdeEventQueue
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.application.runUndoTransparentWriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.ComponentUtil.findParentByCondition
import com.intellij.ui.EditorTextField
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.CodeGPTKeys.IS_PROMPT_TEXT_FIELD_DOCUMENT
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

interface AppliedActionInlay {
    val inlay: Inlay<PromptTextFieldInlayRenderer?>
}

data class AppliedSuggestionActionInlay(
    override val inlay: Inlay<PromptTextFieldInlayRenderer?>,
    val suggestion: SuggestionItem?,
) : AppliedActionInlay

data class AppliedCodeActionInlay(
    override val inlay: Inlay<PromptTextFieldInlayRenderer?>,
    val code: String,
    val editorFile: VirtualFile
) : AppliedActionInlay

const val AT_CHAR = '@'

class PromptTextField(
    private val project: Project,
    private val onTextChanged: (String) -> Unit,
    private val onSubmit: (String, List<AppliedActionInlay>) -> Unit
) : EditorTextField(project, FileTypes.PLAIN_TEXT), Disposable {

    val dispatcherId: UUID = UUID.randomUUID()

    private val appliedInlays: MutableList<AppliedActionInlay> = mutableListOf()
    private val suggestionsPopupManager = SuggestionsPopupManager(project, this)

    init {
        isOneLineMode = false
        background = UIUtil.getTextFieldBackground()
        IS_PROMPT_TEXT_FIELD_DOCUMENT.set(document, true)
        setPlaceholder(CodeGPTBundle.get("toolwindow.chat.textArea.emptyText"))
        IdeEventQueue.getInstance().addDispatcher(
            PromptTextFieldEventDispatcher(this, suggestionsPopupManager, appliedInlays) {
                onSubmit(text, appliedInlays)
                clear()
            },
            this
        )
    }

    fun addInlayElement(actionPrefix: String, text: String?, actionItem: SuggestionActionItem?) {
        editor?.let {
            var startOffset = it.document.text.lastIndexOf(AT_CHAR)
            if (startOffset == -1) {
                startOffset = it.document.textLength
            }

            addInlayElement(startOffset, actionPrefix, text, actionItem)
        }
    }

    fun addInlayElement(
        actionPrefix: String,
        text: String,
        editorFile: VirtualFile? = null,
        tooltipText: String? = null
    ) {
        editor?.let {
            addInlayElement(
                it.caretModel.offset,
                actionPrefix,
                text,
                editorFile = editorFile,
                tooltipText = tooltipText
            )
        }
    }

    private fun addInlayElement(
        startOffset: Int,
        actionPrefix: String,
        text: String?,
        actionItem: SuggestionActionItem? = null,
        editorFile: VirtualFile? = null,
        tooltipText: String? = null
    ) {
        runUndoTransparentWriteAction {
            document.deleteString(startOffset, document.textLength)
            document.setText(document.text + " ")
            val inlay = editor?.inlayModel?.addInlineElement(
                startOffset,
                true,
                PromptTextFieldInlayRenderer(
                    project,
                    actionPrefix,
                    text,
                    editorFile?.name ?: "",
                    tooltipText
                ) { inlay ->
                    appliedInlays.removeIf { appliedInlay -> appliedInlay.inlay == inlay }
                    inlay.dispose()
                })
            if (inlay != null) {
                // TODO
                if (tooltipText != null && editorFile != null) {
                    appliedInlays.add(AppliedCodeActionInlay(inlay, tooltipText, editorFile))
                } else {
                    appliedInlays.add(AppliedSuggestionActionInlay(inlay, actionItem))
                }
                editor?.caretModel?.moveToOffset(document.textLength)
            }
        }
    }

    override fun createEditor(): EditorEx {
        val editorEx = super.createEditor()
        editorEx.settings.isUseSoftWraps = true
        setupDocumentListener(editorEx)
        return editorEx
    }

    override fun updateBorder(editor: EditorEx) {
        editor.setBorder(JBUI.Borders.empty(4, 8))
    }

    override fun dispose() {
        clear()
    }

    private fun clear() {
        runInEdt {
            text = ""
            clearInlays()
        }
    }

    private fun clearInlays() {
        runUndoTransparentWriteAction {
            appliedInlays.forEach { it.inlay.dispose() }
            appliedInlays.clear()
        }
    }

    private fun setupDocumentListener(editor: EditorEx) {
        editor.document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                adjustHeight(editor)
                onTextChanged(event.document.text)

                if (event.document.text.isEmpty()) {
                    suggestionsPopupManager.hidePopup()
                    clearInlays()
                    return
                }
            }
        }, this)
    }

    private fun adjustHeight(editor: EditorEx) {
        val contentHeight = editor.contentComponent.preferredSize.height + 8
        val maxHeight = JBUI.scale(getToolWindowHeight() / 2)
        val newHeight = minOf(contentHeight, maxHeight)

        runInEdt {
            preferredSize = Dimension(width, newHeight)
            editor.setVerticalScrollbarVisible(contentHeight > maxHeight)
            parent?.revalidate()
        }
    }

    private fun getToolWindowHeight(): Int {
        return project.service<ToolWindowManager>()
            .getToolWindow("CodeGPT")?.component?.visibleRect?.height ?: 400
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
                            if (e.isShiftDown) {
                                handleShiftEnter(e)
                            } else if (e.modifiersEx and InputEvent.ALT_DOWN_MASK == 0
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

    private fun handleShiftEnter(e: KeyEvent) {
        textField.editor?.let { editor ->
            runUndoTransparentWriteAction {
                val document = editor.document
                val caretModel = editor.caretModel
                val offset = caretModel.offset
                val lineEndOffset = document.getLineEndOffset(document.getLineNumber(offset))
                val remainingText = if (offset < lineEndOffset) {
                    val textAfterCursor = document.getText(TextRange(offset, lineEndOffset))
                    document.deleteString(offset, lineEndOffset)
                    textAfterCursor
                } else {
                    ""
                }

                document.insertString(offset, "\n" + remainingText)
                caretModel.moveToOffset(offset + 1)
            }
        }
        e.consume()
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
