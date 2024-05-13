package ee.carlrobert.codegpt.codecompletions

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import ee.carlrobert.codegpt.codecompletions.CodeCompletionRequestFactory.buildCodeGPTRequest
import ee.carlrobert.codegpt.codecompletions.CodeCompletionRequestFactory.buildCustomRequest
import ee.carlrobert.codegpt.codecompletions.CodeCompletionRequestFactory.buildLlamaRequest
import ee.carlrobert.codegpt.codecompletions.CodeCompletionRequestFactory.buildOllamaRequest
import ee.carlrobert.codegpt.codecompletions.CodeCompletionRequestFactory.buildOpenAIRequest
import ee.carlrobert.codegpt.completions.CompletionClientProvider
import ee.carlrobert.codegpt.settings.GeneralSettings
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.codegpt.settings.service.ServiceType.*
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings
import ee.carlrobert.llm.client.openai.completion.OpenAITextCompletionEventSourceListener
import ee.carlrobert.llm.completion.CompletionEventListener
import okhttp3.sse.EventSource
import okhttp3.sse.EventSources.createFactory

@Service(Service.Level.PROJECT)
class CodeCompletionService {

    fun isCodeCompletionsEnabled(selectedService: ServiceType): Boolean =
        when (selectedService) {
            CODEGPT -> service<CodeGPTServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled
            OPENAI -> OpenAISettings.getCurrentState().isCodeCompletionsEnabled
            CUSTOM_OPENAI -> service<CustomServiceSettings>().state.codeCompletionSettings.codeCompletionsEnabled
            LLAMA_CPP -> LlamaSettings.isCodeCompletionsPossible()
            OLLAMA -> service<OllamaSettings>().state.codeCompletionsEnabled
            else -> false
        }

    fun getCodeCompletionAsync(
        requestDetails: InfillRequestDetails,
        eventListener: CompletionEventListener<String>
    ): EventSource =
        when (val selectedService = GeneralSettings.getSelectedService()) {
            CODEGPT -> CompletionClientProvider.getCodeGPTClient()
                .getCompletionAsync(buildCodeGPTRequest(requestDetails), eventListener)

            OPENAI -> CompletionClientProvider.getOpenAIClient()
                .getCompletionAsync(buildOpenAIRequest(requestDetails), eventListener)

            CUSTOM_OPENAI -> createFactory(
                CompletionClientProvider.getDefaultClientBuilder().build()
            ).newEventSource(
                buildCustomRequest(requestDetails),
                OpenAITextCompletionEventSourceListener(eventListener)
            )

            OLLAMA -> CompletionClientProvider.getOllamaClient()
                .getCompletionAsync(buildOllamaRequest(requestDetails), eventListener)

            LLAMA_CPP -> CompletionClientProvider.getLlamaClient()
                .getChatCompletionAsync(buildLlamaRequest(requestDetails), eventListener)

            else -> throw IllegalArgumentException("Code completion not supported for ${selectedService.name}")
        }
}
