package testsupport.mixin

import com.intellij.testFramework.PlatformTestUtil
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_OPENAI_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.OPENAI_API_KEY
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import java.util.function.BooleanSupplier

interface ShortcutsTestMixin {

  fun useAnthropicService() {
    GeneralSettings.getCurrentState().selectedService = ServiceType.ANTHROPIC
  }

  fun useCustomOpenAIService() {
    GeneralSettings.getCurrentState().selectedService = ServiceType.CUSTOM_OPENAI
  }

  fun useOpenAIService() {
    useOpenAIService("gpt-4")
  }

  fun useOpenAIService(model: String? = "gpt-4") {
    GeneralSettings.getCurrentState().selectedService = ServiceType.OPENAI
    setCredential(OPENAI_API_KEY, "TEST_API_KEY")
    OpenAISettings.getCurrentState().model = model
  }

  fun useAzureService() {
    GeneralSettings.getCurrentState().selectedService = ServiceType.AZURE
    setCredential(AZURE_OPENAI_API_KEY, "TEST_API_KEY")
    val azureSettings = AzureSettings.getCurrentState()
    azureSettings.resourceName = "TEST_RESOURCE_NAME"
    azureSettings.apiVersion = "TEST_API_VERSION"
    azureSettings.deploymentId = "TEST_DEPLOYMENT_ID"
  }

  fun useYouService() {
    GeneralSettings.getCurrentState().selectedService = ServiceType.YOU
  }

  fun useLlamaService() {
    GeneralSettings.getCurrentState().selectedService = ServiceType.LLAMA_CPP
    LlamaSettings.getCurrentState().serverPort = null
  }

  fun waitExpecting(condition: BooleanSupplier?) {
    PlatformTestUtil.waitWithEventsDispatching(
      "Waiting for message response timed out or did not meet expected conditions",
      condition!!,
      5
    )
  }

}
