package ee.carlrobert.codegpt.predictions

import com.intellij.codeInsight.inline.completion.session.InlineCompletionContext
import com.intellij.codeInsight.inline.completion.session.InlineCompletionSession
import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffContext
import com.intellij.diff.requests.DiffRequest
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.diff.tools.combined.COMBINED_DIFF_MAIN_UI
import com.intellij.diff.tools.fragmented.UnifiedDiffChange
import com.intellij.diff.tools.fragmented.UnifiedDiffViewer
import com.intellij.diff.util.DiffUserDataKeysEx.ScrollToPolicy
import com.intellij.diff.util.DiffUtil
import com.intellij.diff.util.Side
import com.intellij.ide.plugins.newui.TagComponent
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.UserDataHolder
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.concurrency.annotations.RequiresEdt
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.components.BorderLayoutPanel
import ee.carlrobert.codegpt.CodeGPTKeys
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Point
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JPanel

class CodeSuggestionDiffViewer(
    request: DiffRequest,
    private val mainEditor: Editor,
    private val isManuallyOpened: Boolean
) : UnifiedDiffViewer(MyDiffContext(mainEditor.project), request), Disposable {

    private val popup: JBPopup = createSuggestionDiffPopup(component)
    private val keyListener: KeyAdapter
    private val visibleAreaListener: VisibleAreaListener
    private val initialDocumentText: String = mainEditor.document.text

    init {
        keyListener = object : KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                if (mainEditor.document.text != initialDocumentText) {
                    popup.setUiVisible(false)
                    onDispose()
                }
            }
        }
        visibleAreaListener = object : VisibleAreaListener {
            override fun visibleAreaChanged(event: VisibleAreaEvent) {
                val change = diffChanges?.firstOrNull() ?: return
                val adjustedLocation = getAdjustedPopupLocation(
                    popup,
                    mainEditor,
                    change.lineFragment.startOffset1
                )

                if (popup.isVisible && !popup.isDisposed) {
                    adjustPopupSize(popup, editor)
                    popup.setLocation(adjustedLocation)
                }
            }
        }
        setupDiffEditor()
        mainEditor.contentComponent.addKeyListener(keyListener)
        mainEditor.scrollingModel.addVisibleAreaListener(visibleAreaListener)
    }

    fun isVisible(): Boolean {
        return popup.isVisible
    }

    override fun onDispose() {
        popup.dispose()
        mainEditor.putUserData(CodeGPTKeys.EDITOR_PREDICTION_DIFF_VIEWER, null)
        mainEditor.contentComponent.removeKeyListener(keyListener)
        mainEditor.scrollingModel.removeVisibleAreaListener(visibleAreaListener)
        super.onDispose()
    }

    override fun onAfterRediff() {
        val change = diffChanges?.firstOrNull() ?: return

        editor.component.preferredSize =
            Dimension(
                mainEditor.component.width / 2,
                (editor.lineHeight * change.getChangedLinesCount())
            )
        adjustPopupSize(popup, editor)

        val adjustedLocation =
            getAdjustedPopupLocation(
                popup,
                mainEditor,
                change.lineFragment.startOffset1
            )

        if (popup.isVisible) {
            adjustPopupSize(popup, editor)
            popup.setLocation(adjustedLocation)
        } else {
            popup.showInScreenCoordinates(mainEditor.component, adjustedLocation)
        }

        doScrollToFirstChange()
    }

    private fun setupDiffEditor() {
        editor.apply {
            settings.apply {
                additionalLinesCount = 0
                isFoldingOutlineShown = false
                isCaretRowShown = false
                isBlinkCaret = false
                isDndEnabled = false
                isIndentGuidesShown = false
            }
            gutterComponentEx.isVisible = false
            gutterComponentEx.parent.isVisible = false
            scrollPane.horizontalScrollBar.isOpaque = false
        }

        setupStatusLabel()
    }

    private fun getTagPanel(): JComponent {
        val tagPanel = JPanel(FlowLayout(FlowLayout.LEADING, 0, 0))
        if (!isManuallyOpened) {
            tagPanel.add(
                TagComponent(
                    "Trigger manually: ${getShortcutText(TriggerCustomPredictionAction.ID)}"
                ).apply {
                    font = JBUI.Fonts.smallFont()
                }
            )
            tagPanel.add(Box.createHorizontalStrut(6))
        }
        tagPanel.add(TagComponent("Accept: ${getShortcutText(AcceptNextPredictionRevisionAction.ID)}").apply {
            setListener({ _, _ ->
                applyChanges()
                popup.dispose()
            }, component)
            font = JBUI.Fonts.smallFont()
        })
        return tagPanel
    }

    private fun setupStatusLabel() {
        (editor.scrollPane as JBScrollPane).statusComponent = BorderLayoutPanel()
            .andTransparent()
            .withBorder(JBUI.Borders.empty(4))
            .addToRight(getTagPanel())
    }

    fun applyChanges() {
        val changes = diffChanges ?: emptyList()
        val change = changes.firstOrNull() ?: return

        if (isStateIsOutOfDate) return
        if (!isEditable(Side.LEFT, true)) return

        val document: Document = getDocument(Side.LEFT)

        DiffUtil.executeWriteCommand(document, project, null) {
            replaceChange(change, Side.RIGHT)
            moveCaretToChange(change, document)
            scheduleRediff()
        }
        rediff(true)

        if (changes.size == 1) {
            popup.dispose()
        }
    }

    private fun doScrollToFirstChange() {
        val changes = myModel.diffChanges ?: return

        var targetChange = ScrollToPolicy.FIRST_CHANGE.select(
            ContainerUtil.filter(
                changes
            ) { it: UnifiedDiffChange -> !it.isSkipped })
        if (targetChange == null) targetChange = ScrollToPolicy.FIRST_CHANGE.select(changes)
        if (targetChange == null) return

        val pointToScroll = myEditor.offsetToXY(targetChange.lineFragment.startOffset1)
        pointToScroll.y -= myEditor.lineHeight
        DiffUtil.scrollToPoint(myEditor, pointToScroll, false)
    }

    private fun moveCaretToChange(change: UnifiedDiffChange, document: Document) {
        val changeEndOffset = change.lineFragment.endOffset2
        val previousChar = document.getText(TextRange(changeEndOffset - 1, changeEndOffset))
        val offset = if (previousChar == "\n") changeEndOffset - 1 else changeEndOffset

        mainEditor.caretModel.moveToOffset(offset)

        val offsetPosition = mainEditor.offsetToXY(offset)
        val isOffsetVisible = mainEditor.scrollingModel.visibleArea.contains(offsetPosition)
        if (!isOffsetVisible) {
            DiffUtil.scrollToCaret(mainEditor, false)
        }
    }

    private class MyDiffContext(private val project: Project?) : DiffContext() {
        private val mainUi get() = getUserData(COMBINED_DIFF_MAIN_UI)

        private val ownContext: UserDataHolder = UserDataHolderBase()

        override fun getProject() = project
        override fun isFocusedInWindow(): Boolean = mainUi?.isFocusedInWindow() ?: false
        override fun isWindowFocused(): Boolean = mainUi?.isWindowFocused() ?: false
        override fun requestFocusInWindow() {
            mainUi?.requestFocusInWindow()
        }

        override fun <T> getUserData(key: Key<T>): T? {
            return ownContext.getUserData(key)
        }

        override fun <T> putUserData(key: Key<T>, value: T?) {
            ownContext.putUserData(key, value)
        }
    }

    companion object {

        @RequiresEdt
        fun displayInlineDiff(
            editor: Editor,
            nextRevision: String,
            isManuallyOpened: Boolean = false
        ) {
            editor.getUserData(CodeGPTKeys.EDITOR_PREDICTION_DIFF_VIEWER)?.dispose()
            editor.putUserData(CodeGPTKeys.REMAINING_EDITOR_COMPLETION, null)
            InlineCompletionSession.getOrNull(editor)?.let {
                if (it.isActive()) {
                    InlineCompletionContext.getOrNull(editor)?.clear()
                }
            }

            val diffRequest = createSimpleDiffRequest(editor, nextRevision)
            val diffViewer = CodeSuggestionDiffViewer(diffRequest, editor, isManuallyOpened)
            editor.putUserData(CodeGPTKeys.EDITOR_PREDICTION_DIFF_VIEWER, diffViewer)
            diffViewer.rediff(true)
        }
    }
}

fun createSimpleDiffRequest(editor: Editor, nextRevision: String): SimpleDiffRequest {
    val project = editor.project
    val virtualFile = editor.virtualFile
    val tempDiffFile = LightVirtualFile(virtualFile.name, nextRevision)
    val diffContentFactory = DiffContentFactory.getInstance()
    return SimpleDiffRequest(
        null,
        diffContentFactory.create(project, virtualFile),
        diffContentFactory.create(project, tempDiffFile),
        null,
        null
    )
}

fun UnifiedDiffChange.getChangedLinesCount(): Int {
    val insertedLines = insertedRange.end - insertedRange.start
    val deletedLines = deletedRange.end - deletedRange.start
    return deletedLines + insertedLines + 2
}

fun getAdjustedPopupLocation(popup: JBPopup, editor: Editor, changeOffset: Int): Point {
    val pointInEditor = editor.offsetToXY(changeOffset)
    val visibleArea = editor.scrollingModel.visibleArea
    val editorLocationOnScreen = editor.component.locationOnScreen
    val isOffsetVisible = visibleArea.contains(pointInEditor)
    val popupY = if (isOffsetVisible) {
        editorLocationOnScreen.y + pointInEditor.y - editor.scrollingModel.verticalScrollOffset
    } else {
        if (pointInEditor.y < visibleArea.y) {
            editorLocationOnScreen.y
        } else {
            editorLocationOnScreen.y + visibleArea.height - popup.size.height
        }
    }
    val popupX = editorLocationOnScreen.x + editor.component.width - popup.size.width
    return Point(popupX, popupY - editor.lineHeight)
}

fun adjustPopupSize(popup: JBPopup, editor: Editor) {
    val newWidth = editor.component.preferredSize.width
    val newHeight = editor.component.preferredSize.height
    popup.size = Dimension(newWidth, newHeight)
    popup.content.revalidate()
    popup.content.repaint()
}

fun getShortcutText(actionId: String): String {
    return KeymapUtil.getFirstKeyboardShortcutText(
        ActionManager.getInstance().getAction(actionId)
    )
}

fun createSuggestionDiffPopup(content: JComponent): JBPopup {
    return JBPopupFactory.getInstance().createComponentPopupBuilder(content, null)
        .setNormalWindowLevel(true)
        .setCancelOnClickOutside(false)
        .setRequestFocus(false)
        .setFocusable(true)
        .setMovable(true)
        .setResizable(true)
        .setShowBorder(true)
        .setCancelKeyEnabled(true)
        .setCancelOnWindowDeactivation(false)
        .setCancelOnOtherWindowOpen(false)
        .createPopup()
}