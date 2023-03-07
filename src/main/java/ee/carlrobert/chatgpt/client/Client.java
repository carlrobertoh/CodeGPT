package ee.carlrobert.chatgpt.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

public abstract class Client {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private EventSource eventSource;
  protected String prompt = "";
  protected OkHttpClient client;

  protected abstract ApiRequestDetails getRequestDetails(String prompt);

  public abstract void clearPreviousSession();

  protected abstract EventSourceListener getEventSourceListener(Consumer<String> onMessageReceived, Runnable onComplete);

  public void getCompletionsAsync(String prompt, Consumer<String> onMessageReceived, Runnable onComplete) {
    this.prompt = prompt;
    this.client = buildClient();
    this.eventSource = EventSources.createFactory(client)
        .newEventSource(buildHttpRequest(prompt), getEventSourceListener(onMessageReceived, onComplete));
  }

  public OkHttpClient buildClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS);

    var settings = SettingsState.getInstance();
    var proxyHost = settings.proxyHost;
    var proxyPort = settings.proxyPort;
    if (!proxyHost.isEmpty() && proxyPort != 0) {
      builder.proxy(new Proxy(settings.proxyType, new InetSocketAddress(proxyHost, proxyPort)));
    }

    return builder.build();
  }

  public void cancelRequest() {
    eventSource.cancel();
  }

  public Request buildHttpRequest(String prompt) {
    var requestDetails = getRequestDetails(prompt);
    try {
      return new Request.Builder()
          .url(requestDetails.getUrl())
          .headers(Headers.of(Map.of(
              "Accept", "text/event-stream",
              "Content-Type", "application/json",
              "Authorization", "Bearer " + requestDetails.getToken()
          )))
          .post(RequestBody.create(
              objectMapper
                  .writerWithDefaultPrettyPrinter()
                  .writeValueAsString(requestDetails.getBody()),
              MediaType.parse("application/json")))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to serialize request payload", e);
    }
  }
}
