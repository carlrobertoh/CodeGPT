package ee.carlrobert.codegpt.completions

import ai.koog.prompt.message.Message

internal object CompletionTextExtractor {
    fun extract(responses: List<Message.Assistant>): String {
        val parts = responses.flatMap { it.parts }

        val reasoningText = parts
            .filterIsInstance<ai.koog.prompt.message.MessagePart.Reasoning>()
            .map { it.content.joinToString("").trim() }
            .filter { it.isNotEmpty() }
            .joinToString("\n")
            .trim()

        val assistantText = parts
            .filterIsInstance<ai.koog.prompt.message.MessagePart.Text>()
            .joinToString("\n") { it.text.trim() }
            .trim()

        if (reasoningText.isNotEmpty() && assistantText.isNotEmpty()) {
            return "<think>$reasoningText</think>\n$assistantText"
        }

        if (assistantText.isNotEmpty()) {
            return assistantText
        }

        if (reasoningText.isNotEmpty()) {
            return "<think>$reasoningText</think>"
        }

        return responses.joinToString("\n") { it.textContent().trim() }.trim()
    }
}
