package ee.carlrobert.codegpt.client;

import static java.util.stream.Collectors.toList;

import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CompletionRequestProvider {

  private static final int MAX_COMPLETION_TOKENS = 1000;

  private final EncodingManager encodingManager = EncodingManager.getInstance();
  private final String prompt;
  private final Conversation conversation;

  CompletionRequestProvider(String prompt, Conversation conversation) {
    this.prompt = prompt;
    this.conversation = conversation;
  }

  public ChatCompletionRequest buildChatCompletionRequest(String model) {
    return (ChatCompletionRequest) new ChatCompletionRequest.Builder(buildMessages(model))
        .setModel(model)
        .setMaxTokens(MAX_COMPLETION_TOKENS)
        .build();
  }

  public TextCompletionRequest buildTextCompletionRequest(String model) {
    return (TextCompletionRequest) new TextCompletionRequest.Builder(buildPrompt(model))
        .setStop(List.of(" Human:", " AI:"))
        .setModel(model)
        .setMaxTokens(MAX_COMPLETION_TOKENS)
        .build();
  }

  private List<ChatCompletionMessage> buildMessages(String model) {
    var messages = new ArrayList<ChatCompletionMessage>();
    messages.add(new ChatCompletionMessage(
        "system",
        "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible. Include code language in markdown snippets whenever possible."));
    conversation.getMessages().forEach(message -> {
      messages.add(new ChatCompletionMessage("user", message.getPrompt()));
      messages.add(new ChatCompletionMessage("assistant", message.getResponse()));
    });
    messages.add(new ChatCompletionMessage("user", prompt));

    // Do not calculate total usage for custom services
    if (SettingsState.getInstance().useCustomService) {
      return messages;
    }

    int totalUsage = messages.parallelStream()
        .mapToInt(encodingManager::countMessageTokens)
        .sum() + MAX_COMPLETION_TOKENS;
    int modelMaxTokens = ChatCompletionModel.findByCode(model).getMaxTokens();

    if (totalUsage <= modelMaxTokens) {
      return messages;
    }

    return tryReducingMessagesOrThrow(messages, totalUsage, modelMaxTokens);
  }

  private List<ChatCompletionMessage> tryReducingMessagesOrThrow(
      List<ChatCompletionMessage> messages, int totalUsage, int modelMaxTokens) {
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

  private String buildPrompt(String model) {
    var basePrompt = getBasePrompt(model);
    conversation.getMessages().forEach(message ->
        basePrompt.append("Human: ")
            .append(message.getPrompt())
            .append("\n")
            .append("AI: ")
            .append(message.getResponse())
            .append("\n"));
    basePrompt.append("Human: ")
        .append(prompt)
        .append("\n")
        .append("AI: ")
        .append("\n");
    return basePrompt.toString();
  }
}
