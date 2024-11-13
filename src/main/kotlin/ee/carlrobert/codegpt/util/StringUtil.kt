package ee.carlrobert.codegpt.util

import ai.grazie.nlp.utils.takeWhitespaces
import com.intellij.util.diff.Diff

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

    fun findCompletionParts(
        editorLineSuffix: String,
        completionLine: String
    ): List<Pair<String, Int>> {
        val nonOverlappingPart = findNonOverlappingPart(editorLineSuffix, completionLine)
        if (nonOverlappingPart.length == completionLine.length) {
            return listOf(Pair(completionLine, 0))
        }

        val result = ArrayList<Pair<String, Int>>()
        val editorChars: IntArray = editorLineSuffix.chars().toArray()
        val completionChars: IntArray = completionLine.chars().toArray()
        val changes: List<Diff.Change> =
            Diff.buildChanges(editorChars, completionChars)?.toList() ?: emptyList()
        for (change in changes) {
            val part = completionLine.substring(change.line1, change.line1 + change.inserted)
            result.add(Pair(part, change.line0))
        }

        return result
    }

    private fun findNonOverlappingPart(
        editorLineSuffix: String,
        completionLine: String
    ): String {
        var i = editorLineSuffix.length - 1
        var j = completionLine.length - 1
        while (i >= 0 && j >= 0 && editorLineSuffix[i] == completionLine[j]) {
            i--
            j--
        }

        if (j >= 0) {
            return completionLine.substring(0, j + 1)
        }

        return ""
    }
}
