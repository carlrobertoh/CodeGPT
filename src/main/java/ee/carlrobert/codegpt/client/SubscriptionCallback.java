package ee.carlrobert.codegpt.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.function.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SubscriptionCallback implements Callback {

  private static final Logger LOG = LoggerFactory.getLogger(SubscriptionCallback.class);

  private final Consumer<Subscription> subscriptionConsumer;

  SubscriptionCallback(Consumer<Subscription> subscriptionConsumer) {
    this.subscriptionConsumer = subscriptionConsumer;
  }

  @Override
  public void onFailure(@NotNull Call call, @NotNull IOException e) {
    LOG.warn("Unable to retrieve subscription info", e);
  }

  @Override
  public void onResponse(@NotNull Call call, @NotNull Response response) {
    if (response.body() != null) {
      try {
        subscriptionConsumer.accept(new ObjectMapper().readValue(response.body().string(), Subscription.class));
      } catch (IOException ex) {
        LOG.warn("Unable to deserialize subscription response", ex);
      }
    }
  }
}
