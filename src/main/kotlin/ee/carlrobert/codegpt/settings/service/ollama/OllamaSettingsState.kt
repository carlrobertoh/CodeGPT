package ee.carlrobert.codegpt.settings.service.ollama

import ee.carlrobert.codegpt.codecompletions.ollama.OllamaInlineCompletionModel

data class OllamaSettingsState(
    var host: String = "http://localhost:11434",
    var model: String = "",
    var availableModels: List<String> = emptyList(),
    var codeCompletionMaxTokens: Int = 128,
    var codeCompletionsEnabled: Boolean = true,
) {
    val fimTemplate
        get() = OllamaInlineCompletionModel.entries.firstOrNull { model.contains(it.identifier) }?.fimTemplate

    fun isCodeCompletionPossible(): Boolean {
        return fimTemplate != null && codeCompletionsEnabled
    }
}
