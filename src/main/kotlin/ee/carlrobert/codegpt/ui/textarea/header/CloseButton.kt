package ee.carlrobert.codegpt.ui.textarea.header

import com.intellij.icons.AllIcons
import com.intellij.icons.AllIcons.Actions.Close
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import javax.swing.JButton

class CloseButton(onClose: () -> Unit) : JButton(Close) {
    init {
        addActionListener {
            onClose()
        }

        preferredSize = Dimension(Close.iconWidth, Close.iconHeight)
        border = JBUI.Borders.emptyLeft(4)
        isContentAreaFilled = false
        toolTipText = "Remove"
        rolloverIcon = AllIcons.Actions.CloseHovered
    }
}