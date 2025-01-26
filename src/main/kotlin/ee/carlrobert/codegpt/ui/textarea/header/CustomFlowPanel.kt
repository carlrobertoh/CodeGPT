package ee.carlrobert.codegpt.ui.textarea.header

import com.intellij.util.ui.JBUI
import java.awt.Container
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JPanel

class CustomFlowLayout(private val spacing: Int = 4) : FlowLayout(LEFT, 0, 0) {
    override fun layoutContainer(target: Container) {
        synchronized(target.treeLock) {
            val insets = target.insets
            var x = insets.left
            val y = insets.top

            val visibleComponents = target.components.filter { it.isVisible }
            visibleComponents.forEachIndexed { index, component ->
                val dim = component.preferredSize
                component.setBounds(x, y, dim.width, dim.height)
                x += dim.width

                if (index < visibleComponents.size - 1) {
                    x += spacing
                }
            }
        }
    }
}

const val DEFAULT_SPACING = 4

class CustomFlowPanel : JPanel(CustomFlowLayout(DEFAULT_SPACING)) {

    init {
        isOpaque = false
        border = JBUI.Borders.empty()
    }

    override fun getPreferredSize(): Dimension {
        var width = 0
        var height = 0
        var visibleCount = 0

        for (component in components) {
            if (component.isVisible) {
                width += component.preferredSize.width
                height = maxOf(height, component.preferredSize.height)
                visibleCount++
            }
        }

        if (visibleCount > 1) {
            // gaps between multiple visible components
            width += DEFAULT_SPACING * (visibleCount - 1)
        }

        return Dimension(width, height)
    }

    override fun getMinimumSize(): Dimension = preferredSize
    override fun getMaximumSize(): Dimension = preferredSize
}