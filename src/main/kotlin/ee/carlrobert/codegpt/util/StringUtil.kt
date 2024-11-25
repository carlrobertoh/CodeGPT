package ee.carlrobert.codegpt.util

import ai.grazie.nlp.utils.takeWhitespaces

object StringUtil {

    fun adjustWhitespace(
        completionLine: String,
        editorLine: String
    ): String {
        val editorWhitespaces = editorLine.takeWhitespaces()

        if (completionLine.isNotEmpty() && editorWhitespaces.isNotEmpty()) {
            if (completionLine.startsWith(editorWhitespaces)) {
                return completionLine.substring(editorWhitespaces.length)
            }
            if (editorLine.isBlank()) {
                val completionWhitespaces = completionLine.takeWhitespaces()
                return completionLine.substring(completionWhitespaces.length)
            }
        }

        return completionLine
    }

    fun String.extractUntilNewline(): String {
        val index = this.indexOf('\n')
        if (index == -1) {
            return this
        }
        return this.substring(0, index + 1)
    }
}
