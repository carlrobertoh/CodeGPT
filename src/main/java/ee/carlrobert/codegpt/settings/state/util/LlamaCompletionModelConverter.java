package ee.carlrobert.codegpt.settings.state.util;

import com.intellij.util.xmlb.Converter;
import ee.carlrobert.codegpt.completions.llama.CustomLlamaModel;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LlamaCompletionModelConverter extends Converter<LlamaCompletionModel> {

  public static final String CUSTOM_MODEL_PREFIX = "custom:";

  @Override
  public @Nullable LlamaCompletionModel fromString(@NotNull String value) {
    if (value.startsWith(CUSTOM_MODEL_PREFIX)) {
      return new CustomLlamaModel(value.replace(CUSTOM_MODEL_PREFIX, ""));
    }
    return HuggingFaceModel.valueOf(value);
  }

  @Override
  public @Nullable String toString(@NotNull LlamaCompletionModel value) {
    if (value instanceof HuggingFaceModel) {
      return ((HuggingFaceModel) value).name();
    } else if (value instanceof CustomLlamaModel) {
      return CUSTOM_MODEL_PREFIX + ((CustomLlamaModel) value).getModelPath();
    }
    throw new IllegalArgumentException(String.format("No conversion available for %s",
        value.getClass().getName()));
  }
}
