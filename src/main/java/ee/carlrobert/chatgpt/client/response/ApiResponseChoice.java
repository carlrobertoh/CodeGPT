package ee.carlrobert.chatgpt.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResponseChoice(
    String text,
    int index,
    Object logprobs,
    @JsonProperty("finish_reason") String finishReason) {}
