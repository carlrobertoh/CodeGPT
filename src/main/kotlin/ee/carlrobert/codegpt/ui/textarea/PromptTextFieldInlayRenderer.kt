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
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.Cursor
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.geom.Rectangle2D

class PromptTextFieldInlayRenderer(
    private val actionPrefix: String,
    private val text: String?,
    private val onClose: (Inlay<*>) -> Unit
) : EditorCustomElementRenderer {

    private val closeIcon = AllIcons.Actions.Close

    override fun calcWidthInPixels(inlay: Inlay<*>): Int {
        val editor = inlay.editor
        val font = editor.colorsScheme.getFont(EditorFontType.PLAIN)
        val textWidth = editor.component.getFontMetrics(font)
            .stringWidth(actionPrefix + (if (text != null) ":$text" else ""))
        return textWidth + closeIcon.iconWidth + JBUI.scale(10)
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

    private fun addMouseListeners(editor: Editor, inlay: Inlay<*>, target: Rectangle2D) {
        fun isWithinIconBounds(e: MouseEvent): Boolean {
            val iconX = (target.x + target.width - closeIcon.iconWidth - JBUI.scale(5)).toInt()
            val iconY = (target.y + (target.height - closeIcon.iconHeight) / 2).toInt()
            return e.x >= iconX && e.x <= iconX + closeIcon.iconWidth &&
                    e.y >= iconY && e.y <= iconY + closeIcon.iconHeight
        }

        fun updateCursor(event: MouseEvent, inlay: Inlay<*>) {
            editor.contentComponent.let {
                if (inlay.isValid) {
                    val inlayBounds = inlay.bounds
                    if (inlayBounds != null && inlayBounds.contains(event.x, event.y)) {
                        it.cursor = if (isWithinIconBounds(event)) {
                            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        } else {
                            Cursor.getDefaultCursor()
                        }
                        return@let
                    }
                }
                it.cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)
            }
        }

        editor.addEditorMouseMotionListener(object : EditorMouseMotionListener {
            override fun mouseMoved(event: EditorMouseEvent) {
                updateCursor(event.mouseEvent, inlay)
            }
        })

        editor.addEditorMouseListener(object : EditorMouseListener {
            override fun mouseClicked(event: EditorMouseEvent) {
                if (isWithinIconBounds(event.mouseEvent)) {
                    onClose(inlay)
                    event.consume()
                }
            }
        })
    }
}
