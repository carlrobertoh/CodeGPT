package ee.carlrobert.codegpt.settings.service.ollama

import ee.carlrobert.codegpt.codecompletions.ollama.OllamaInlineCompletionModel

data class OllamaSettingsState(
    var host: String = "http://localhost:11437",
    var model: String = "",
    var codeCompletionMaxTokens: Int = 128,
    var codeCompletionsEnabled: Boolean = true,
) {
    val codeCompletionSupported = OllamaInlineCompletionModel.entries.any { model.contains(it.identifier) }

    fun isCodeCompletionEnabled(): Boolean {
        return codeCompletionSupported && codeCompletionsEnabled
    }
}
