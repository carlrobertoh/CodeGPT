package ee.carlrobert.codegpt.ui.textarea.suggestion

import com.intellij.openapi.components.service
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.UnscaledGaps
import com.intellij.util.ui.JBUI
import ee.carlrobert.codegpt.CodeGPTBundle
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.ScrollPaneConstants

class SuggestionsPopupBuilder {

    private var preferableFocusComponent: JComponent? = null
    private var onCancel: () -> Boolean = { true }

    fun setPreferableFocusComponent(preferableFocusComponent: JComponent?): SuggestionsPopupBuilder {
        this.preferableFocusComponent = preferableFocusComponent
        return this
    }

    fun setOnCancel(onCancel: () -> Boolean): SuggestionsPopupBuilder {
        this.onCancel = onCancel
        return this
    }

    fun build(list: SuggestionList): JBPopup {
        val scrollPane = JBScrollPane(list).apply {
            border = JBUI.Borders.empty()
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        }
        val popupPanel = panel {
            row { cell(scrollPane).customize(UnscaledGaps.EMPTY) }
            separator()
            row {
                text(CodeGPTBundle.get("shared.escToCancel"))
                    .customize(UnscaledGaps(left = 4))
                    .applyToComponent {
                        font = JBUI.Fonts.smallFont()
                    }
            }
        }

        return service<JBPopupFactory>()
            .createComponentPopupBuilder(popupPanel, preferableFocusComponent)
            .setMovable(true)
            .setCancelOnClickOutside(false)
            .setCancelOnWindowDeactivation(true)
            .setRequestFocus(true)
            .setMinSize(Dimension(480, 30))
            .setCancelCallback(onCancel)
            .setResizable(true)
            .createPopup()
    }
}