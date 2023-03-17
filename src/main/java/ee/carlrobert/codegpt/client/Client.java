package ee.carlrobert.codegpt.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.codegpt.ide.conversations.Conversation;
import ee.carlrobert.codegpt.ide.conversations.ConversationsState;
import ee.carlrobert.codegpt.ide.settings.SettingsState;
import ee.carlrobert.codegpt.ide.settings.advanced.AdvancedSettingsState;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Client {

  private static final Logger LOG = LoggerFactory.getLogger(Client.class);

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ClientCode clientCode;
  private EventSource eventSource;
  protected Conversation conversation;
  protected String prompt;
  protected OkHttpClient client;

  protected Client(ClientCode clientCode) {
    this.clientCode = clientCode;
  }

  protected abstract ApiRequestDetails getRequestDetails(String prompt);

  protected abstract EventSourceListener getEventSourceListener(
      Consumer<String> onMessageReceived,
      Consumer<Conversation> onComplete,
      Consumer<String> onFailure);

  public void getCompletionsAsync(
      String prompt,
      Consumer<String> onMessageReceived,
      Consumer<Conversation> onComplete,
      Consumer<String> onFailure) {
    var conversationsState = ConversationsState.getInstance();
    this.conversation = ConversationsState.getCurrentConversation();
    if (conversation == null) {
      this.conversation = conversationsState.startConversation();
    }
    this.prompt = prompt;
    this.client = buildClient();
    this.eventSource = EventSources.createFactory(client)
        .newEventSource(
            buildHttpRequest(prompt),
            getEventSourceListener(onMessageReceived, onComplete, onFailure));
  }

  public CreditUsage getCreditGrants() {
    try {
      var response = buildClient().newCall(new Request.Builder()
              .url("https://api.openai.com/dashboard/billing/credit_grants")
              .headers(Headers.of(Map.of(
                  "Content-Type", "application/json",
                  "Authorization", "Bearer " + SettingsState.getInstance().apiKey
              )))
              .get()
              .build())
          .execute();

      if (response.body() == null) {
        return null;
      }

      return objectMapper.readValue(response.body().string(), CreditUsage.class);
    } catch (IOException ex) {
      LOG.error("Unable to retrieve credit info", ex);
      return null;
    }
  }

  public OkHttpClient buildClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS);

    var settings = AdvancedSettingsState.getInstance();
    var proxyHost = settings.proxyHost;
    var proxyPort = settings.proxyPort;
    if (!proxyHost.isEmpty() && proxyPort != 0) {
      builder.proxy(new Proxy(settings.proxyType, new InetSocketAddress(proxyHost, proxyPort)));

      var username = settings.proxyUsername;
      var password = settings.proxyPassword;
      if (settings.isProxyAuthSelected) {
        builder.proxyAuthenticator((route, response) ->
            response.request()
                .newBuilder()
                .header("Proxy-Authorization", Credentials.basic(username, password))
                .build());
      }
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

  public ClientCode getCode() {
    return clientCode;
  }
}
