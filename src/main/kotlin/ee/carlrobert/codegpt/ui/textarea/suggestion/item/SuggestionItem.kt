package ee.carlrobert.codegpt.ui.textarea.suggestion.item

import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.ui.textarea.CustomTextPane
import kotlinx.coroutines.Job
import javax.swing.Icon

interface SuggestionItem {
    val displayName: String
    val icon: Icon?
    val enabled: Boolean
        get() = true
}

interface SuggestionActionItem : SuggestionItem {
    fun execute(project: Project, textPane: CustomTextPane)
}

interface SuggestionGroupItem : SuggestionItem {
    val groupPrefix: String

    suspend fun getSuggestions(
        searchText: String? = null,
        updateSuggestionsJob: Job?
    ): List<SuggestionItem>
}