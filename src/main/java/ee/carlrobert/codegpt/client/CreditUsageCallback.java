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

public class CreditUsageCallback implements Callback {

  private static final Logger LOG = LoggerFactory.getLogger(CreditUsageCallback.class);

  private final Consumer<CreditUsage> creditUsageConsumer;

  CreditUsageCallback(Consumer<CreditUsage> creditUsageConsumer) {
    this.creditUsageConsumer = creditUsageConsumer;
  }
  @Override
  public void onFailure(@NotNull Call call, @NotNull IOException e) {
    LOG.warn("Unable to retrieve credit info", e);
  }

  @Override
  public void onResponse(@NotNull Call call, @NotNull Response response) {
    if (response.body() != null) {
      try {
        creditUsageConsumer.accept(new ObjectMapper().readValue(response.body().string(), CreditUsage.class));
      } catch (IOException ex) {
        LOG.warn("Unable to deserialize credit info response", ex);
      }
    }
  }
}
