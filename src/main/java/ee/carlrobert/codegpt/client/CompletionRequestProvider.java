package ee.carlrobert.codegpt.client;

import static ee.carlrobert.openai.client.completion.chat.ChatCompletionModel.GPT_3_5;
import static ee.carlrobert.openai.client.completion.chat.ChatCompletionModel.GPT_3_5_SNAPSHOT;
import static java.util.stream.Collectors.toList;

import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class CompletionRequestProvider {

  private final EncodingManager encodingManager = EncodingManager.getInstance();
  private final String prompt;
  private final Conversation conversation;

  CompletionRequestProvider(String prompt, Conversation conversation) {
    this.prompt = prompt;
    this.conversation = conversation;
  }

  public ChatCompletionRequest buildChatCompletionRequest(String model) {
    return new ChatCompletionRequest.Builder(buildMessages())
        .setModel(model)
        .build();
  }

  public TextCompletionRequest buildTextCompletionRequest(String model) {
    return new TextCompletionRequest.Builder(buildPrompt(model))
        .setStop(List.of(" Human:", " AI:"))
        .setModel(model)
        .build();
  }

  private List<ChatCompletionMessage> buildMessages() {
    var messages = new ArrayList<ChatCompletionMessage>();
    messages.add(new ChatCompletionMessage(
        "system",
        "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible. Include code language in markdown snippets whenever possible."));
    conversation.getMessages().forEach(message -> {
      messages.add(new ChatCompletionMessage("user", message.getPrompt()));
      messages.add(new ChatCompletionMessage("assistant", message.getResponse()));
    });
    messages.add(new ChatCompletionMessage("user", prompt));

    var settingsState = SettingsState.getInstance();

    // TODO: Add support for other models
    var isSeamlessConversationSupported = settingsState.isChatCompletionOptionSelected &&
        List.of(GPT_3_5.getCode(), GPT_3_5_SNAPSHOT.getCode()).contains(settingsState.chatCompletionBaseModel);
    if (isSeamlessConversationSupported) {
      return tryReducingMessagesOrThrow(messages);
    }
    return messages;
  }

  private List<ChatCompletionMessage> tryReducingMessagesOrThrow(List<ChatCompletionMessage> messages) {
    int MAX_TOKEN_LIMIT = 4097;
    int totalMessagesUsage = messages.parallelStream().mapToInt(encodingManager::countMessageTokens).sum();
    int totalUsage = totalMessagesUsage + 1000; // 1000 - max completion token size (currently not customizable)

    if (totalUsage <= MAX_TOKEN_LIMIT) {
      return messages;
    }

    if (!ConversationsState.getInstance().discardAllTokenLimits) {
      if (!conversation.isDiscardTokenLimit()) {
        throw new TotalUsageExceededException();
      }
    }

    // skip the system prompt
    for (int i = 1; i < messages.size(); i++) {
      if (totalUsage <= MAX_TOKEN_LIMIT) {
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
