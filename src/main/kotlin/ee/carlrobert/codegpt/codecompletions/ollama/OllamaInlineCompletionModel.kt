package ee.carlrobert.codegpt.codecompletions.ollama

import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate

enum class OllamaInlineCompletionModel(val identifier: String, val fimTemplate: InfillPromptTemplate) {
    LLAMA("llama", InfillPromptTemplate.CODE_LLAMA),
    DEEPSEEK_CODER("deepseek-coder", InfillPromptTemplate.DEEPSEEK_CODER),
    STABILITY_AI("stable", InfillPromptTemplate.STABILITY),
}
