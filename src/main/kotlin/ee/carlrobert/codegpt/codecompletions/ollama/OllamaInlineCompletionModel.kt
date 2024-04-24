package ee.carlrobert.codegpt.codecompletions.ollama

import ee.carlrobert.codegpt.codecompletions.InfillPromptTemplate

// This should only be necessary until https://github.com/ollama/ollama/issues/3869 is fixed
enum class OllamaInlineCompletionModel(val identifier: String, val fimTemplate: InfillPromptTemplate) {
    LLAMA("llama", InfillPromptTemplate.LLAMA),
    DEEPSEEK_CODER("deepseek-coder", InfillPromptTemplate.DEEPSEEK_CODER),
    STABILITY_AI("stable", InfillPromptTemplate.STABILITY),
}
