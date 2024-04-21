package ee.carlrobert.codegpt.settings.service

import com.intellij.ide.DataManager
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ex.Settings
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.ActionLink
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import ee.carlrobert.codegpt.CodeGPTBundle
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceConfigurable
import java.util.*
import javax.swing.DefaultComboBoxModel
import javax.swing.JPanel

class ServiceConfigurableComponent {

    private var serviceComboBox: ComboBox<ServiceType> =
        ComboBox(DefaultComboBoxModel<ServiceType>().apply {
            addAll(Arrays.stream(ServiceType.entries.toTypedArray()).toList())
        }).apply {
            selectedItem = ServiceType.OPENAI // TODO
        }

    fun getSelectedService(): ServiceType {
        return serviceComboBox.item
    }

    fun setSelectedService(serviceType: ServiceType?) {
        serviceComboBox.selectedItem = serviceType
    }

    fun getPanel(): JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.label"),
            serviceComboBox
        )
        .addVerticalGap(8)
        .addComponent(JBLabel("All available services that can be used with CodeGPT:"))
        .addVerticalGap(8)
        .addComponent(FormBuilder.createFormBuilder()
            .setFormLeftIndent(16).apply {
                addLinks(this)
            }
            .panel)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    private fun addLinks(formBuilder: FormBuilder) {
        mapOf(
            "OpenAI" to OpenAIServiceConfigurable::class.java,
            "Custom OpenAI" to CustomServiceConfigurable::class.java,
            "Azure" to AzureServiceConfigurable::class.java,
            "Anthropic" to AnthropicServiceConfigurable::class.java,
            "You.com" to YouServiceConfigurable::class.java,
            "LLaMA C/C++ Port" to LlamaServiceConfigurable::class.java
        ).entries.forEach { (name, configurableClass) ->
            formBuilder.addComponent(ActionLink(name) {
                val context = service<DataManager>().getDataContext(it.source as ActionLink)
                val settings = Settings.KEY.getData(context)
                settings?.select(settings.find(configurableClass))
            })
        }
    }
}