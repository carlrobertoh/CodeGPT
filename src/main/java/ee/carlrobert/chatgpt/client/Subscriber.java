package ee.carlrobert.chatgpt.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.http.HttpResponse.BodySubscriber;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public abstract class Subscriber<T extends ApiResponse> implements BodySubscriber<Void> {

  protected final CompletableFuture<Void> future = new CompletableFuture<>();
  private final Consumer<T> responseConsumer;
  private volatile Subscription subscription;
  private volatile String deferredText;

  protected abstract T deserializePayload(String jsonPayload) throws JsonProcessingException;

  protected abstract void onRequestComplete();

  // Overridden from concrete class
  protected void processRegularResponse(String response) {
  }

  public Subscriber(Consumer<T> responseConsumer) {
    this.responseConsumer = responseConsumer;
  }

  public CompletionStage<Void> getBody() {
    return this.future;
  }

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

  public void onNext(List<ByteBuffer> buffers) {
    try {
      var deferredText = this.deferredText;
      for (var buffer : buffers) {
        var decodedText = deferredText + UTF_8.decode(buffer);
        var tokens = decodedText.split("\n\n", -1);
        if (tokens.length == 1) {
          processRegularResponse(decodedText);
        }

        for (var i = 0; i < tokens.length - 1; i++) {
          var responsePayload = extractPayload(tokens[i].split("\n"));
          if ("[DONE]".equals(responsePayload)) {
            future.complete(null);
          } else {
            try {
              this.responseConsumer.accept(deserializePayload(responsePayload));
            } catch (JsonProcessingException e) {
              throw new RuntimeException("Couldn't deserialize the payload", e);
            }
          }
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

  public void onError(Throwable e) {
    this.future.completeExceptionally(e);
  }

  public void onComplete() {
    try {
      this.future.complete(null);
      onRequestComplete();
    } catch (Exception e) {
      this.future.completeExceptionally(e);
    }
  }

  private String extractPayload(String[] payload) {
    Pattern dataLinePattern = Pattern.compile("^data: ?(.*)$");
    var responseBuilder = new StringBuilder();
    for (var line : payload) {
      var matcher = dataLinePattern.matcher(line);
      if (matcher.matches()) {
        responseBuilder.append(matcher.group(1));
      }
    }
    return responseBuilder.toString();
  }
}
