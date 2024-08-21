package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.completions.ConversationType.FIX_COMPILE_ERRORS;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CUSTOM_SERVICE_API_KEY;
import static ee.carlrobert.codegpt.util.file.FileUtil.getResourceContent;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.ReferencedFile;
import ee.carlrobert.codegpt.completions.llama.LlamaModel;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.settings.IncludedFilesSettings;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.codegpt.settings.persona.PersonaSettings;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceChatCompletionSettingsState;
import ee.carlrobert.codegpt.settings.service.custom.CustomServiceSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.util.file.FileUtil;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeBase64Source;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionDetailedMessage;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionMessage;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionRequest;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeCompletionStandardMessage;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeMessageImageContent;
import ee.carlrobert.llm.client.anthropic.completion.ClaudeMessageTextContent;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionContent;
import ee.carlrobert.llm.client.google.completion.GoogleCompletionRequest;
import ee.carlrobert.llm.client.google.completion.GoogleContentPart;
import ee.carlrobert.llm.client.google.completion.GoogleContentPart.Blob;
import ee.carlrobert.llm.client.google.completion.GoogleGenerationConfig;
import ee.carlrobert.llm.client.google.models.GoogleModel;
import ee.carlrobert.llm.client.llama.completion.LlamaCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaChatCompletionMessage;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaChatCompletionRequest;
import ee.carlrobert.llm.client.ollama.completion.request.OllamaParameters;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionDetailedMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionRequest;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIImageUrl;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIMessageImageURLContent;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIMessageTextContent;
import ee.carlrobert.llm.client.openai.completion.request.RequestDocumentationDetails;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.Nullable;

public class CompletionRequestProvider {

  public static final String GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT =
      getResourceContent("/prompts/generate-commit-message.txt");

  public static final String FIX_COMPILE_ERRORS_SYSTEM_PROMPT =
      getResourceContent("/prompts/fix-compile-errors.txt");

  public static final String GENERATE_METHOD_NAMES_SYSTEM_PROMPT =
      getResourceContent("/prompts/method-name-generator.txt");

  public static final String EDIT_CODE_SYSTEM_PROMPT =
      getResourceContent("/prompts/edit-code.txt");

  private final EncodingManager encodingManager = EncodingManager.getInstance();
  private final Conversation conversation;

  public CompletionRequestProvider(Conversation conversation) {
    this.conversation = conversation;
  }

  public static String getPromptWithContext(List<ReferencedFile> referencedFiles,
      String userPrompt) {
    var includedFilesSettings = IncludedFilesSettings.getCurrentState();
    var repeatableContext = referencedFiles.stream()
        .map(item -> includedFilesSettings.getRepeatableContext()
            .replace("{FILE_PATH}", item.getFilePath())
            .replace("{FILE_CONTENT}", format(
                "```%s%n%s%n```",
                item.getFileExtension(),
                item.getFileContent().trim())))
        .collect(joining("\n\n"));

    return includedFilesSettings.getPromptTemplate()
        .replace("{REPEATABLE_CONTEXT}", repeatableContext)
        .replace("{QUESTION}", userPrompt);
  }

  public static OpenAIChatCompletionRequest buildOpenAILookupCompletionRequest(String context) {
    return buildOpenAILookupCompletionRequest(context, OpenAISettings.getCurrentState().getModel());
  }

  public static OpenAIChatCompletionRequest buildOpenAILookupCompletionRequest(
      String context,
      String model) {
    return new OpenAIChatCompletionRequest.Builder(
        List.of(
            new OpenAIChatCompletionStandardMessage("system", GENERATE_METHOD_NAMES_SYSTEM_PROMPT),
            new OpenAIChatCompletionStandardMessage("user", context)))
        .setModel(model)
        .setStream(false)
        .build();
  }

  public static OpenAIChatCompletionRequest buildEditCodeRequest(
      String context,
      String model) {
    return new OpenAIChatCompletionRequest.Builder(
        List.of(
            new OpenAIChatCompletionStandardMessage("system", EDIT_CODE_SYSTEM_PROMPT),
            new OpenAIChatCompletionStandardMessage("user", context)))
        .setModel(model)
        .setStream(true)
        .setMaxTokens(ConfigurationSettings.getState().getMaxTokens())
        .build();
  }

  public static Request buildCustomOpenAICompletionRequest(String system, String context) {
    return buildCustomOpenAIChatCompletionRequest(
        ApplicationManager.getApplication().getService(CustomServiceSettings.class)
            .getState()
            .getChatCompletionSettings(),
        List.of(
            new OpenAIChatCompletionStandardMessage("system", system),
            new OpenAIChatCompletionStandardMessage("user", context)),
        true);
  }

  public static Request buildCustomOpenAICompletionRequest(String context, String url,
      Map<String, String> headers, Map<String, Object> body, String credential) {
    var usedSettings = new CustomServiceChatCompletionSettingsState();
    usedSettings.setBody(body);
    usedSettings.setHeaders(headers);
    usedSettings.setUrl(url);
    return buildCustomOpenAIChatCompletionRequest(
        usedSettings,
        List.of(new OpenAIChatCompletionStandardMessage("user", context)),
        true,
        credential);
  }

  public static Request buildCustomOpenAILookupCompletionRequest(String context) {
    return buildCustomOpenAIChatCompletionRequest(
        ApplicationManager.getApplication().getService(CustomServiceSettings.class)
            .getState()
            .getChatCompletionSettings(),
        List.of(
            new OpenAIChatCompletionStandardMessage(
                "system",
                GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT),
            new OpenAIChatCompletionStandardMessage("user", context)),
        false);
  }

  public static LlamaCompletionRequest buildLlamaLookupCompletionRequest(String context) {
    return new LlamaCompletionRequest.Builder(
        PromptTemplate.LLAMA.buildPrompt(GENERATE_COMMIT_MESSAGE_SYSTEM_PROMPT, context, List.of()))
        .setStream(false)
        .build();
  }

  public LlamaCompletionRequest buildLlamaCompletionRequest(
      Message message,
      ConversationType conversationType) {
    var settings = LlamaSettings.getCurrentState();
    PromptTemplate promptTemplate;
    if (settings.isRunLocalServer()) {
      promptTemplate = settings.isUseCustomModel()
          ? settings.getLocalModelPromptTemplate()
          : LlamaModel.findByHuggingFaceModel(settings.getHuggingFaceModel()).getPromptTemplate();
    } else {
      promptTemplate = settings.getRemoteModelPromptTemplate();
    }

    var systemPrompt = conversationType == FIX_COMPILE_ERRORS
        ? FIX_COMPILE_ERRORS_SYSTEM_PROMPT : PersonaSettings.getSystemPrompt();

    var prompt = promptTemplate.buildPrompt(
        systemPrompt,
        message.getPrompt(),
        conversation.getMessages());
    var configuration = ConfigurationSettings.getState();
    return new LlamaCompletionRequest.Builder(prompt)
        .setN_predict(configuration.getMaxTokens())
        .setTemperature(configuration.getTemperature())
        .setTop_k(settings.getTopK())
        .setTop_p(settings.getTopP())
        .setMin_p(settings.getMinP())
        .setRepeat_penalty(settings.getRepeatPenalty())
        .setStop(promptTemplate.getStopTokens())
        .build();
  }

  public OpenAIChatCompletionRequest buildOpenAIChatCompletionRequest(
      @Nullable String model,
      CallParameters callParameters) {
    var configuration = ConfigurationSettings.getState();
    var requestBuilder = new OpenAIChatCompletionRequest.Builder(
        buildOpenAIMessages(model, callParameters))
        .setModel(model)
        .setMaxTokens(configuration.getMaxTokens())
        .setStream(true)
        .setTemperature(configuration.getTemperature());
    if (callParameters.getMessage().isWebSearchIncluded()) {
      // tri-state boolean
      requestBuilder.setWebSearchIncluded(true);
    }
    var documentationDetails =
        callParameters.getMessage().getDocumentationDetails();
    if (documentationDetails != null) {
      var requestDocumentationDetails = new RequestDocumentationDetails();
      requestDocumentationDetails.setName(documentationDetails.getName());
      requestDocumentationDetails.setUrl(documentationDetails.getUrl());
      requestBuilder.setDocumentationDetails(requestDocumentationDetails);
    }
    return requestBuilder.build();
  }

  public GoogleCompletionRequest buildGoogleChatCompletionRequest(
      @Nullable String model,
      CallParameters callParameters) {
    var configuration = ConfigurationSettings.getState();
    return new GoogleCompletionRequest.Builder(buildGoogleMessages(model, callParameters))
        .generationConfig(new GoogleGenerationConfig.Builder()
            .maxOutputTokens(configuration.getMaxTokens())
            .temperature(configuration.getTemperature()).build()).build();
  }

  public Request buildCustomOpenAIChatCompletionRequest(
      CustomServiceChatCompletionSettingsState settings,
      CallParameters callParameters) {
    return buildCustomOpenAIChatCompletionRequest(
        settings,
        buildOpenAIMessages(callParameters),
        true);
  }

  private static Request buildCustomOpenAIChatCompletionRequest(
      CustomServiceChatCompletionSettingsState settings,
      List<OpenAIChatCompletionMessage> messages,
      boolean streamRequest) {
    return buildCustomOpenAIChatCompletionRequest(settings, messages, streamRequest,
        CredentialsStore.getCredential(CUSTOM_SERVICE_API_KEY));
  }

  private static Request buildCustomOpenAIChatCompletionRequest(
      CustomServiceChatCompletionSettingsState settings,
      List<OpenAIChatCompletionMessage> messages,
      boolean streamRequest,
      String credential) {
    var requestBuilder = new Request.Builder().url(requireNonNull(settings.getUrl()).trim());
    for (var entry : settings.getHeaders().entrySet()) {
      String value = entry.getValue();
      if (credential != null && value.contains("$CUSTOM_SERVICE_API_KEY")) {
        value = value.replace("$CUSTOM_SERVICE_API_KEY", credential);
      }
      requestBuilder.addHeader(entry.getKey(), value);
    }

    var body = settings.getBody().entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> {
              if (!streamRequest && "stream".equals(entry.getKey())) {
                return false;
              }

              var value = entry.getValue();
              if (value instanceof String string && "$OPENAI_MESSAGES".equals(string.trim())) {
                return messages;
              }
              return value;
            }
        ));

    try {
      var requestBody = RequestBody.create(new ObjectMapper()
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(body)
          .getBytes(StandardCharsets.UTF_8));
      return requestBuilder.post(requestBody).build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public ClaudeCompletionRequest buildAnthropicChatCompletionRequest(
      CallParameters callParameters) {
    var configuration = ConfigurationSettings.getState();
    var settings = AnthropicSettings.getCurrentState();
    var request = new ClaudeCompletionRequest();
    request.setModel(settings.getModel());
    request.setMaxTokens(configuration.getMaxTokens());
    request.setStream(true);
    request.setSystem(PersonaSettings.getSystemPrompt());
    List<ClaudeCompletionMessage> messages = conversation.getMessages().stream()
        .filter(prevMessage -> prevMessage.getResponse() != null
            && !prevMessage.getResponse().isEmpty())
        .flatMap(prevMessage -> Stream.of(
            new ClaudeCompletionStandardMessage("user", prevMessage.getPrompt()),
            new ClaudeCompletionStandardMessage("assistant", prevMessage.getResponse())))
        .collect(toList());

    if (callParameters.getImageMediaType() != null && callParameters.getImageData().length > 0) {
      messages.add(new ClaudeCompletionDetailedMessage("user",
          List.of(
              new ClaudeMessageImageContent(new ClaudeBase64Source(
                  callParameters.getImageMediaType(),
                  callParameters.getImageData())),
              new ClaudeMessageTextContent(callParameters.getMessage().getPrompt()))));
    } else {
      messages.add(
          new ClaudeCompletionStandardMessage("user", callParameters.getMessage().getPrompt()));
    }
    request.setMessages(messages);
    return request;
  }

  public OllamaChatCompletionRequest buildOllamaChatCompletionRequest(
      CallParameters callParameters
  ) {
    var configuration = ConfigurationSettings.getState();
    var settings = ApplicationManager.getApplication().getService(OllamaSettings.class).getState();
    return new OllamaChatCompletionRequest
        .Builder(settings.getModel(), buildOllamaMessages(callParameters))
        .setStream(true)
        .setOptions(new OllamaParameters.Builder()
            .numPredict(configuration.getMaxTokens())
            .temperature((double) configuration.getTemperature())
            .build())
        .build();
  }

  private List<OllamaChatCompletionMessage> buildOllamaMessages(CallParameters callParameters) {
    var message = callParameters.getMessage();
    var messages = new ArrayList<OllamaChatCompletionMessage>();
    if (callParameters.getConversationType() == ConversationType.DEFAULT) {
      messages.add(
          new OllamaChatCompletionMessage("system", PersonaSettings.getSystemPrompt(), null));
    }
    if (callParameters.getConversationType() == ConversationType.FIX_COMPILE_ERRORS) {
      messages.add(
          new OllamaChatCompletionMessage("system", FIX_COMPILE_ERRORS_SYSTEM_PROMPT, null)
      );
    }

    for (var prevMessage : conversation.getMessages()) {
      if (callParameters.isRetry() && prevMessage.getId().equals(message.getId())) {
        break;
      }
      var prevMessageImageFilePath = prevMessage.getImageFilePath();
      if (prevMessageImageFilePath != null && !prevMessageImageFilePath.isEmpty()) {
        try {
          var imageFilePath = Path.of(prevMessageImageFilePath);
          var imageBytes = Files.readAllBytes(imageFilePath);
          var imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
          messages.add(
              new OllamaChatCompletionMessage(
                  "user", prevMessage.getPrompt(), List.of(imageBase64)
              )
          );
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        messages.add(
            new OllamaChatCompletionMessage("user", prevMessage.getPrompt(), null)
        );
      }
      messages.add(
          new OllamaChatCompletionMessage("assistant", prevMessage.getResponse(), null)
      );
    }

    if (callParameters.getImageMediaType() != null && callParameters.getImageData().length > 0) {
      var imageBase64 = Base64.getEncoder().encodeToString(callParameters.getImageData());
      messages.add(
          new OllamaChatCompletionMessage("user", message.getPrompt(), List.of(imageBase64))
      );
    } else {
      messages.add(new OllamaChatCompletionMessage("user", message.getPrompt(), null));
    }
    return messages;
  }

  private List<OpenAIChatCompletionMessage> buildOpenAIMessages(CallParameters callParameters) {
    var message = callParameters.getMessage();
    var messages = new ArrayList<OpenAIChatCompletionMessage>();
    if (callParameters.getConversationType() == ConversationType.DEFAULT) {
      var sessionPersonaDetails = callParameters.getMessage().getPersonaDetails();
      if (callParameters.getMessage().getPersonaDetails() == null) {
        messages.add(
            new OpenAIChatCompletionStandardMessage("system", PersonaSettings.getSystemPrompt()));
      } else {
        messages.add(new OpenAIChatCompletionStandardMessage(
            "system",
            sessionPersonaDetails.instructions()));
      }
    }
    if (callParameters.getConversationType() == ConversationType.FIX_COMPILE_ERRORS) {
      messages.add(
          new OpenAIChatCompletionStandardMessage("system", FIX_COMPILE_ERRORS_SYSTEM_PROMPT));
    }

    for (var prevMessage : conversation.getMessages()) {
      if (callParameters.isRetry() && prevMessage.getId().equals(message.getId())) {
        break;
      }
      var prevMessageImageFilePath = prevMessage.getImageFilePath();
      if (prevMessageImageFilePath != null && !prevMessageImageFilePath.isEmpty()) {
        try {
          var imageFilePath = Path.of(prevMessageImageFilePath);
          var imageData = Files.readAllBytes(imageFilePath);
          var imageMediaType = FileUtil.getImageMediaType(imageFilePath.getFileName().toString());
          messages.add(new OpenAIChatCompletionDetailedMessage("user",
              List.of(
                  new OpenAIMessageImageURLContent(new OpenAIImageUrl(imageMediaType, imageData)),
                  new OpenAIMessageTextContent(prevMessage.getPrompt()))));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        messages.add(new OpenAIChatCompletionStandardMessage("user", prevMessage.getPrompt()));
      }
      messages.add(
          new OpenAIChatCompletionStandardMessage("assistant", prevMessage.getResponse())
      );
    }

    if (callParameters.getImageMediaType() != null && callParameters.getImageData().length > 0) {
      messages.add(new OpenAIChatCompletionDetailedMessage("user",
          List.of(
              new OpenAIMessageImageURLContent(
                  new OpenAIImageUrl(callParameters.getImageMediaType(),
                      callParameters.getImageData())),
              new OpenAIMessageTextContent(message.getPrompt()))));
    } else {
      messages.add(new OpenAIChatCompletionStandardMessage("user", message.getPrompt()));
    }
    return messages;
  }

  private List<OpenAIChatCompletionMessage> buildOpenAIMessages(
      @Nullable String model,
      CallParameters callParameters) {
    var messages = buildOpenAIMessages(callParameters);

    if (model == null) {
      return messages;
    }

    int totalUsage = messages.parallelStream()
        .mapToInt(encodingManager::countMessageTokens)
        .sum() + ConfigurationSettings.getState().getMaxTokens();
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

  private List<GoogleCompletionContent> buildGoogleMessages(CallParameters callParameters) {
    var message = callParameters.getMessage();
    var messages = new ArrayList<GoogleCompletionContent>();
    // Gemini API does not support direct 'system' prompts:
    // see https://www.reddit.com/r/Bard/comments/1b90i8o/does_gemini_have_a_system_prompt_option_while/
    if (callParameters.getConversationType() == ConversationType.DEFAULT) {
      messages.add(new GoogleCompletionContent("user", List.of(PersonaSettings.getSystemPrompt())));
      messages.add(new GoogleCompletionContent("model", List.of("Understood.")));
    }
    if (callParameters.getConversationType() == ConversationType.FIX_COMPILE_ERRORS) {
      messages.add(
          new GoogleCompletionContent("user", List.of(FIX_COMPILE_ERRORS_SYSTEM_PROMPT)));
      messages.add(new GoogleCompletionContent("model", List.of("Understood.")));
    }

    for (var prevMessage : conversation.getMessages()) {
      if (callParameters.isRetry() && prevMessage.getId().equals(message.getId())) {
        break;
      }
      var prevMessageImageFilePath = prevMessage.getImageFilePath();
      if (prevMessageImageFilePath != null && !prevMessageImageFilePath.isEmpty()) {
        try {
          var imageFilePath = Path.of(prevMessageImageFilePath);
          var imageData = Files.readAllBytes(imageFilePath);
          var imageMediaType = FileUtil.getImageMediaType(imageFilePath.getFileName().toString());
          messages.add(new GoogleCompletionContent(
              List.of(
                  new GoogleContentPart(null, new Blob(imageMediaType, imageData)),
                  new GoogleContentPart(prevMessage.getPrompt())), "user"));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else {
        messages.add(new GoogleCompletionContent("user", List.of(prevMessage.getPrompt())));
      }
      messages.add(new GoogleCompletionContent("model", List.of(prevMessage.getResponse())));
    }

    if (callParameters.getImageMediaType() != null && callParameters.getImageData().length > 0) {
      messages.add(new GoogleCompletionContent(
          List.of(
              new GoogleContentPart(null,
                  new Blob(callParameters.getImageMediaType(), callParameters.getImageData())),
              new GoogleContentPart(message.getPrompt())), "user"));
    } else {
      messages.add(new GoogleCompletionContent("user", List.of(message.getPrompt())));
    }
    return messages;
  }

  private List<GoogleCompletionContent> buildGoogleMessages(
      @Nullable String model,
      CallParameters callParameters) {
    var messages = buildGoogleMessages(callParameters);

    if (model == null) {
      return messages;
    }

    int totalUsage = messages.parallelStream()
        .mapToInt(message -> encodingManager.countMessageTokens(message.getRole(),
            String.join(",", message.getParts().stream().map(GoogleContentPart::getText).toList())))
        .sum() + ConfigurationSettings.getState().getMaxTokens();
    int modelMaxTokens;
    try {
      modelMaxTokens = GoogleModel.findByCode(model).getMaxTokens();

      if (totalUsage <= modelMaxTokens) {
        return messages;
      }
    } catch (NoSuchElementException ex) {
      return messages;
    }
    return tryReducingGoogleMessagesOrThrow(messages, totalUsage, modelMaxTokens);
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

      var message = messages.get(i);
      if (message instanceof OpenAIChatCompletionStandardMessage) {
        totalUsage -= encodingManager.countMessageTokens(message);
        messages.set(i, null);
      }
    }

    return messages.stream().filter(Objects::nonNull).toList();
  }

  private List<GoogleCompletionContent> tryReducingGoogleMessagesOrThrow(
      List<GoogleCompletionContent> messages,
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

      var message = messages.get(i);
      totalUsage -= encodingManager.countMessageTokens(message.getRole(),
          String.join(",", message.getParts().stream().map(GoogleContentPart::getText).toList()));
      messages.set(i, null);
    }

    return messages.stream().filter(Objects::nonNull).toList();
  }
}
