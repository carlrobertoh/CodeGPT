package ee.carlrobert.codegpt.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.intellij.util.xmlb.Converter

abstract class BaseConverter<T : Any> protected constructor(private val typeReference: TypeReference<T>) : Converter<T>() {
  private val objectMapper: ObjectMapper = ObjectMapper()
    .registerModule(Jdk8Module())
    .registerModule(JavaTimeModule())

  override fun fromString(value: String): T? {
    try {
      return objectMapper.readValue(value, typeReference)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Unable to deserialize conversations", e)
    }
  }

  override fun toString(value: T): String? {
    try {
      return objectMapper.writeValueAsString(value)
    } catch (e: JsonProcessingException) {
      throw RuntimeException("Unable to serialize conversations", e)
    }
  }
}
