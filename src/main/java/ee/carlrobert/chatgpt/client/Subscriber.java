package ee.carlrobert.chatgpt.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.http.HttpResponse.BodySubscriber;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscription;
import java.util.regex.Pattern;

public abstract class Subscriber<T extends ApiResponse> implements BodySubscriber<Void> {

  protected final CompletableFuture<Void> future = new CompletableFuture<>();
  private volatile Subscription subscription;
  private volatile String deferredText;

  protected abstract void onRequestComplete();

  protected abstract void onErrorOccurred();

  protected abstract void send(String jsonPayload, String token);

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
        for (String token : tokens) {
          var responsePayload = extractPayload(token.split("\n"));
          if ("[DONE]".equals(responsePayload)) {
            future.complete(null);
          } else {
            send(responsePayload, token);
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
    try {
      onErrorOccurred();
    } finally {
      this.future.completeExceptionally(e);
    }
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
    Pattern dataLinePattern = Pattern.compile("^data:\\s*(\\{.*})\\s*$");
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
