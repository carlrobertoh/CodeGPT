package ee.carlrobert.codegpt.settings.service.ollama

import ee.carlrobert.codegpt.codecompletions.ollama.OllamaInlineCompletionModel

data class OllamaSettingsState(
    val host: String = "http://localhost:11434",
    val model: String = "",
    val codeCompletionMaxTokens: Int = 128,
    val codeCompletionsEnabled: Boolean = true,
) {
    val fimTemplate = OllamaInlineCompletionModel.entries.firstOrNull { model.contains(it.identifier) }?.fimTemplate

    fun isCodeCompletionPossible(): Boolean {
        return fimTemplate != null && codeCompletionsEnabled
    }
}
