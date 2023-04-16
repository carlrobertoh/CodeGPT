package ee.carlrobert.codegpt.client;

import static ee.carlrobert.openai.client.completion.chat.ChatCompletionModel.GPT_3_5;
import static ee.carlrobert.openai.client.completion.chat.ChatCompletionModel.GPT_3_5_SNAPSHOT;

import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.EncodingManager;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    if (settingsState.isChatCompletionOptionSelected &&
        List.of(GPT_3_5.getCode(), GPT_3_5_SNAPSHOT.getCode()).contains(settingsState.chatCompletionBaseModel)) {
      var totalMessagesUsage = messages.parallelStream().mapToInt(this::getMessageTokenCount).sum();
      var totalUsage = totalMessagesUsage + 1000; // 1000 - total completion tokens, currently not customizable

      var messageSize = messages.size();

      if (totalUsage > 4097) {
        if (!ConversationsState.getInstance().discardAllTokenLimits) {
          if (!conversation.isDiscardTokenLimit()) {
            throw new TotalUsageExceededException();
          }
        }

        // skip the system prompt
        for (int i = 1; i < messageSize; i++) {
          if (totalUsage <= 4097) {
            break;
          }

          totalUsage -= getMessageTokenCount(messages.get(i));
          messages.set(i, null);
        }

        return messages.stream().filter(Objects::nonNull).collect(Collectors.toList());
      }
    }

    return messages;
  }

  private int getMessageTokenCount(ChatCompletionMessage message) {
    // TODO: Size 4 for GPT-3.5, 3 for GPT-4
    var tokensPerMessage = 4; // every message follows <|start|>{role/name}\n{content}<|end|>\n
    return encodingManager.countTokens(message.getRole() + message.getContent()) + tokensPerMessage;
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
