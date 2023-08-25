package ee.carlrobert.codegpt.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import ee.carlrobert.codegpt.user.subscription.Subscription;
import java.io.IOException;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.jetbrains.annotations.Nullable;

@Service
public final class ApiClient {

  private static final String API_BASE_URL = "http://localhost:8000/api";
  private final OkHttpClient httpClient;

  private ApiClient() {
    httpClient = new OkHttpClient();
  }

  public static ApiClient getInstance() {
    return ApplicationManager.getApplication().getService(ApiClient.class);
  }

  public @Nullable Subscription getSubscription(String accessToken) {
    try {
      var body = httpClient.newCall(new Request.Builder()
              .url(API_BASE_URL + "/subscriptions")
              .header("Authorization", accessToken)
              .get()
              .build())
          .execute()
          .body();
      if (body == null) {
        return null;
      }
      return new ObjectMapper().readValue(body.string(), new TypeReference<>() {});
    } catch (IOException e) {
      throw new RuntimeException("Unable to obtain subscriptions", e);
    }
  }

  public void authenticate(String email, String password, Callback callback) {
    try {
      httpClient.newCall(new Request.Builder()
              .url(API_BASE_URL + "/auth")
              .post(RequestBody.create(
                  new ObjectMapper()
                      .writerWithDefaultPrettyPrinter()
                      .writeValueAsString(Map.of(
                          "email", email,
                          "password", password)),
                  MediaType.parse("application/json")))
              .build())
          .enqueue(callback);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not process request", e);
    }
  }

  public void refreshToken(String refreshToken, Callback callback) {
    try {
      httpClient.newCall(new Request.Builder()
              .url(API_BASE_URL + "/refresh-token")
              .patch(RequestBody.create(
                  new ObjectMapper()
                      .writerWithDefaultPrettyPrinter()
                      .writeValueAsString(Map.of(
                          "refreshToken", refreshToken)),
                  MediaType.parse("application/json")))
              .build())
          .enqueue(callback);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to refresh token", e);
    }
  }
}
