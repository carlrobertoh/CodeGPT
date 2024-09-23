package ee.carlrobert.codegpt.settings.service

import com.intellij.util.messages.Topic

interface ProviderChangeNotifier {

    fun providerChanged(provider: ServiceType)

    companion object {
        @JvmStatic
        val TOPIC = Topic.create("providerChange", ProviderChangeNotifier::class.java)
    }
}