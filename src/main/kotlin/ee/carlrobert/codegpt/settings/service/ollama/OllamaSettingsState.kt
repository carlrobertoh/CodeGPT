package ee.carlrobert.codegpt.settings.service.ollama

import ee.carlrobert.codegpt.codecompletions.ollama.OllamaInlineCompletionModel

data class OllamaSettingsState(
    var host: String = "http://localhost:11437",
    var model: String = "",
    var codeCompletionMaxTokens: Int = 128,
    var codeCompletionsEnabled: Boolean = true,
) {
    val fimTemplate = OllamaInlineCompletionModel.entries.firstOrNull { model.contains(it.identifier) }?.fimTemplate

    fun isCodeCompletionEnabled(): Boolean {
        return fimTemplate != null && codeCompletionsEnabled
    }
}
