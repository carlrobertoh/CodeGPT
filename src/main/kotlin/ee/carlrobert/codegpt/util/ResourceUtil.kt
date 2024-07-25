package ee.carlrobert.codegpt.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import ee.carlrobert.codegpt.settings.persona.PersonaDetails
import ee.carlrobert.codegpt.ui.textarea.DefaultAction
import ee.carlrobert.codegpt.ui.textarea.SuggestionItem
import ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent

object ResourceUtil {

    fun getFilteredPersonaSuggestions(
        filterPredicate: ((PersonaDetails) -> Boolean)? = null
    ): List<SuggestionItem> {
        var personaDetails = getFilteredPersonaSuggestions()
        if (filterPredicate != null) {
            personaDetails = personaDetails.filter(filterPredicate).toMutableList()
        }
        return personaDetails
            .map { SuggestionItem.PersonaItem(it) }
            .take(10) + listOf(SuggestionItem.ActionItem(DefaultAction.CREATE_NEW_PERSONA))
    }

    fun getFilteredPersonaSuggestions(): MutableList<PersonaDetails> {
        return ObjectMapper().readValue(
            getResourceContent("/prompts.json"),
            object : TypeReference<MutableList<PersonaDetails>>() {})
    }
}