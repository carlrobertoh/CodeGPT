package ee.carlrobert.codegpt.settings.service.custom;

import java.util.HashMap;
import java.util.Map;

public enum CustomServiceTemplate {

  // Cloud providers
  ANYSCALE(
      "Anyscale",
      "https://docs.endpoints.anyscale.com/",
      "https://api.endpoints.anyscale.com/v1/chat/completions",
      getDefaultHeadersWithAuthentication(),
      getDefaultBodyParams(Map.of(
          "model", "mistralai/Mixtral-8x7B-Instruct-v0.1",
          "max_tokens", 1024))),
  AZURE(
      "Azure OpenAI",
      "https://learn.microsoft.com/en-us/azure/ai-services/openai/reference#chat-completions",
      "https://{your-resource-name}.openai.azure.com/openai/deployments/{deployment-id}/chat/completions?api-version=2023-05-15",
      getDefaultHeaders("api-key", "$CUSTOM_SERVICE_API_KEY"),
      getDefaultBodyParams(Map.of())),
  DEEP_INFRA(
      "DeepInfra",
      "https://deepinfra.com/docs/advanced/openai_api",
      "https://api.deepinfra.com/v1/openai/chat/completions",
      getDefaultHeadersWithAuthentication(),
      getDefaultBodyParams(Map.of(
          "model", "meta-llama/Llama-2-70b-chat-hf",
          "max_tokens", 1024))),
  FIREWORKS(
      "Fireworks",
      "https://readme.fireworks.ai/reference/createchatcompletion",
      "https://api.fireworks.ai/inference/v1/chat/completions",
      getDefaultHeadersWithAuthentication(),
      getDefaultBodyParams(Map.of(
          "model", "accounts/fireworks/models/llama-v2-7b-chat",
          "max_tokens", 1024))),
  GROQ(
      "Groq",
      "https://docs.api.groq.com/md/openai.oas.html",
      "https://api.groq.com/openai/v1/chat/completions",
      getDefaultHeadersWithAuthentication(),
      getDefaultBodyParams(Map.of(
          "model", "codellama-34b",
          "max_tokens", 1024))),
  OPENAI(
      "OpenAI",
      "https://platform.openai.com/docs/api-reference/chat",
      "https://api.openai.com/v1/chat/completions",
      getDefaultHeaders("Authorization", "Bearer $CUSTOM_SERVICE_API_KEY"),
      getDefaultBodyParams(Map.of(
          "model", "gpt-4",
          "max_tokens", 1024))),
  PERPLEXITY(
      "Perplexity AI",
      "https://docs.perplexity.ai/reference/post_chat_completions",
      "https://api.perplexity.ai/chat/completions",
      getDefaultHeadersWithAuthentication(),
      getDefaultBodyParams(Map.of(
          "model", "codellama",
          "max_tokens", 1024))),
  TOGETHER(
      "Together AI",
      "https://docs.together.ai/docs/openai-api-compatibility",
      "https://api.together.xyz/v1/chat/completions",
      getDefaultHeaders("Authorization", "Bearer $CUSTOM_SERVICE_API_KEY"),
      getDefaultBodyParams(Map.of(
          "model", "deepseek-ai/deepseek-coder-33b-instruct",
          "max_tokens", 1024))),

  // Local providers
  OLLAMA(
      "Ollama",
      "https://github.com/ollama/ollama/blob/main/docs/openai.md",
      "http://localhost:11434/v1/chat/completions",
      getDefaultHeaders(),
      getDefaultBodyParams(Map.of("model", "codellama"))),
  LLAMA_CPP(
      "LLaMA C/C++",
      "https://github.com/ggerganov/llama.cpp/blob/master/examples/server/README.md",
      "http://localhost:8080/v1/chat/completions",
      getDefaultHeaders(),
      getDefaultBodyParams(Map.of()));

  private final String name;
  private final String docsUrl;
  private final String url;
  private final Map<String, String> headers;
  private final Map<String, ?> body;

  CustomServiceTemplate(
      String name,
      String docsUrl,
      String url,
      Map<String, String> headers,
      Map<String, ?> body) {
    this.name = name;
    this.docsUrl = docsUrl;
    this.url = url;
    this.headers = headers;
    this.body = body;
  }

  public String getName() {
    return name;
  }

  public String getDocsUrl() {
    return docsUrl;
  }

  public String getUrl() {
    return url;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public Map<String, ?> getBody() {
    return body;
  }

  @Override
  public String toString() {
    return name;
  }

  private static Map<String, String> getDefaultHeadersWithAuthentication() {
    return getDefaultHeaders("Authorization", "Bearer $CUSTOM_SERVICE_API_KEY");
  }

  private static Map<String, String> getDefaultHeaders() {
    return getDefaultHeaders(Map.of());
  }

  private static Map<String, String> getDefaultHeaders(String key, String value) {
    return getDefaultHeaders(Map.of(key, value));
  }

  private static Map<String, String> getDefaultHeaders(Map<String, String> additionalHeaders) {
    var defaultHeaders = new HashMap<>(Map.of(
        "Content-Type", "application/json",
        "X-LLM-Application-Tag", "codegpt"));
    defaultHeaders.putAll(additionalHeaders);
    return defaultHeaders;
  }

  private static Map<String, ?> getDefaultBodyParams(Map<String, ?> additionalParams) {
    var defaultParams = new HashMap<String, Object>(Map.of(
        "stream", true,
        "messages", "$OPENAI_MESSAGES",
        "temperature", 0.1));
    defaultParams.putAll(additionalParams);
    return defaultParams;
  }
}
