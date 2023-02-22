package ee.carlrobert.chatgpt.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResponseUsage(
    @JsonProperty("prompt_tokens") int promptTokens,
    @JsonProperty("completion_tokens") int completionTokens,
    @JsonProperty("total_tokens") int totalTokens) {}
