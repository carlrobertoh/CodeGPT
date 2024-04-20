package ee.carlrobert.codegpt.settings.service.ollama;

import java.util.Objects;

public class OllamaSettingsState {

    private String host = "http://localhost:11437";
    private String model = "";
    private boolean codeCompletionsEnabled = true;
    private int codeCompletionMaxTokens = 128;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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
        OllamaSettingsState that = (OllamaSettingsState) o;
        return Objects.equals(host, that.host)
                && Objects.equals(model, that.model)
                && codeCompletionsEnabled == that.codeCompletionsEnabled
                && codeCompletionMaxTokens == that.codeCompletionMaxTokens;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, model, codeCompletionsEnabled, codeCompletionMaxTokens);
    }
}
