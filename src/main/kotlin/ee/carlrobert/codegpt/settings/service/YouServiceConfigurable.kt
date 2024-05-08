package ee.carlrobert.codegpt.settings.service

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.Disposer
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.you.YouSettings
import ee.carlrobert.codegpt.settings.service.you.YouSettingsForm
import javax.swing.JComponent

class YouServiceConfigurable : Configurable {

    private var parentDisposable = Disposer.newDisposable()
    private lateinit var component: YouSettingsForm

    override fun getDisplayName(): String {
        return "CodeGPT: You.com Service"
    }

    override fun createComponent(): JComponent {
        parentDisposable = Disposer.newDisposable();
        component = YouSettingsForm(service<YouSettings>().state, parentDisposable)
        return component
    }

    override fun isModified(): Boolean {
        return component.getCurrentState() != service<YouSettings>().state
    }

    override fun apply() {
        service<GeneralSettings>().state.selectedService = ServiceType.YOU
        service<YouSettings>().loadState(component.currentState)
    }

    override fun disposeUIResources() {
        Disposer.dispose(parentDisposable)
    }

    override fun reset() {
        component.resetForm()
    }
}