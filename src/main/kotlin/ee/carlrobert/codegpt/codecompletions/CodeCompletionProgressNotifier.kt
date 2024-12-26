package ee.carlrobert.codegpt.codecompletions

import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic

interface CodeCompletionProgressNotifier {

    fun loading(loading: Boolean)

    companion object {
        @JvmStatic
        val CODE_COMPLETION_PROGRESS_TOPIC =
            Topic.create("codeCompletionProgressTopic", CodeCompletionProgressNotifier::class.java)

        fun startLoading(project: Project) {
            handleLoading(project, true)
        }

        fun stopLoading(project: Project) {
            handleLoading(project, false)
        }

        private fun handleLoading(project: Project, loading: Boolean) {
            if (project.isDisposed) return
            project.messageBus
                .syncPublisher(CODE_COMPLETION_PROGRESS_TOPIC)
                ?.loading(loading)
        }
    }
}