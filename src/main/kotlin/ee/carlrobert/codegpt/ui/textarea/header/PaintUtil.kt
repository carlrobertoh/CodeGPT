package ee.carlrobert.codegpt.ui.textarea.header

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.*
import java.awt.geom.RoundRectangle2D
import javax.swing.JComponent

object PaintUtil {

    private const val ARC_WIDTH = 8f
    private const val ARC_HEIGHT = 8f
    private const val STROKE_WIDTH = 1f

    fun drawRoundedBackground(
        g: Graphics,
        component: JComponent,
        selected: Boolean = true
    ) {
        val g2 = g.create() as Graphics2D
        try {
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            )

            val rect = createRoundedRectangle(component)

            g2.color = service<EditorColorsManager>().globalScheme.defaultBackground
            g2.fill(rect)

            if (!selected) {
                g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)
            }

            drawBorder(g2, rect, selected)
        } finally {
            g2.dispose()
        }
    }

    private fun createRoundedRectangle(component: JComponent): RoundRectangle2D.Float {
        return RoundRectangle2D.Float(
            STROKE_WIDTH / 2f,
            STROKE_WIDTH / 2f,
            component.width.toFloat() - STROKE_WIDTH,
            component.height.toFloat() - STROKE_WIDTH,
            ARC_WIDTH,
            ARC_HEIGHT
        )
    }

    private fun drawBorder(g2: Graphics2D, rect: RoundRectangle2D.Float, selected: Boolean) {
        g2.color = JBUI.CurrentTheme.Focus.defaultButtonColor()
        g2.stroke = if (selected) {
            BasicStroke(STROKE_WIDTH)
        } else {
            BasicStroke(
                STROKE_WIDTH,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0f,
                floatArrayOf(10f, 5f),
                0f
            )
        }
        g2.draw(rect)
    }
}
