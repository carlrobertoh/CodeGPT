package ee.carlrobert.chatgpt.client.official;

import ee.carlrobert.chatgpt.client.Client;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class CompletionClient extends Client {

  protected static final List<Map.Entry<String, String>> queries = new ArrayList<>();

  protected abstract CompletionSubscriber createSubscriber(
      Consumer<String> responseConsumer,
      Consumer<String> onCompleteCallback);

  protected BodySubscriber<Void> subscribe(
      ResponseInfo responseInfo,
      Consumer<String> onMessageReceived,
      Runnable onComplete) {
    if (responseInfo.statusCode() == 200) {
      return createSubscriber(onMessageReceived, finalMsg -> {
        queries.add(Map.entry(super.userPrompt, finalMsg));
        onComplete.run();
      });
    } else {
      try {
        handleError(responseInfo, onMessageReceived);
        return null;
      } finally {
        onComplete.run();
      }
    }
  }

  private void handleError(ResponseInfo responseInfo, Consumer<String> onMessageReceived) {
    if (responseInfo.statusCode() == 401) {
      onMessageReceived.accept("Incorrect API key provided.\n" +
          "You can find your API key at https://platform.openai.com/account/api-keys.");
      throw new IllegalArgumentException();
    } else if (responseInfo.statusCode() == 429) {
      onMessageReceived.accept("You exceeded your current quota, please check your plan and billing details.");
      throw new RuntimeException("Insufficient quota");
    } else {
      onMessageReceived.accept("Something went wrong. Please try again later.");
      throw new RuntimeException();
    }
  }

  public void clearPreviousSession() {
    queries.clear();
  }
}
