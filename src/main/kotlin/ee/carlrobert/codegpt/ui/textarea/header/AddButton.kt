package ee.carlrobert.codegpt.ui.textarea.header

import com.intellij.icons.AllIcons
import com.intellij.util.IconUtil
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JButton

class AddButton(onAdd: () -> Unit) : JButton() {
    init {
        addActionListener {
            onAdd()
        }

        cursor = Cursor(Cursor.HAND_CURSOR)
        preferredSize = Dimension(20, 20)
        isContentAreaFilled = false
        isOpaque = false
        border = null
        toolTipText = "Add Context"
        icon = IconUtil.scale(AllIcons.General.InlineAdd, null, 0.75f)
        rolloverIcon = IconUtil.scale(AllIcons.General.InlineAddHover, null, 0.75f)
        pressedIcon = IconUtil.scale(AllIcons.General.InlineAddHover, null, 0.75f)
    }

    override fun paintComponent(g: Graphics) {
        PaintUtil.drawRoundedBackground(g, this, true)
        super.paintComponent(g)
    }
}