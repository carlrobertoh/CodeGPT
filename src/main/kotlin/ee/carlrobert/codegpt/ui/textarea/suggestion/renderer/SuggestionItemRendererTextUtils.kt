package ee.carlrobert.codegpt.ui.textarea.suggestion.renderer

import com.intellij.ui.ColorUtil
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.JBUI.CurrentTheme.GotItTooltip
import javax.swing.JLabel

object SuggestionItemRendererTextUtils {

    fun String.searchText(): String? {
        val lastAtIndex = this.lastIndexOf('@')
        if (lastAtIndex == -1) return null

        return this.substring(lastAtIndex + 1).takeIf { it.isNotEmpty() }
    }

    fun String.truncate(maxWidth: Int, truncateFromStart: Boolean = false): String {
        val fontMetrics = getFontMetrics(JBUI.Fonts.smallFont())
        if (fontMetrics.stringWidth(this) <= maxWidth) return this

        val ellipsis = "..."
        var truncated = this
        while (fontMetrics.stringWidth(ellipsis + truncated) > maxWidth && truncated.isNotEmpty()) {
            truncated = if (truncateFromStart) {
                truncated.drop(1)
            } else {
                truncated.dropLast(1)
            }
        }
        return if (truncateFromStart) ellipsis + truncated else truncated + ellipsis
    }

    fun String.highlightSearchText(searchText: String): String {
        val searchIndex = this.indexOf(searchText, ignoreCase = true)
        if (searchIndex == -1) return this

        val prefix = this.substring(0, searchIndex)
        val highlight =
            this.substring(
                searchIndex,
                (searchIndex + searchText.length).coerceAtMost(this.length)
            )
        val suffix = this.substring((searchIndex + searchText.length).coerceAtMost(this.length))

        val foregroundHex = ColorUtil.toHex(GotItTooltip.codeForeground(true))
        val backgroundHex = ColorUtil.toHex(GotItTooltip.codeBackground(true))

        return "<html>$prefix<span style=\"color: $foregroundHex;background-color: $backgroundHex;\">$highlight</span>$suffix</html>"
    }

    private fun getFontMetrics(font: java.awt.Font) = JBUI.Fonts.smallFont().let {
        JLabel().getFontMetrics(font)
    }
}