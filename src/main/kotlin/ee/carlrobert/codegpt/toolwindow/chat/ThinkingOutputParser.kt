package ee.carlrobert.codegpt.toolwindow.chat

import java.util.regex.Pattern

class ThinkingOutputParser {

    private val buffer = StringBuilder()

    var isThinking: Boolean = false
        private set

    var isFinished: Boolean = false
        private set

    var thoughtProcess: String = ""
        private set

    fun processChunk(chunk: String) {
        if (isFinished) {
            return
        }

        buffer.append(chunk)

        val thinkPattern = Pattern.compile("<think>(.*?)</think>", Pattern.DOTALL)
        val matcher = thinkPattern.matcher(buffer.toString())
        if (matcher.find()) {
            isFinished = true
            isThinking = false
            thoughtProcess = matcher.group(1).trim { it <= ' ' }
        } else if (buffer.isNotBlank() && "<think>".contains(buffer)) {
            thoughtProcess = ""
            isThinking = true
        } else if (buffer.toString().startsWith("<think>")) {
            thoughtProcess = buffer.toString().replaceFirst("<think>".toRegex(), "")
            isThinking = true
        }
    }
}