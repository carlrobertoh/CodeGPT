package ee.carlrobert.codegpt.completions;

import static java.util.stream.Collectors.toList;

import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.embeddings.EmbeddingsService;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.util.FileUtils;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompletionRequestProvider {

  private static final Logger LOG = Logger.getInstance(CompletionRequestProvider.class);

  public static final String COMPLETION_SYSTEM_PROMPT = "You are an AI programming assistant.\n" +
      "When asked for you name, you must respond with \"CodeGPT\".\n" +
      "Follow the user's requirements carefully & to the letter.\n" +
      "Your responses should be informative and logical.\n" +
      "You should always adhere to technical information.\n" +
      "If the user asks for code or technical questions, you must provide code suggestions and adhere to technical information.\n" +
      "If the question is related to a developer, CodeGPT must respond with content related to a developer.\n" +
      "First think step-by-step - describe your plan for what to build in pseudocode, written out in great detail.\n" +
      "Then output the code in a single code block.\n" +
      "Minimize any other prose.\n" +
      "Keep your answers short and impersonal.\n" +
      "Use Markdown formatting in your answers.\n" +
      "Make sure to include the programming language name at the start of the Markdown code blocks.\n" +
      "Avoid wrapping the whole response in triple backticks.\n" +
      "The user works in an IDE built by JetBrains which has a concept for editors with open files, integrated unit test support, " +
      "and output pane that shows the output of running the code as well as an integrated terminal.\n" +
      "You can only give one reply for each conversation turn.";

  private final EncodingManager encodingManager = EncodingManager.getInstance();
  private final EmbeddingsService embeddingsService;
  private final Conversation conversation;

  public CompletionRequestProvider(Conversation conversation) {
    this.embeddingsService = new EmbeddingsService(
        CompletionClientProvider.getEmbeddingsClient(),
        CompletionClientProvider.getChatCompletionClient(SettingsState.getInstance()),
        CodeGPTPlugin.getPluginBasePath());
    this.conversation = conversation;
  }

  public ChatCompletionRequest buildChatCompletionRequest(String model, Message message, boolean isRetry) {
    return buildChatCompletionRequest(model, message, isRetry, false);
  }

  public ChatCompletionRequest buildChatCompletionRequest(String model, Message message, boolean isRetry, boolean useContextualSearch) {
    return (ChatCompletionRequest) new ChatCompletionRequest.Builder(buildMessages(model, message, isRetry, useContextualSearch))
        .setModel(model)
        .setMaxTokens(ConfigurationState.getInstance().getMaxTokens())
        .setTemperature(ConfigurationState.getInstance().getTemperature())
        .build();
  }

  public TextCompletionRequest buildTextCompletionRequest(String model, Message message, boolean isRetry) {
    return (TextCompletionRequest) new TextCompletionRequest.Builder(buildPrompt(model, message, isRetry))
        .setStop(List.of(" Human:", " AI:"))
        .setModel(model)
        .setMaxTokens(ConfigurationState.getInstance().getMaxTokens())
        .setTemperature(ConfigurationState.getInstance().getTemperature())
        .build();
  }

  private List<ChatCompletionMessage> buildMessages(String model, Message message, boolean isRetry, boolean useContextualSearch) {
    var messages = new ArrayList<ChatCompletionMessage>();
    if (useContextualSearch) {
      var context = embeddingsService.buildRelevantContext(message.getPrompt());
      var prompt = FileUtils.getResourceContent("/prompts/retrieval-prompt.txt")
          .replace("{prompt}", message.getPrompt())
          .replace("{context}", context.getContext());

      LOG.info("Retrieved context:\n" + prompt);
      messages.add(new ChatCompletionMessage("user", prompt));
    } else {
      var systemPrompt = ConfigurationState.getInstance().getSystemPrompt();
      messages.add(new ChatCompletionMessage("system",
          systemPrompt.isEmpty() ? COMPLETION_SYSTEM_PROMPT : systemPrompt));

      for (var prevMessage : conversation.getMessages()) {
        if (isRetry && prevMessage.getId().equals(message.getId())) {
          break;
        }
        messages.add(new ChatCompletionMessage("user", prevMessage.getPrompt()));
        messages.add(new ChatCompletionMessage("assistant", prevMessage.getResponse()));
      }
      messages.add(new ChatCompletionMessage("user", message.getPrompt()));
    }

    int totalUsage = messages.parallelStream()
        .mapToInt(encodingManager::countMessageTokens)
        .sum() + ConfigurationState.getInstance().getMaxTokens();
    int modelMaxTokens = ChatCompletionModel.findByCode(model).getMaxTokens();

    if (totalUsage <= modelMaxTokens) {
      return messages;
    }

    return tryReducingMessagesOrThrow(messages, totalUsage, modelMaxTokens);
  }

  private List<ChatCompletionMessage> tryReducingMessagesOrThrow(List<ChatCompletionMessage> messages, int totalUsage, int modelMaxTokens) {
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

  private StringBuilder getBasePrompt(String model) {
    var isDavinciModel = TextCompletionModel.DAVINCI.getCode().equals(model);
    if (isDavinciModel) {
      return new StringBuilder(
          "You are ChatGPT, a large language model trained by OpenAI.\n" +
              "Answer in a markdown language, code blocks should contain language whenever possible.\n");
    }
    return new StringBuilder(
        "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.\n\n");
  }

  private String buildPrompt(String model, Message message, boolean isRetry) {
    var systemPrompt = ConfigurationState.getInstance().getSystemPrompt();
    var basePrompt = systemPrompt.isEmpty() ? getBasePrompt(model) : new StringBuilder(systemPrompt + "\n");
    conversation.getMessages().forEach(prevMessage ->
        basePrompt.append("Human: ")
            .append(prevMessage.getPrompt())
            .append("\n")
            .append("AI: ")
            .append(prevMessage.getResponse())
            .append("\n"));
    basePrompt.append("Human: ")
        .append(message.getPrompt())
        .append("\n")
        .append("AI: ")
        .append("\n");
    return basePrompt.toString();
  }
}
