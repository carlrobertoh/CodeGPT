package ee.carlrobert.codegpt.settings.state.util;

import com.intellij.util.xmlb.Converter;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenAiModelConverter extends Converter<OpenAIChatCompletionModel> {

  @Override
  public @Nullable OpenAIChatCompletionModel fromString(@NotNull String value) {
    return OpenAIChatCompletionModel.valueOf(value);
  }

  @Override
  public @Nullable String toString(@NotNull OpenAIChatCompletionModel value) {
    return value.name();
  }
}
