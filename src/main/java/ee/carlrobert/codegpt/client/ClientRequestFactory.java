package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import java.util.ArrayList;
import java.util.List;

public class ClientRequestFactory {

  private final String prompt;
  private final Conversation conversation;

  public ClientRequestFactory(String prompt, Conversation conversation) {
    this.prompt = prompt;
    this.conversation = conversation;
  }

  public ChatCompletionRequest buildChatCompletionRequest(SettingsState settings) {
    return new ChatCompletionRequest.Builder(buildMessages(prompt, conversation))
        .setModel(settings.chatCompletionBaseModel)
        .build();
  }

  public TextCompletionRequest buildTextCompletionRequest(SettingsState settings) {
    return new TextCompletionRequest.Builder(buildPrompt(conversation, prompt))
        .setStop(List.of(" Human:", " AI:"))
        .setModel(settings.textCompletionBaseModel)
        .build();
  }

  private List<ChatCompletionMessage> buildMessages(String prompt, Conversation conversation) {
    var messages = new ArrayList<ChatCompletionMessage>();
    messages.add(new ChatCompletionMessage(
        "system",
        "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible."));
    conversation.getMessages().forEach(message -> {
      messages.add(new ChatCompletionMessage("user", message.getPrompt()));
      messages.add(new ChatCompletionMessage("assistant", message.getResponse()));
    });
    messages.add(new ChatCompletionMessage("user", prompt));
    return messages;
  }

  private StringBuilder getBasePrompt() {
    var isDavinciModel = TextCompletionModel.DAVINCI.getCode().equals(SettingsState.getInstance().textCompletionBaseModel);
    if (isDavinciModel) {
      return new StringBuilder(
          "You are ChatGPT, a large language model trained by OpenAI.\n" +
              "Answer in a markdown language, code blocks should contain language whenever possible.\n");
    }
    return new StringBuilder(
        "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.\n\n");
  }

  private String buildPrompt(Conversation conversation, String prompt) {
    var basePrompt = getBasePrompt();
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
