package testsupport.mixin

import com.intellij.openapi.components.service
import com.intellij.testFramework.PlatformTestUtil
import ee.carlrobert.codegpt.completions.HuggingFaceModel
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.*
import ee.carlrobert.codegpt.credentials.CredentialsStore.setCredential
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.google.GoogleSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.llm.client.google.models.GoogleModel
import java.util.function.BooleanSupplier

interface ShortcutsTestMixin {

  fun useCodeGPTService() {
    GeneralSettings.getCurrentState().selectedService = ServiceType.CODEGPT
    setCredential(CODEGPT_API_KEY, "TEST_API_KEY")
    service<CodeGPTServiceSettings>().state.chatCompletionSettings.model = "TEST_MODEL"
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

  fun useLlamaService(codeCompletionsEnabled: Boolean = false) {
    GeneralSettings.getCurrentState().selectedService = ServiceType.LLAMA_CPP
    LlamaSettings.getCurrentState().serverPort = null
    LlamaSettings.getCurrentState().isCodeCompletionsEnabled = codeCompletionsEnabled
  }

  fun useOllamaService() {
    GeneralSettings.getCurrentState().selectedService = ServiceType.OLLAMA
    setCredential(OLLAMA_API_KEY, "TEST_API_KEY")
    service<OllamaSettings>().state.apply {
      model = HuggingFaceModel.LLAMA_3_8B_Q6_K.code
      host = null
    }
  }

  fun useGoogleService() {
    GeneralSettings.getCurrentState().selectedService = ServiceType.GOOGLE
    setCredential(GOOGLE_API_KEY, "TEST_API_KEY")
    service<GoogleSettings>().state.model = GoogleModel.GEMINI_PRO.code
  }

  fun waitExpecting(condition: BooleanSupplier?) {
    PlatformTestUtil.waitWithEventsDispatching(
      "Waiting for message response timed out or did not meet expected conditions",
      condition!!,
      5
    )
  }
}
