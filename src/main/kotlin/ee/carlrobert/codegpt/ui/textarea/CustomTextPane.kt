package ee.carlrobert.codegpt.ui.textarea

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.CodeGPTBundle
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.event.ActionEvent
import javax.swing.AbstractAction
import javax.swing.JTextPane
import javax.swing.KeyStroke
import javax.swing.UIManager
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext

class CustomTextPane(private val onSubmit: (String) -> Unit) : JTextPane() {

    init {
        isOpaque = false
        background = JBColor.namedColor("Editor.SearchField.background")
        document = DefaultStyledDocument()
        border = JBUI.Borders.empty(8)
        isFocusable = true
        font = if (Registry.`is`("ide.find.use.editor.font", false)) {
            EditorUtil.getEditorFont()
        } else {
            UIManager.getFont("TextField.font")
        }
        inputMap.put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break")
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "text-submit")
        actionMap.put("text-submit", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                onSubmit(text)
            }
        })
    }

    fun highlightText(text: String) {
        val lastIndex = this.text.lastIndexOf('@')
        if (lastIndex != -1) {
            val styleContext = StyleContext.getDefaultStyleContext()
            val fileNameStyle = styleContext.addStyle("smart-highlighter", null)
            val fontFamily = service<EditorColorsManager>().globalScheme
                .getFont(EditorFontType.PLAIN)
                .deriveFont(JBFont.label().size.toFloat())
                .family

            StyleConstants.setFontFamily(fileNameStyle, fontFamily)
            StyleConstants.setForeground(
                fileNameStyle,
                JBColor.foreground()
            )
            StyleConstants.setBackground(
                fileNameStyle,
                JBColor.background()
            )

            document.remove(lastIndex + 1, document.length - (lastIndex + 1))
            document.insertString(lastIndex + 1, text, fileNameStyle)
            styledDocument.setCharacterAttributes(
                lastIndex,
                text.length,
                fileNameStyle,
                true
            )
            document.insertString(
                document.length,
                " ",
                styleContext.getStyle(StyleContext.DEFAULT_STYLE)
            )
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        if (document.length == 0) {
            g2d.color = JBColor.GRAY
            g2d.font = if (Registry.`is`("ide.find.use.editor.font", false)) {
                EditorUtil.getEditorFont()
            } else {
                UIManager.getFont("TextField.font")
            }
            // Draw placeholder
            g2d.drawString(
                CodeGPTBundle.get("toolwindow.chat.textArea.emptyText"),
                insets.left,
                g2d.fontMetrics.maxAscent + insets.top
            )
        }
    }
}