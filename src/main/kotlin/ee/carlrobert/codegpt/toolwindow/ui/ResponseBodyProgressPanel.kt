package ee.carlrobert.codegpt.toolwindow.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.AsyncProcessIcon
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.Icons
import ee.carlrobert.codegpt.events.EventDetails
import ee.carlrobert.codegpt.events.ProcessContextEventDetails
import java.awt.FlowLayout
import javax.swing.*

class ResponseBodyProgressPanel : JPanel() {

    companion object {
        private val logger = thisLogger()
    }

    private val processSpinner = AsyncProcessIcon("process_spinner")

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = JBUI.Borders.emptyBottom(8)
    }

    fun updateProgressContainer(text: String, icon: Icon?) {
        runInEdt {
            removeAll()
            val wrapper: JComponent
            if (icon != null) {
                wrapper = JBLabel(text, icon, SwingConstants.LEADING)
                wrapper.horizontalTextPosition = SwingConstants.LEADING
            } else {
                wrapper = JPanel(FlowLayout(FlowLayout.LEADING, 0, 0))
                wrapper.add(JBLabel(text))
                wrapper.add(Box.createHorizontalStrut(4))
                wrapper.add(processSpinner)
            }
            add(JBUI.Panels.simplePanel(wrapper))
            revalidate()
            repaint()
        }
    }

    fun updateProgressDetails(eventDetails: EventDetails?) {
        if (eventDetails == null) {
            logger.error("No event details provided")
            return
        }

        if (eventDetails is ProcessContextEventDetails) {
            when (eventDetails.status) {
                "STARTED" -> {
                    updateProgressContainer(eventDetails.description, null)
                }

                "FAILED" -> {
                    updateProgressContainer(eventDetails.description, AllIcons.General.Error)
                }

                "COMPLETED" -> {
                    updateProgressContainer(eventDetails.description, Icons.GreenCheckmark)
                }

                else -> {}
            }
        }
    }
}
