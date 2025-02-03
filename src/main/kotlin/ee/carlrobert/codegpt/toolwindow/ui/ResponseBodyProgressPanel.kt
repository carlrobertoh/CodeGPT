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
import ee.carlrobert.codegpt.util.MarkdownUtil.convertMdToHtml
import java.awt.FlowLayout
import javax.swing.*

class ResponseBodyProgressPanel : JPanel() {

    companion object {
        private val logger = thisLogger()
    }

    private val processSpinner = AsyncProcessIcon("process_spinner")

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = JBUI.Borders.empty(4, 0)
        isVisible = false
    }

    fun updateProgressContainer(text: String, icon: Icon?) {
        runInEdt {
            isVisible = true

            removeAll()
            val wrapper = if (icon != null) {
                JBLabel(
                    "<html>" + convertMdToHtml(text) + "</html>",
                    icon,
                    SwingConstants.LEADING
                ).apply {
                    horizontalAlignment = SwingConstants.LEFT
                    horizontalTextPosition = SwingConstants.RIGHT
                }
            } else {
                JPanel(FlowLayout(FlowLayout.LEADING, 0, 0)).apply {
                    add(JBLabel("<html>" + convertMdToHtml(text) + "</html>"))
                    add(Box.createHorizontalStrut(4))
                    add(processSpinner)
                }
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
