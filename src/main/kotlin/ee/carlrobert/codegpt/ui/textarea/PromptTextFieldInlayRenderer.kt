package ee.carlrobert.codegpt.ui.textarea

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorCustomElementRenderer
import com.intellij.openapi.editor.Inlay
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.ui.JBUI
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.geom.Rectangle2D
import javax.swing.Icon

class PromptTextFieldInlayRenderer(
    private val project: Project,
    private val actionPrefix: String,
    private val text: String?,
    private val fileName: String,
    private val tooltipText: String?,
    private val onClose: (Inlay<*>) -> Unit
) : EditorCustomElementRenderer {

    private val closeIcon = AllIcons.Actions.Close
    private val helpIcon = AllIcons.General.ContextHelp

    private var tooltip: JBPopup? = null

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val editor = inlay.editor
        val font = editor.colorsScheme.getFont(EditorFontType.PLAIN)
        val textWidth = editor.component.getFontMetrics(font)
            .stringWidth(actionPrefix + (if (text != null) ":$text" else ""))

        if (tooltipText.isNullOrEmpty()) {
            return textWidth + closeIcon.iconWidth + JBUI.scale(10)
        }

        return textWidth + closeIcon.iconWidth + JBUI.scale(10) + helpIcon.iconWidth + JBUI.scale(10)
    }

    override fun paint(
        inlay: Inlay<*>,
        g: Graphics2D,
        target: Rectangle2D,
        textAttributes: TextAttributes
    ) {
        val editor = inlay.editor
        val currentTextAttributes: TextAttributes? =
            EditorColorsManager.getInstance().globalScheme.getAttributes(
                DefaultLanguageHighlighterColors.INLAY_DEFAULT
            )

        drawBackground(g, target, currentTextAttributes)
        drawBorder(g, target)
        drawText(g, target, editor, currentTextAttributes)
        drawCloseIcon(g, target)
        if (tooltipText != null) {
            drawHelpIcon(g, target)
        }

        addMouseListeners(editor, inlay, target)
    }

    private fun drawBackground(
        g: Graphics2D,
        target: Rectangle2D,
        textAttributes: TextAttributes?
    ) {
        g.color = textAttributes?.backgroundColor ?: JBColor.background()
        g.fill(target)
    }

    private fun drawBorder(g: Graphics2D, target: Rectangle2D) {
        g.color = JBColor.border()
        g.draw(target)
    }

    private fun drawText(
        g: Graphics2D,
        target: Rectangle2D,
        editor: Editor,
        textAttributes: TextAttributes?
    ) {
        g.font = editor.colorsScheme.getFont(EditorFontType.PLAIN)

        val metrics = g.fontMetrics
        val textHeight = metrics.height
        val startX = (target.x + JBUI.scale(5)).toInt()
        val startY = (target.y + (target.height - textHeight) / 2 + metrics.ascent).toInt()

        g.color = textAttributes?.foregroundColor ?: JBColor.foreground()
        g.drawString(actionPrefix, startX, startY)

        if (!text.isNullOrEmpty()) {
            val prefixWidth = metrics.stringWidth(actionPrefix)
            g.color = service<EditorColorsManager>().globalScheme.defaultForeground
            g.drawString(":$text", startX + prefixWidth, startY)
        }
    }

    private fun drawCloseIcon(g: Graphics2D, target: Rectangle2D) {
        val iconX = (target.x + target.width - closeIcon.iconWidth - JBUI.scale(5)).toInt()
        val iconY = (target.y + (target.height - closeIcon.iconHeight) / 2).toInt()
        closeIcon.paintIcon(null, g, iconX, iconY)
    }

    private fun drawHelpIcon(g: Graphics2D, target: Rectangle2D) {
        val iconX =
            (target.x + target.width - closeIcon.iconWidth - helpIcon.iconWidth - JBUI.scale(10)).toInt()
        val iconY = (target.y + (target.height - helpIcon.iconHeight) / 2).toInt()
        helpIcon.paintIcon(null, g, iconX, iconY)
    }

    private fun showTooltip(inlay: Inlay<*>) {
        if (tooltipText != null) {
            hideTooltip()

            val tooltipContent = CodePreviewTooltipContent(project, fileName, tooltipText)
            tooltip = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(
                    tooltipContent,
                    tooltipContent.getFocusableComponent()
                )
                .setTitle("Code Preview")
                .setResizable(true)
                .setMovable(true)
                .setStretchToOwnerHeight(true)
                .setStretchToOwnerWidth(true)
                .setMinSize(
                    Dimension(
                        tooltipContent.preferredSize?.width ?: 240,
                        (tooltipContent.preferredSize?.height ?: 0)
                    )
                )
                .createPopup()
            tooltip?.show(
                RelativePoint(
                    inlay.editor.contentComponent,
                    calculatePopupPoint(inlay, tooltipContent)
                )
            )
        }
    }

    private fun calculatePopupPoint(
        inlay: Inlay<*>,
        tooltipContent: CodePreviewTooltipContent
    ): Point {
        val visibleArea = inlay.editor.scrollingModel.visibleArea
        val inlayBounds = inlay.bounds
        if (inlayBounds != null) {
            val x = inlayBounds.x
            val tooltipHeight = tooltipContent.preferredSize?.height ?: 0
            val y = inlayBounds.y - tooltipHeight
            return Point(x, y)
        }
        return Point(visibleArea.x, visibleArea.y)
    }

    private fun hideTooltip() {
        tooltip?.dispose()
    }

    private fun addMouseListeners(editor: Editor, inlay: Inlay<*>, target: Rectangle2D) {
        fun isWithinIconBounds(e: MouseEvent, icon: Icon): Boolean {
            val iconX = when (icon) {
                closeIcon -> (target.x + target.width - closeIcon.iconWidth - JBUI.scale(5)).toInt()
                helpIcon -> (target.x + target.width - closeIcon.iconWidth - helpIcon.iconWidth - JBUI.scale(
                    10
                )).toInt()

                else -> return false
            }
            val iconY = (target.y + (target.height - icon.iconHeight) / 2).toInt()
            return e.x >= iconX && e.x <= iconX + icon.iconWidth &&
                    e.y >= iconY && e.y <= iconY + icon.iconHeight
        }

        fun updateCursor(event: EditorMouseEvent, inlay: Inlay<*>) {
            editor.contentComponent.let {
                if (inlay.isValid) {
                    val inlayBounds = inlay.bounds
                    val mouseX = event.mouseEvent.x.toDouble()
                    val mouseY = event.mouseEvent.y.toDouble()

                    if (inlayBounds != null && inlayBounds.contains(mouseX, mouseY)) {
                        it.cursor = Cursor.getDefaultCursor()
                        return
                    }
                }
                it.cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)
            }
        }

        editor.addEditorMouseMotionListener(object : EditorMouseMotionListener {
            override fun mouseMoved(event: EditorMouseEvent) {
                findInlayAtMouseEvent(event)?.let {
                    updateCursor(event, it)
                }
            }

            private fun findInlayAtMouseEvent(event: EditorMouseEvent): Inlay<*>? {
                val offset = editor.logicalPositionToOffset(event.logicalPosition)
                val inlays = editor.inlayModel.getInlineElementsInRange(offset, offset)
                return inlays.firstOrNull { inlay ->
                    val inlayBounds = editor.visualPositionToXY(inlay.visualPosition)
                    val mousePoint = event.mouseEvent.point
                    inlayBounds.x <= mousePoint.x && mousePoint.x <= inlayBounds.x + inlay.widthInPixels
                }
            }
        })

        editor.addEditorMouseListener(object : EditorMouseListener {
            override fun mouseClicked(event: EditorMouseEvent) {
                when {
                    isWithinIconBounds(event.mouseEvent, closeIcon) -> {
                        onClose(inlay)
                        event.consume()
                    }

                    isWithinIconBounds(event.mouseEvent, helpIcon) -> {
                        showTooltip(inlay)
                        event.consume()
                    }
                }
            }
        })
    }
}
