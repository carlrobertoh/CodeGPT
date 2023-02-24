package ee.carlrobert.chatgpt.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.response.ApiResponse;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Subscriber implements HttpResponse.BodySubscriber<Void> {

  private static final Pattern dataLinePattern = Pattern.compile("^data: ?(.*)$");
  private volatile Subscription subscription;
  private volatile String deferredText;
  private final Consumer<? super ApiResponse> messageDataConsumer;
  private final CompletableFuture<Void> future;
  private final Consumer<String> onComplete;
  private final StringBuilder msgBuilder = new StringBuilder();

  public Subscriber(Consumer<? super ApiResponse> messageDataConsumer, Consumer<String> onComplete) {
    this.messageDataConsumer = messageDataConsumer;
    this.future = new CompletableFuture<>();
    this.subscription = null;
    this.deferredText = null;
    this.onComplete = onComplete;
  }

  protected static ApiResponse extractMessageData(String[] messageLines) {
    var responseBuilder = new StringBuilder();
    for (var line : messageLines) {
      var matcher = dataLinePattern.matcher(line);
      if (matcher.matches()) {
        responseBuilder.append(matcher.group(1));
      }
    }

    try {
      return new ObjectMapper().readValue(responseBuilder.toString(), ApiResponse.class);
    } catch (Exception e) {
      throw new RuntimeException("Couldn't read the payload", e);
    }
  }

  @Override
  public void onSubscribe(Subscription subscription) {
    this.subscription = subscription;
    try {
      this.deferredText = "";
      this.subscription.request(1);
    } catch (Exception e) {
      this.future.completeExceptionally(e);
      this.subscription.cancel();
    }
  }

  @Override
  public void onNext(List<ByteBuffer> buffers) {
    try {
      var deferredText = this.deferredText;

      for (var buffer : buffers) {
        var decodedText = deferredText + UTF_8.decode(buffer);
        var tokens = decodedText.split("\n\n", -1);

        for (var i = 0; i < tokens.length - 1; i++) {
          var response = extractMessageData(tokens[i].split("\n"));
          var choice = response.choices().get(0);
          if ("stop".equals(choice.finishReason())) {
            onComplete();
          } else {
            msgBuilder.append(choice.text());
          }
          this.messageDataConsumer.accept(response);
        }
        deferredText = tokens[tokens.length - 1];
      }

      this.deferredText = deferredText;
      this.subscription.request(1);
    } catch (Exception e) {
      this.future.completeExceptionally(e);
      this.subscription.cancel();
    }
  }

  @Override
  public void onError(Throwable e) {
    this.future.completeExceptionally(e);
  }

  @Override
  public void onComplete() {
    try {
      this.future.complete(null);
      this.onComplete.accept(msgBuilder.toString());
    } catch (Exception e) {
      this.future.completeExceptionally(e);
    }
  }

  @Override
  public CompletionStage<Void> getBody() {
    return this.future;
  }
}
