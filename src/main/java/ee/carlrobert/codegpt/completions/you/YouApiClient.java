package ee.carlrobert.codegpt.completions.you;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
public final class YouApiClient {

  private static final String API_BASE_URL = "https://web.stytch.com/sdk";
  private static final String publicToken = "public-token-live-507a52ad-7e69-496b-aee0-1c9863c7c819";

  public static YouApiClient getInstance() {
    return ApplicationManager.getApplication().getService(YouApiClient.class);
  }

  public void authenticate(String email, String password, Callback callback) {
    try {
      new OkHttpClient()
          .newCall(new Request.Builder()
              .url(API_BASE_URL + "/v1/passwords/authenticate")
              .headers(Headers.of(
                  "content-type", "application/json",
                  "authority", "web.stytch.com",
                  "user-agent", "youide CodeGPT",
                  "authorization", "Basic " + Base64.getEncoder().encodeToString((publicToken + ":" + publicToken).getBytes()),
                  "x-sdk-client", "eyJldmVudF9pZCI6ImV2ZW50LWlkLWY5YmU4YWU5LWE3MjctNGFlYy1hNzY0LTk4NDg1NDFkZjcwYSIsImFwcF9zZXNzaW9uX2lkIjoiYXBwLXNlc3Npb24taWQtYjY1NzcwZjMtMWFkMy00YjlhLWFjYzctMzJjNWQyMGMxNGU0IiwicGVyc2lzdGVudF9pZCI6InBlcnNpc3RlbnQtaWQtYzY0M2M0YTMtZDg5MC00ZGJkLTk3YjQtMjY0MmFlODdkMTZhIiwiY2xpZW50X3NlbnRfYXQiOiIyMDIzLTA5LTAxVDIyOjMwOjU1LjIzNFoiLCJ0aW1lem9uZSI6IkV1cm9wZS9UYWxsaW5uIiwiYXBwIjp7ImlkZW50aWZpZXIiOiJ5b3UuY29tIn0sInNkayI6eyJpZGVudGlmaWVyIjoiU3R5dGNoLmpzIEphdmFzY3JpcHQgU0RLIC0gWU9VLkNPTSBERUJVRyBCVUlMRCIsInZlcnNpb24iOiI0LjAuMCJ9fQ==",
                  "x-sdk-parent-host", "https://you.com"
              ))
              .post(RequestBody.create(new ObjectMapper()
                  .writerWithDefaultPrettyPrinter()
                  .writeValueAsString(Map.of(
                      "email", email,
                      "password", password,
                      "session_duration_minutes", 129_600))
                  .getBytes(StandardCharsets.UTF_8)))
              .build())
          .enqueue(callback);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not process request", e);
    }
  }
}
