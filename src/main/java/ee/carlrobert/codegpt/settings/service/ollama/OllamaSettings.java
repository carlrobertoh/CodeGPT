package ee.carlrobert.codegpt.settings.service.ollama;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "CodeGPT_OllamaSettings_210", storages = @Storage("CodeGPT_OllamaSettings_210.xml"))
public class OllamaSettings implements PersistentStateComponent<OllamaSettingsState> {

    private OllamaSettingsState state = new OllamaSettingsState();

    @Override
    @NotNull
    public OllamaSettingsState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull OllamaSettingsState state) {
        this.state = state;
    }

    public static OllamaSettingsState getCurrentState() {
        return getInstance().getState();
    }

    public static OllamaSettings getInstance() {
        return ApplicationManager.getApplication().getService(OllamaSettings.class);
    }

    public boolean isModified(OllamaSettingsForm form) {
        return !form.getCurrentState().equals(state);
    }
}
