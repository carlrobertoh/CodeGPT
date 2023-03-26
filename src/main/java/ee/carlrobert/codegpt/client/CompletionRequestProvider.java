package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionMessage;
import ee.carlrobert.openai.client.completion.chat.request.ChatCompletionRequest;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import ee.carlrobert.openai.client.completion.text.request.TextCompletionRequest;
import java.util.ArrayList;
import java.util.List;

class CompletionRequestProvider {

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
        "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible."));
    conversation.getMessages().forEach(message -> {
      messages.add(new ChatCompletionMessage("user", message.getPrompt()));
      messages.add(new ChatCompletionMessage("assistant", message.getResponse()));
    });
    messages.add(new ChatCompletionMessage("user", prompt));
    return messages;
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
