package ee.carlrobert.chatgpt.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.response.ApiResponse;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Subscriber implements HttpResponse.BodySubscriber<Void> {

  protected static final Pattern dataLinePattern = Pattern.compile("^data: ?(.*)$");

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

  protected final Consumer<? super ApiResponse> messageDataConsumer;
  protected final CompletableFuture<Void> future;
  protected volatile Flow.Subscription subscription;
  protected volatile String deferredText;
  private final Consumer<String> onComplete;
  private final StringBuilder msgBuilder = new StringBuilder();

  public Subscriber(Consumer<? super ApiResponse> messageDataConsumer, Consumer<String> onComplete) {
    this.messageDataConsumer = messageDataConsumer;
    this.future = new CompletableFuture<>();
    this.subscription = null;
    this.deferredText = null;
    this.onComplete = onComplete;
  }

  @Override
  public void onSubscribe(Flow.Subscription subscription) {
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
        var s = deferredText + UTF_8.decode(buffer);
        var tokens = s.split("\n\n", -1);

        for (var i = 0; i < tokens.length - 1; i++) {
          var message = tokens[i];
          var data = extractMessageData(message.split("\n"));
          var choice = data.getChoices().get(0); // TODO: Is there only one choice per response?
          if ("stop".equals(choice.getFinish_reason())) {
            onComplete();
          } else {
            msgBuilder.append(choice.getText());
          }
          this.messageDataConsumer.accept(data);
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
