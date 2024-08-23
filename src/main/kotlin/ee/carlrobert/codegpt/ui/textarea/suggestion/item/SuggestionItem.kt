package ee.carlrobert.codegpt.ui.textarea.suggestion.item

import com.intellij.openapi.project.Project
import ee.carlrobert.codegpt.ui.textarea.PromptTextField
import javax.swing.Icon

interface SuggestionItem {
    val displayName: String
    val icon: Icon?
    val enabled: Boolean
        get() = true
}

interface SuggestionActionItem : SuggestionItem {
    suspend fun execute(project: Project, textPane: PromptTextField)
}

interface SuggestionGroupItem : SuggestionItem {
    suspend fun getSuggestions(searchText: String? = null): List<SuggestionItem>
}