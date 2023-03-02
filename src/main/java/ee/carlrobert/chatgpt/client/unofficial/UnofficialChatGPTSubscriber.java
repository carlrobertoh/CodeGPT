package ee.carlrobert.chatgpt.client.unofficial;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.carlrobert.chatgpt.client.Subscriber;
import ee.carlrobert.chatgpt.client.unofficial.response.Response;
import ee.carlrobert.chatgpt.client.unofficial.response.ResponseDetail;
import ee.carlrobert.chatgpt.client.unofficial.response.ResponseError;
import java.util.function.Consumer;

public class UnofficialChatGPTSubscriber extends Subscriber<Response> {

  private final Consumer<String> responseConsumer;
  private final Consumer<Response> onCompleteCallback;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private Response lastReceivedResponse;

  public UnofficialChatGPTSubscriber(
      Consumer<String> responseConsumer,
      Consumer<Response> onCompleteCallback) {
    this.responseConsumer = responseConsumer;
    this.onCompleteCallback = onCompleteCallback;
  }

  protected void onRequestComplete() {
    onCompleteCallback.accept(lastReceivedResponse);
  }

  protected void onErrorOccurred() {
    responseConsumer.accept("\nSomething went wrong. Please try again later.");
  }

  protected void send(String responsePayload, String token) {
    if (!responsePayload.isEmpty() && isValidJson(responsePayload)) {
      try {
        var response = objectMapper.readValue(responsePayload, Response.class);
        var author = response.getMessage().getAuthor();
        if (author != null && "assistant".equals(author.getRole())) {
          var message = response.getFullMessage();
          if (lastReceivedResponse != null) {
            message = message.replace(lastReceivedResponse.getFullMessage(), "");
          }
          lastReceivedResponse = response;
          this.responseConsumer.accept(message);
        }
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Unable to deserialize the payload", e);
      }
    } else {
      if (token != null && !token.isEmpty() && isValidJson(token)) {
        try {
          var response = objectMapper.readValue(token, ResponseDetail.class);
          this.responseConsumer.accept(response.getDetail());
        } catch (JsonProcessingException e) {
          tryProcessingErrorResponse(token);
        }
      }
    }
  }

  private void tryProcessingErrorResponse(String jsonPayload) {
    try {
      var error = objectMapper.readValue(jsonPayload, ResponseError.class);
      if ("invalid_api_key".equals(error.getDetail().getCode())) {
        responseConsumer.accept(error.getDetail().getMessage());
      }
      future.complete(null);
    } catch (JsonProcessingException e) {
      future.completeExceptionally(e);
    }
  }

  private boolean isValidJson(String json) {
    ObjectMapper mapper = new ObjectMapper()
        .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    try {
      mapper.readTree(json);
    } catch (JacksonException e) {
      return false;
    }
    return true;
  }
}
