package ee.carlrobert.codegpt.conversations.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class BaseConverter<T> extends Converter<T> {

  private final Class<T> clazz;
  private final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule());

  BaseConverter(Class<T> clazz) {
    this.clazz = clazz;
  }

  public @Nullable T fromString(@NotNull String value) {
    try {
      return objectMapper.readValue(value, clazz);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to deserialize conversations", e);
    }
  }

  public @Nullable String toString(@NotNull T value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to serialize conversations", e);
    }
  }
}
