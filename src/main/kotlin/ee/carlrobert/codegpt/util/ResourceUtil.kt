package ee.carlrobert.codegpt.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import ee.carlrobert.codegpt.settings.persona.PersonaDetails
import ee.carlrobert.codegpt.ui.textarea.DefaultAction
import ee.carlrobert.codegpt.ui.textarea.SuggestionItem
import ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent

object ResourceUtil {

    fun getPrompts(filterPredicate: ((PersonaDetails) -> Boolean)? = null): List<SuggestionItem> {
        var prompts = getPrompts()
        if (filterPredicate != null) {
            prompts = prompts.filter(filterPredicate).toMutableList()
        }
        return prompts
            .map { SuggestionItem.PersonaItem(it) }
            .take(10) + listOf(SuggestionItem.ActionItem(DefaultAction.CREATE_NEW_PERSONA))
    }

    fun getPrompts(): MutableList<PersonaDetails> {
        return ObjectMapper().readValue(
            getResourceContent("/prompts.json"),
            object : TypeReference<MutableList<PersonaDetails>>() {})
    }
}