package ee.carlrobert.codegpt.predictions

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffContext
import com.intellij.diff.requests.DiffRequest
import com.intellij.diff.requests.SimpleDiffRequest
import com.intellij.diff.tools.fragmented.UnifiedDiffChange
import com.intellij.diff.tools.fragmented.UnifiedDiffViewer
import com.intellij.diff.util.DiffUtil
import com.intellij.ide.plugins.newui.TagComponent
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.*
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.concurrency.annotations.RequiresEdt
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.components.BorderLayoutPanel
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.CodeGPTKeys
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Point
import javax.swing.Box
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.math.abs
import kotlin.math.max

class CodeSuggestionDiffViewer(
    request: DiffRequest,
    private val mainEditor: Editor,
    private val isManuallyOpened: Boolean
) : UnifiedDiffViewer(MyDiffContext(mainEditor.project), request), Disposable {

    private val popup: JBPopup = createSuggestionDiffPopup(component)
    private val visibleAreaListener: VisibleAreaListener
    private val documentListener: DocumentListener

    private var applyInProgress = false

    init {
        visibleAreaListener = getVisibleAreaListener()
        documentListener = getDocumentListener()
        setupDiffEditor()
        mainEditor.scrollingModel.addVisibleAreaListener(visibleAreaListener)
        mainEditor.document.addDocumentListener(documentListener, this)

        Disposer.register(popup) { clearListeners() }
    }

    override fun onDispose() {
        popup.dispose()
        super.onDispose()
    }

    override fun onAfterRediff() {
        val change = getClosestChange() ?: return

        myEditor.component.preferredSize =
            Dimension(
                mainEditor.component.width / 2,
                (myEditor.lineHeight * change.getChangedLinesCount())
            )
        adjustPopupSize(popup, myEditor)

        val changeOffset = change.lineFragment.startOffset1
        val adjustedLocation =
            getAdjustedPopupLocation(popup, mainEditor, changeOffset)

        if (popup.isVisible) {
            popup.setLocation(adjustedLocation)
        } else {
            popup.showInScreenCoordinates(mainEditor.component, adjustedLocation)
        }

        scrollToChange(change)
    }

    fun applyChanges() {
        val changes = diffChanges ?: emptyList()
        val change = getClosestChange() ?: return

        if (isStateIsOutOfDate) return
        if (!isEditable(masterSide, true)) return

        val document: Document = getDocument(masterSide)

        DiffUtil.executeWriteCommand(document, project, null) {
            applyInProgress = true
            try {
                replaceChange(change, masterSide)
            } finally {
                applyInProgress = false
            }
            moveCaretToChange(change, document)
            scheduleRediff()
        }
        rediff(true)

        if (changes.size == 1) {
            popup.dispose()
        }
    }

    fun isVisible(): Boolean {
        return popup.isVisible
    }

    private fun setupDiffEditor() {
        myEditor.apply {
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

    private fun clearListeners() {
        mainEditor.putUserData(CodeGPTKeys.EDITOR_PREDICTION_DIFF_VIEWER, null)
        mainEditor.scrollingModel.removeVisibleAreaListener(visibleAreaListener)
        mainEditor.document.removeDocumentListener(documentListener)
    }

    private fun getClosestChange(): UnifiedDiffChange? {
        val changes = diffChanges ?: emptyList()
        val cursorOffset = mainEditor.caretModel.offset
        return changes.minByOrNull { abs(it.lineFragment.startOffset1 - cursorOffset) }
    }

    private fun getTagPanel(): JComponent {
        val tagPanel = JPanel(FlowLayout(FlowLayout.LEADING, 0, 0)).apply {
            isOpaque = false
        }
        tagPanel.add(
            TagComponent(
                "Open: ${getShortcutText(OpenPredictionAction.ID)}"
            ).apply {
                setListener({ _, _ ->
                    service<PredictionService>().openDirectPrediction(
                        mainEditor,
                        content2.document.text
                    )
                    popup.dispose()
                }, component)
                font = JBUI.Fonts.smallFont()
            }
        )
        tagPanel.add(Box.createHorizontalStrut(6))
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
        (myEditor.scrollPane as JBScrollPane).statusComponent = BorderLayoutPanel()
            .andTransparent()
            .withBorder(JBUI.Borders.empty(4))
            .addToRight(getTagPanel())

        val footerText = if (isManuallyOpened) {
            CodeGPTBundle.get("shared.escToCancel")
        } else {
            "Trigger manually: ${getShortcutText(OpenPredictionAction.ID)} · ${CodeGPTBundle.get("shared.escToCancel")}"
        }

        myEditor.component.add(
            BorderLayoutPanel()
                .addToRight(
                    JBLabel(footerText)
                        .apply {
                            font = JBUI.Fonts.miniFont()
                        })
                .apply {
                    background = editor.backgroundColor
                    border = JBUI.Borders.empty(4)
                },
            BorderLayout.SOUTH
        )
    }

    private fun getVisibleAreaListener(): VisibleAreaListener {
        return object : VisibleAreaListener {
            override fun visibleAreaChanged(event: VisibleAreaEvent) {
                val change = getClosestChange() ?: return
                val adjustedLocation = getAdjustedPopupLocation(
                    popup,
                    mainEditor,
                    change.lineFragment.startOffset1
                )

                if (popup.isVisible && !popup.isDisposed) {
                    adjustPopupSize(popup, myEditor)
                    popup.setLocation(adjustedLocation)
                }
            }
        }
    }

    private fun getDocumentListener(): DocumentListener {
        return object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                if (applyInProgress) return

                popup.setUiVisible(false)
                onDispose()
            }
        }
    }

    private fun scrollToChange(change: UnifiedDiffChange) {
        val pointToScroll = myEditor.logicalPositionToXY(LogicalPosition(change.line1, 0))
        pointToScroll.y -= myEditor.lineHeight
        DiffUtil.scrollToPoint(myEditor, pointToScroll, false)
    }

    private fun moveCaretToChange(change: UnifiedDiffChange, document: Document) {
        val changeEndOffset = change.lineFragment.endOffset2
        val previousChar = document.getText(TextRange(changeEndOffset - 1, changeEndOffset))
        val offset = if (previousChar == "\n") changeEndOffset - 1 else changeEndOffset

        mainEditor.caretModel.moveToOffset(max(offset, 0))

        val offsetPosition = mainEditor.offsetToXY(offset)
        val offsetVisible = mainEditor.scrollingModel.visibleArea.contains(offsetPosition)
        if (!offsetVisible) {
            DiffUtil.scrollToCaret(mainEditor, false)
        }
    }

    private class MyDiffContext(private val project: Project?) : DiffContext() {
        private val ownContext: UserDataHolder = UserDataHolderBase()

        override fun getProject() = project

        override fun isFocusedInWindow(): Boolean {
            return false
        }

        override fun isWindowFocused(): Boolean {
            return false
        }

        override fun requestFocusInWindow() {
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
            if ((editor is EditorEx && editor.virtualFile == null) || editor.isViewer) {
                return
            }

            editor.getUserData(CodeGPTKeys.EDITOR_PREDICTION_DIFF_VIEWER)?.dispose()

            val diffRequest = createSimpleDiffRequest(editor, nextRevision)
            val diffViewer = CodeSuggestionDiffViewer(diffRequest, editor, isManuallyOpened)
            editor.putUserData(CodeGPTKeys.EDITOR_PREDICTION_DIFF_VIEWER, diffViewer)
            diffViewer.rediff(true)
        }
    }
}

fun createSimpleDiffRequest(editor: Editor, nextRevision: String): SimpleDiffRequest {
    val project = editor.project
    val virtualFile = (editor as EditorEx).virtualFile
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
    if (!editor.component.isShowing) {
        val point = Point(pointInEditor)
        SwingUtilities.convertPointToScreen(point, editor.component)
        return point
    }

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