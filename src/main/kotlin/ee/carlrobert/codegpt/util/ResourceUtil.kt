package ee.carlrobert.codegpt.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import ee.carlrobert.codegpt.settings.persona.PersonaDetails
import ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent

object ResourceUtil {

    fun getDefaultPersonas(): MutableList<PersonaDetails> {
        return ObjectMapper().readValue(
            getResourceContent("/prompts.json"),
            object : TypeReference<MutableList<PersonaDetails>>() {})
    }
}