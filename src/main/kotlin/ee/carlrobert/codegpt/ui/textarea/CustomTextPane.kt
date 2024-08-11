package ee.carlrobert.codegpt.ui.textarea

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.util.TextRange
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

class CustomTextPane(
    private val highlightedTextRanges: MutableList<Pair<TextRange, Boolean>>,
    private val onSubmit: (String) -> Unit
) : JTextPane() {

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
                var textWithoutActions = text
                highlightedTextRanges.forEach {
                    val (textRange, replacement) = it
                    if (replacement) {
                        textWithoutActions =
                            textWithoutActions.replace(text.substring(textRange.startOffset, textRange.endOffset), "")
                    }
                }
                onSubmit(textWithoutActions.trim())
            }
        })
    }

    fun appendHighlightedText(
        text: String,
        searchChar: Char = '@',
        withWhitespace: Boolean = true,
        replacement: Boolean = true
    ): TextRange? {
        val lastIndex = this.text.lastIndexOf(searchChar)
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
                JBUI.CurrentTheme.GotItTooltip.codeForeground(true)
            )
            StyleConstants.setBackground(
                fileNameStyle,
                JBUI.CurrentTheme.GotItTooltip.codeBackground(true)
            )

            val startOffset = lastIndex + 1
            document.remove(startOffset, document.length - startOffset)
            document.insertString(startOffset, text, fileNameStyle)
            styledDocument.setCharacterAttributes(
                lastIndex,
                text.length,
                fileNameStyle,
                true
            )
            if (withWhitespace) {
                document.insertString(
                    document.length,
                    " ",
                    styleContext.getStyle(StyleContext.DEFAULT_STYLE)
                )
            }
            val modifiedStartOffset = if (searchChar == '@') startOffset - 1 else startOffset
            val endOffset = startOffset + text.length + (if (withWhitespace) 1 else 0)
            val textRange = TextRange(modifiedStartOffset, endOffset)
            highlightedTextRanges.add(Pair(textRange, replacement))
            return textRange
        }
        return null
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
            g2d.drawString(
                CodeGPTBundle.get("toolwindow.chat.textArea.emptyText"),
                insets.left,
                g2d.fontMetrics.maxAscent + insets.top
            )
        }
    }
}