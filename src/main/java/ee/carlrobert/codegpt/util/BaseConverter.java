package ee.carlrobert.codegpt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseConverter<T> extends Converter<T> {

  private final TypeReference<T> typeReference;
  private final ObjectMapper objectMapper = new ObjectMapper()
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule());

  public BaseConverter(TypeReference<T> typeReference) {
    this.typeReference = typeReference;
  }

  public @Nullable T fromString(@NotNull String value) {
    try {
      return objectMapper.readValue(value, typeReference);
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
