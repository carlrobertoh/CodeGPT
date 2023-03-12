package ee.carlrobert.codegpt.ide.settings;

import com.intellij.openapi.options.Configurable;
import ee.carlrobert.codegpt.ide.conversations.ConversationsState;
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
        !settingsComponent.getProxyHost().equals(settings.proxyHost) ||
        settingsComponent.getProxyPort() != settings.proxyPort ||
        !settingsComponent.getProxyType().equals(settings.proxyType) ||
        settingsComponent.isProxyAuthSelected() != settings.isProxyAuthSelected ||
        !settingsComponent.getProxyAuthUsername().equals(settings.proxyUsername) ||
        !settingsComponent.getProxyAuthPassword().equals(settings.proxyPassword) ||
        !settingsComponent.getChatCompletionBaseModel().equals(settings.chatCompletionBaseModel) ||
        !settingsComponent.getTextCompletionBaseModel().equals(settings.textCompletionBaseModel) ||
        !settingsComponent.getReverseProxyUrl().equals(settings.reverseProxyUrl) ||
        isClientChanged(settings);
  }

  @Override
  public void apply() {
    var settings = SettingsState.getInstance();

    if (isClientChanged(settings)) {
      ConversationsState.getInstance().setCurrentConversation(null);
    }

    settings.isGPTOptionSelected = settingsComponent.isGPTOptionSelected();
    settings.isChatGPTOptionSelected = settingsComponent.isChatGPTOptionSelected();
    settings.accessToken = settingsComponent.getAccessToken();
    settings.proxyHost = settingsComponent.getProxyHost();
    settings.proxyPort = settingsComponent.getProxyPort();
    settings.proxyType = settingsComponent.getProxyType();
    settings.isProxyAuthSelected = settingsComponent.isProxyAuthSelected();
    settings.proxyUsername = settingsComponent.getProxyAuthUsername();
    settings.proxyPassword = settingsComponent.getProxyAuthPassword();
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
    settingsComponent.setProxyHost(settings.proxyHost);
    settingsComponent.setProxyPort(settings.proxyPort);
    settingsComponent.setProxyType(settings.proxyType);
    settingsComponent.setUseProxyAuthentication(settings.isProxyAuthSelected);
    settingsComponent.setProxyUsername(settings.proxyUsername);
    settingsComponent.setProxyPassword(settings.proxyPassword);
    settingsComponent.setApiKey(settings.apiKey);
    settingsComponent.setReverseProxyUrl(settings.reverseProxyUrl);
    settingsComponent.setChatCompletionBaseModel(settings.chatCompletionBaseModel);
    settingsComponent.setTextCompletionBaseModel(settings.textCompletionBaseModel);
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }

  private boolean isClientChanged(SettingsState settings) {
    return settingsComponent.isGPTOptionSelected() != settings.isGPTOptionSelected ||
        settingsComponent.isChatCompletionOptionSelected() != settings.isChatCompletionOptionSelected ||
        settingsComponent.isTextCompletionOptionSelected() != settings.isTextCompletionOptionSelected ||
        settingsComponent.isChatGPTOptionSelected() != settings.isChatGPTOptionSelected;
  }
}
