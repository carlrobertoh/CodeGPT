package ee.carlrobert.codegpt.util

import com.fasterxml.jackson.core.type.TypeReference

class MapConverter : BaseConverter<Map<String, Any>>(object : TypeReference<Map<String, Any>>() {})
