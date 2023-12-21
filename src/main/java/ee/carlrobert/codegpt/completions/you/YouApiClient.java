package ee.carlrobert.codegpt.completions.you;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.completions.you.auth.response.YouAuthenticationResponse;
import java.io.IOException;
import java.util.List;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.Nullable;

@Service
public final class YouApiClient {

  private static final String API_BASE_URL = "https://you.com/api";

  public static YouApiClient getInstance() {
    return ApplicationManager.getApplication().getService(YouApiClient.class);
  }

  public @Nullable YouSubscription getSubscription(YouAuthenticationResponse auth) {
    var sessionId = auth.getData().getSession().getSessionId();
    var sessionJwt = auth.getData().getSessionJwt();
    var request = new Request.Builder()
        .url(API_BASE_URL + "/payments/orders/subscriptions/current")
        .header("Accept", "application/json")
        .header("Cache-Control", "no-cache")
        .header("User-Agent", "youide CodeGPT")
        .header("Cookie", (
            "stytch_session=" + sessionId + "; "
                + "ydc_stytch_session=" + sessionId + "; "
                + "stytch_session_jwt=" + sessionJwt + "; "
                + "ydc_stytch_session_jwt=" + sessionJwt + "; "))
        .get()
        .build();

    try (var response = new OkHttpClient().newCall(request).execute()) {
      var body = response.body();
      if (body == null || !response.isSuccessful()) {
        return null;
      }
      List<YouSubscription> subscriptions =
          new ObjectMapper().readValue(body.string(), new TypeReference<>() {
          });
      if (subscriptions == null || subscriptions.isEmpty()) {
        return null;
      }
      return subscriptions.get(0);
    } catch (IOException ex) {
      throw new RuntimeException("Could not get You.com subscription", ex);
    }
  }
}
