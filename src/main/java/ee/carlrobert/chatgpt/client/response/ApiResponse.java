package ee.carlrobert.chatgpt.client.response;

import java.util.List;

public record ApiResponse(
    String id,
    String object,
    long created,
    String model,
    List<ApiResponseChoice> choices,
    ApiResponseUsage usage,
    ApiError error) {}
