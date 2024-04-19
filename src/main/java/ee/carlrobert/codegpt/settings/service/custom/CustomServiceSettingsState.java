package ee.carlrobert.codegpt.settings.service.custom;

import static ee.carlrobert.codegpt.settings.service.custom.CustomServiceTemplate.OPENAI;

import com.intellij.util.xmlb.annotations.OptionTag;
import ee.carlrobert.codegpt.util.MapConverter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomServiceSettingsState {

  //#region /v1/chat/completions
  @OptionTag(value = "url")
  private String chatUrl = OPENAI.getUrl();

  @OptionTag(value = "headers")
  private Map<String, String> chatHeaders = new HashMap<>(OPENAI.getHeaders());

  @OptionTag(value = "body", converter = MapConverter.class)
  private Map<String, Object> chatBody = new HashMap<>(OPENAI.getBody());
  //#endregion

  //#region /v1/completions
  @OptionTag(value = "completionUrl")
  private String completionUrl = OPENAI.getUrl().replace("/v1/chat/completions", "/v1/completions");

  @OptionTag(value = "completionHeaders")
  private Map<String, String> completionHeaders = new HashMap<>(OPENAI.getHeaders());

  @OptionTag(value = "completionBody")
  private Map<String, Object> completionBody = new HashMap<>(OPENAI.getBody());
  //#endregion

  private CustomServiceTemplate template = OPENAI;
  private boolean codeCompletionsEnabled = true;
  private int codeCompletionMaxTokens = 128;

  public CustomServiceSettingsState() {
    // Change default model for /v1/completions
    completionBody.put("model", "gpt-3.5-turbo-instruct");

    // Change from messages to prefix + suffix, add stop sequence
    completionBody.remove("messages");
    completionBody.put("prompt", "$OPENAI_PREFIX");
    completionBody.put("suffix", "$OPENAI_SUFFIX");
  }

  public ChatCompletionsSettings getChatCompletionSettings() {
    return new ChatCompletionsSettings(this);
  }

  public CompletionsSettings getCompletionSettings() {
    return new CompletionsSettings(this);
  }

  public CustomServiceTemplate getTemplate() {
    return template;
  }

  public void setTemplate(CustomServiceTemplate template) {
    this.template = template;
  }

  public boolean isCodeCompletionsEnabled() {
    return codeCompletionsEnabled;
  }

  public void setCodeCompletionsEnabled(boolean codeCompletionsEnabled) {
    this.codeCompletionsEnabled = codeCompletionsEnabled;
  }

  public int getCodeCompletionMaxTokens() {
    return codeCompletionMaxTokens;
  }

  public void setCodeCompletionMaxTokens(int codeCompletionMaxTokens) {
    this.codeCompletionMaxTokens = codeCompletionMaxTokens;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomServiceSettingsState that = (CustomServiceSettingsState) o;
    return Objects.equals(chatUrl, that.chatUrl)
        && Objects.equals(chatHeaders, that.chatHeaders)
        && Objects.equals(chatBody, that.chatBody)
        && Objects.equals(completionUrl, that.completionUrl)
        && Objects.equals(completionHeaders, that.completionHeaders)
        && Objects.equals(completionBody, that.completionBody)
        && template == that.template;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        chatUrl, chatHeaders, chatBody,
        completionUrl, completionHeaders, completionBody,
        template);
  }

  public class ChatCompletionsSettings {
    private final CustomServiceSettingsState state;

    private ChatCompletionsSettings(CustomServiceSettingsState state) {
      this.state = state;
    }

    public String getUrl() {
      return state.chatUrl;
    }

    public void setUrl(String url) {
      state.chatUrl = url;
    }

    public Map<String, String> getHeaders() {
      return state.chatHeaders;
    }

    public void setHeaders(Map<String, String> headers) {
      state.chatHeaders = headers;
    }

    public Map<String, Object> getBody() {
      return state.chatBody;
    }

    public void setBody(Map<String, Object> body) {
      state.chatBody = body;
    }
  }

  public class CompletionsSettings {
    private final CustomServiceSettingsState state;

    private CompletionsSettings(CustomServiceSettingsState state) {
      this.state = state;
    }

    public String getUrl() {
      if (state.completionUrl == null) {
        state.completionUrl = state.chatUrl.replace("/v1/chat/completions", "/v1/completions");
      }

      return state.completionUrl;
    }

    public void setUrl(String url) {
      state.completionUrl = url;
    }

    public Map<String, String> getHeaders() {
      return state.completionHeaders;
    }

    public void setHeaders(Map<String, String> headers) {
      state.completionHeaders = headers;
    }

    public Map<String, Object> getBody() {
      return state.completionBody;
    }

    public void setBody(Map<String, Object> body) {
      state.completionBody = body;
    }
  }
}
