package ee.carlrobert.codegpt.completions

import ee.carlrobert.codegpt.completions.CompletionRequestUtil.EDIT_CODE_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.CompletionRequestUtil.GENERATE_METHOD_NAMES_SYSTEM_PROMPT
import ee.carlrobert.codegpt.completions.factory.*
import ee.carlrobert.codegpt.settings.service.ServiceType
import ee.carlrobert.llm.completion.CompletionRequest

interface CompletionRequestFactory {
    fun createChatRequest(callParameters: CallParameters): CompletionRequest
    fun createEditCodeRequest(input: String): CompletionRequest
    fun createCommitMessageRequest(systemPrompt: String, gitDiff: String): CompletionRequest
    fun createLookupRequest(prompt: String): CompletionRequest

    companion object {
        @JvmStatic
        fun getFactory(serviceType: ServiceType): CompletionRequestFactory {
            return when (serviceType) {
                ServiceType.CODEGPT -> CodeGPTRequestFactory()
                ServiceType.OPENAI -> OpenAIRequestFactory()
                ServiceType.CUSTOM_OPENAI -> CustomOpenAIRequestFactory()
                ServiceType.AZURE -> AzureRequestFactory()
                ServiceType.ANTHROPIC -> ClaudeRequestFactory()
                ServiceType.GOOGLE -> GoogleRequestFactory()
                ServiceType.OLLAMA -> OllamaRequestFactory()
                ServiceType.LLAMA_CPP -> LlamaRequestFactory()
            }
        }
    }
}

abstract class BaseRequestFactory : CompletionRequestFactory {
    override fun createEditCodeRequest(input: String): CompletionRequest {
        return createBasicCompletionRequest(EDIT_CODE_SYSTEM_PROMPT, input, true)
    }

    override fun createCommitMessageRequest(
        systemPrompt: String,
        gitDiff: String
    ): CompletionRequest {
        return createBasicCompletionRequest(systemPrompt, gitDiff, true)
    }

    override fun createLookupRequest(prompt: String): CompletionRequest {
        return createBasicCompletionRequest(GENERATE_METHOD_NAMES_SYSTEM_PROMPT, prompt)
    }

    abstract fun createBasicCompletionRequest(
        systemPrompt: String,
        userPrompt: String,
        stream: Boolean = false
    ): CompletionRequest
}
