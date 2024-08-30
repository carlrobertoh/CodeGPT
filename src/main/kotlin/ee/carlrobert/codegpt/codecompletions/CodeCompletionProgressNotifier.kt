package ee.carlrobert.codegpt.codecompletions

import com.intellij.util.messages.Topic

interface CodeCompletionProgressNotifier {

    fun loading(loading: Boolean)

    companion object {
        @JvmStatic
        val CODE_COMPLETION_PROGRESS_TOPIC =
            Topic.create("codeCompletionProgressTopic", CodeCompletionProgressNotifier::class.java)
    }
}