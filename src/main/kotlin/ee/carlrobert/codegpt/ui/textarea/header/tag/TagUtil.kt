package ee.carlrobert.codegpt.ui.textarea.header.tag

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowContentManager

object TagUtil {
    fun <T : TagDetails> isTagTypePresent(
        project: Project,
        tagClass: Class<T>
    ): Boolean {
        return project.service<ChatToolWindowContentManager>()
            .tryFindActiveChatTabPanel()
            .map { it.selectedTags }
            .orElse(emptyList())
            .any { tagClass.isInstance(it) }
    }

    fun <T : TagDetails> getExistingTags(
        project: Project,
        tagClass: Class<T>
    ): List<T> {
        return project.service<ChatToolWindowContentManager>()
            .tryFindActiveChatTabPanel()
            .map { it.selectedTags }
            .orElse(emptyList())
            .filterIsInstance(tagClass)
    }
}