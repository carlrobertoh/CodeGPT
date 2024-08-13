package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel.GPT_4_O;
import static ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel.GPT_4_O_MINI;
import static ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel.GPT_4_VISION_PREVIEW;

import com.intellij.openapi.application.ApplicationManager;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTServiceSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ServiceType {
  CODEGPT("CODEGPT", "service.codegpt.title", "codegpt.chat.completion"),
  OPENAI("OPENAI", "service.openai.title", "chat.completion"),
  CUSTOM_OPENAI("CUSTOM_OPENAI", "service.custom.openai.title", "custom.openai.chat.completion"),
  ANTHROPIC("ANTHROPIC", "service.anthropic.title", "anthropic.chat.completion"),
  AZURE("AZURE", "service.azure.title", "azure.chat.completion"),
  GOOGLE("GOOGLE", "service.google.title", "google.chat.completion"),
  LLAMA_CPP("LLAMA_CPP", "service.llama.title", "llama.chat.completion"),
  OLLAMA("OLLAMA", "service.ollama.title", "ollama.chat.completion");

  private final String code;
  private final String label;
  private final String completionCode;

  private static final Map<String, ServiceType> CLIENT_CODE_MAP = new HashMap<>();

  static {
    for (ServiceType type : values()) {
      CLIENT_CODE_MAP.put(type.getCompletionCode(), type);
    }
  }

  ServiceType(String code, String messageKey, String completionCode) {
    this.code = code;
    this.label = CodeGPTBundle.get(messageKey);
    this.completionCode = completionCode;
  }

  public String getCode() {
    return code;
  }

  public String getLabel() {
    return label;
  }

  public String getCompletionCode() {
    return completionCode;
  }

  @Override
  public String toString() {
    return label;
  }

  public static ServiceType fromClientCode(String clientCode) {
    ServiceType serviceType = CLIENT_CODE_MAP.get(clientCode);
    if (serviceType == null) {
      throw new RuntimeException("Provided client code '" + clientCode + "' is not supported");
    }
    return serviceType;
  }

  public boolean isImageActionSupported() {
    return switch (this) {
      case ANTHROPIC:
      case OLLAMA:
        yield true;
      case CODEGPT:
        var codegptModel = ApplicationManager.getApplication()
            .getService(CodeGPTServiceSettings.class)
            .getState()
            .getChatCompletionSettings()
            .getModel();
        yield List.of(
            "gpt-4o",
            "gpt-4o-mini",
            "claude-3-opus",
            "claude-3.5-sonnet"
        ).contains(codegptModel);
      case OPENAI:
        var openaiModel = ApplicationManager.getApplication().getService(OpenAISettings.class)
            .getState()
            .getModel();
        yield List.of(
            GPT_4_VISION_PREVIEW.getCode(),
            GPT_4_O.getCode(),
            GPT_4_O_MINI.getCode()).contains(openaiModel);
      default:
        yield false;
    };
  }
}
