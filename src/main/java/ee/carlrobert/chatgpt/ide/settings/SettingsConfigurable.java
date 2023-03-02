package ee.carlrobert.chatgpt.ide.settings;

import com.intellij.openapi.options.Configurable;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class SettingsConfigurable implements Configurable {

  private SettingsComponent settingsComponent;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "CodeGPT: Settings";
  }

  @Override
  public JComponent getPreferredFocusedComponent() {
    return settingsComponent.getPreferredFocusedComponent();
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    var settings = SettingsState.getInstance();
    settingsComponent = new SettingsComponent(settings);
    return settingsComponent.getPanel();
  }

  @Override
  public boolean isModified() {
    var settings = SettingsState.getInstance();
    return !settingsComponent.getApiKey().equals(settings.apiKey) ||
        !settingsComponent.getAccessToken().equals(settings.accessToken) ||
        !settingsComponent.getReverseProxyUrl().equals(settings.reverseProxyUrl) ||
        !settingsComponent.getChatCompletionBaseModel().equals(settings.chatCompletionBaseModel) ||
        !settingsComponent.getTextCompletionBaseModel().equals(settings.textCompletionBaseModel) ||
        settingsComponent.isGPTOptionSelected() != settings.isGPTOptionSelected ||
        settingsComponent.isChatCompletionOptionSelected() != settings.isChatCompletionOptionSelected ||
        settingsComponent.isTextCompletionOptionSelected() != settings.isTextCompletionOptionSelected ||
        settingsComponent.isChatGPTOptionSelected() != settings.isChatGPTOptionSelected;
  }

  @Override
  public void apply() {
    var settings = SettingsState.getInstance();
    settings.isGPTOptionSelected = settingsComponent.isGPTOptionSelected();
    settings.isChatGPTOptionSelected = settingsComponent.isChatGPTOptionSelected();
    settings.accessToken = settingsComponent.getAccessToken();
    settings.apiKey = settingsComponent.getApiKey();
    settings.reverseProxyUrl = settingsComponent.getReverseProxyUrl();
    settings.chatCompletionBaseModel = settingsComponent.getChatCompletionBaseModel();
    settings.isChatCompletionOptionSelected = settingsComponent.isChatCompletionOptionSelected();
    settings.isTextCompletionOptionSelected = settingsComponent.isTextCompletionOptionSelected();
    settings.textCompletionBaseModel = settingsComponent.getTextCompletionBaseModel();
  }

  @Override
  public void reset() {
    var settings = SettingsState.getInstance();
    settingsComponent.setUseGPTOptionSelected(settings.isGPTOptionSelected);
    settingsComponent.setUseChatCompletionSelected(settings.isChatCompletionOptionSelected);
    settingsComponent.setUseTextCompletionSelected(settings.isTextCompletionOptionSelected);
    settingsComponent.setUseChatGPTOptionSelected(settings.isChatGPTOptionSelected);
    settingsComponent.setAccessToken(settings.accessToken);
    settingsComponent.setApiKey(settings.apiKey);
    settingsComponent.setReverseProxyUrl(settings.reverseProxyUrl);
    settingsComponent.setChatCompletionBaseModel(settings.chatCompletionBaseModel);
    settingsComponent.setTextCompletionBaseModel(settings.textCompletionBaseModel);
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }
}
