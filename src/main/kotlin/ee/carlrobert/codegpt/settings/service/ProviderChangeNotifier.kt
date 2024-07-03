package ee.carlrobert.codegpt.settings.service

import com.intellij.util.messages.Topic

interface ProviderChangeNotifier {

    fun providerChanged(provider: ServiceType)

    companion object {
        @JvmStatic
        val PROVIDER_CHANGE_TOPIC =
            Topic.create("providerChange", ProviderChangeNotifier::class.java)
    }
}