package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.IncludedFilesSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import ee.carlrobert.codegpt.telemetry.core.configuration.TelemetryConfiguration;
import ee.carlrobert.codegpt.telemetry.core.service.UserId;
import ee.carlrobert.embedding.EmbeddingsService;
import ee.carlrobert.embedding.ReferencedFile;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import ee.carlrobert.llm.client.openai.completion.OpenAICompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.you.completion.YouCompletionRequest;
import ee.carlrobert.llm.client.you.completion.YouCompletionRequestMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class CompletionRequestProvider {

  private static final Logger LOG = Logger.getInstance(CompletionRequestProvider.class);

  public static final String COMPLETION_SYSTEM_PROMPT = getResourceContent(
      "/prompts/default-completion-system-prompt.txt");

  public static final String GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT = getResourceContent(
      "/prompts/generate-commit-message-system-prompt.txt");

  public static final String FIX_COMPILE_ERRORS_SYSTEM_PROMPT = getResourceContent(
      "/prompts/fix-compile-errors.txt");

  public static final String INLINE_COMPLETION_PROMPT = getResourceContent(
      "/prompts/inline-completion-prompt.txt");

  private final EncodingManager encodingManager = EncodingManager.getInstance();
  private final EmbeddingsService embeddingsService;
  private final Conversation conversation;

  public CompletionRequestProvider(Conversation conversation) {
    this.embeddingsService = new EmbeddingsService(
        CompletionClientProvider.getOpenAIClient(),
        CodeGPTPlugin.getPluginBasePath());
    this.conversation = conversation;
  }

  public static String getPromptWithContext(List<ReferencedFile> referencedFiles,
      String userPrompt) {
    var includedFilesSettings = IncludedFilesSettingsState.getInstance();
    var repeatableContext = referencedFiles.stream()
        .map(item -> includedFilesSettings.getRepeatableContext()
            .replace("{FILE_PATH}", item.getFilePath())
            .replace("{FILE_CONTENT}", format(
                "```%s\n%s\n```",
                item.getFileExtension(),
                item.getFileContent().trim())))
        .collect(joining("\n\n"));

    return includedFilesSettings.getPromptTemplate()
        .replace("{REPEATABLE_CONTEXT}", repeatableContext)
        .replace("{QUESTION}", userPrompt);
  }

  public static OpenAICompletionRequest buildOpenAILookupCompletionRequest(
      String context) {
    return new OpenAIChatCompletionRequest.Builder(
        List.of(
            new OpenAIChatCompletionMessage("system",
                getResourceContent("/prompts/method-name-generator.txt")),
            new OpenAIChatCompletionMessage("user", context)))
        .setModel(OpenAISettingsState.getInstance().getModel())
        .setStream(false)
        .build();
  }

  public static LlamaCompletionRequest buildLlamaLookupCompletionRequest(String context) {
    return new LlamaCompletionRequest.Builder(PromptTemplate.LLAMA
        .buildPrompt(getResourceContent("/prompts/method-name-generator.txt"), context, List.of()))
        .setStream(false)
        .build();
  }

  public LlamaCompletionRequest buildLlamaCompletionRequest(
      Message message,
      ConversationType conversationType) {
    var settings = LlamaSettingsState.getInstance();
    var promptTemplate = settings.isUseCustomModel()
        ? settings.getPromptTemplate()
        : LlamaModel.findByHuggingFaceModel(settings.getHuggingFaceModel()).getPromptTemplate();

    var systemPrompt = COMPLETION_SYSTEM_PROMPT;
    if (conversationType == ConversationType.FIX_COMPILE_ERRORS) {
      systemPrompt = FIX_COMPILE_ERRORS_SYSTEM_PROMPT;
    }

    var prompt = promptTemplate.buildPrompt(
        systemPrompt,
        message.getPrompt(),
        conversation.getMessages());
    var configuration = ConfigurationState.getInstance();
    return new LlamaCompletionRequest.Builder(prompt)
        .setN_predict(configuration.getMaxTokens())
        .setTemperature(configuration.getTemperature())
        .setTop_k(settings.getTopK())
        .setTop_p(settings.getTopP())
        .setMin_p(settings.getMinP())
        .setRepeat_penalty(settings.getRepeatPenalty())
        .build();
  }

  public YouCompletionRequest buildYouCompletionRequest(Message message) {
    var requestBuilder = new YouCompletionRequest.Builder(message.getPrompt())
        .setUseGPT4Model(YouSettingsState.getInstance().isUseGPT4Model())
        .setChatHistory(conversation.getMessages().stream()
            .map(prevMessage -> new YouCompletionRequestMessage(
                prevMessage.getPrompt(),
                prevMessage.getResponse()))
            .collect(toList()));
    if (TelemetryConfiguration.getInstance().isEnabled()
        && !ApplicationManager.getApplication().isUnitTestMode()) {
      requestBuilder.setUserId(UUID.fromString(UserId.INSTANCE.get()));
    }
    return requestBuilder.build();
  }

  public OpenAIChatCompletionRequest buildOpenAIChatCompletionRequest(
      @Nullable String model,
      CallParameters callParameters,
      boolean useContextualSearch,
      @Nullable String overriddenPath) {
    var builder = new OpenAIChatCompletionRequest.Builder(
        buildMessages(model, callParameters, useContextualSearch))
        .setModel(model)
        .setMaxTokens(ConfigurationState.getInstance().getMaxTokens())
        .setTemperature(ConfigurationState.getInstance().getTemperature());

    if (overriddenPath != null) {
      builder.setOverriddenPath(overriddenPath);
    }

    return (OpenAIChatCompletionRequest) builder.build();
  }

  public List<OpenAIChatCompletionMessage> buildMessages(
      CallParameters callParameters,
      boolean useContextualSearch) {
    var message = callParameters.getMessage();
    var messages = new ArrayList<OpenAIChatCompletionMessage>();
    if (useContextualSearch) {
      var prompt = embeddingsService.buildPromptWithContext(
          message.getPrompt());
      LOG.info("Retrieved context:\n" + prompt);
      messages.add(new OpenAIChatCompletionMessage("user", prompt));
    } else {
      if (callParameters.getConversationType() == ConversationType.DEFAULT) {
        messages.add(new OpenAIChatCompletionMessage(
            "system",
            ConfigurationState.getInstance().getSystemPrompt()));
      }
      if (callParameters.getConversationType() == ConversationType.FIX_COMPILE_ERRORS) {
        messages.add(new OpenAIChatCompletionMessage("system", FIX_COMPILE_ERRORS_SYSTEM_PROMPT));
      }

      for (var prevMessage : conversation.getMessages()) {
        if (callParameters.isRetry() && prevMessage.getId().equals(message.getId())) {
          break;
        }
        messages.add(new OpenAIChatCompletionMessage("user", prevMessage.getPrompt()));
        messages.add(new OpenAIChatCompletionMessage("assistant", prevMessage.getResponse()));
      }
      messages.add(new OpenAIChatCompletionMessage("user", message.getPrompt()));
    }
    return messages;
  }

  private List<OpenAIChatCompletionMessage> buildMessages(
      @Nullable String model,
      CallParameters callParameters,
      boolean useContextualSearch) {
    var messages = buildMessages(callParameters, useContextualSearch);

    if (model == null || SettingsState.getInstance().getSelectedService() == ServiceType.YOU) {
      return messages;
    }

    int totalUsage = messages.parallelStream()
        .mapToInt(encodingManager::countMessageTokens)
        .sum() + ConfigurationState.getInstance().getMaxTokens();
    int modelMaxTokens;
    try {
      modelMaxTokens = OpenAIChatCompletionModel.findByCode(model).getMaxTokens();

      if (totalUsage <= modelMaxTokens) {
        return messages;
      }
    } catch (NoSuchElementException ex) {
      return messages;
    }
    return tryReducingMessagesOrThrow(messages, totalUsage, modelMaxTokens);
  }

  private List<OpenAIChatCompletionMessage> tryReducingMessagesOrThrow(
      List<OpenAIChatCompletionMessage> messages,
      int totalUsage,
      int modelMaxTokens) {
    if (!ConversationsState.getInstance().discardAllTokenLimits) {
      if (!conversation.isDiscardTokenLimit()) {
        throw new TotalUsageExceededException();
      }
    }

    // skip the system prompt
    for (int i = 1; i < messages.size(); i++) {
      if (totalUsage <= modelMaxTokens) {
        break;
      }

      totalUsage -= encodingManager.countMessageTokens(messages.get(i));
      messages.set(i, null);
    }

    return messages.stream().filter(Objects::nonNull).collect(toList());
  }
}
