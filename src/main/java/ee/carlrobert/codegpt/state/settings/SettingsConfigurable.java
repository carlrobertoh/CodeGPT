package ee.carlrobert.codegpt.state.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.ProjectUtil;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatContentManagerService;
import ee.carlrobert.codegpt.toolwindow.chat.ToolWindowTabPanelFactory;
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
        !settingsComponent.getOrganization().equals(settings.organization) ||
        !settingsComponent.getDisplayName().equals(settings.displayName) ||
        settingsComponent.isUseOpenAIAccountName() != settings.useOpenAIAccountName ||
        isModelChanged(settings) ||
        isClientChanged(settings);
  }

  @Override
  public void apply() {
    var settings = SettingsState.getInstance();
    var isModelChanged = isModelChanged(settings);

    if (isModelChanged) {
      EncodingManager.getInstance()
          .setEncoding(settings.isChatCompletionOptionSelected ? settings.chatCompletionBaseModel
              : settings.textCompletionBaseModel);
    }

    if (isClientChanged(settings) || isModelChanged) {
      ConversationsState.getInstance().setCurrentConversation(null);
      var project = ProjectUtil.guessCurrentProject(
          settingsComponent.getPanel()); // TODO: Find a better way
      project.getService(ChatContentManagerService.class)
          .tryFindChatTabbedPane()
          .ifPresent(tabbedPane -> {
            tabbedPane.clearAll();
            var tabPanel = ToolWindowTabPanelFactory.getTabPanel(project);
            tabPanel.displayLandingView();
            tabbedPane.addNewTab(tabPanel);
          });
    }

    settings.apiKey = settingsComponent.getApiKey();
    settings.organization = settingsComponent.getOrganization();
    settings.displayName = settingsComponent.getDisplayName();
    settings.useOpenAIAccountName = settingsComponent.isUseOpenAIAccountName();
    settings.chatCompletionBaseModel = settingsComponent.getChatCompletionBaseModel().getCode();
    settings.isChatCompletionOptionSelected = settingsComponent.isChatCompletionOptionSelected();
    settings.isTextCompletionOptionSelected = settingsComponent.isTextCompletionOptionSelected();
    settings.textCompletionBaseModel = settingsComponent.getTextCompletionBaseModel().getCode();
  }

  @Override
  public void reset() {
    var settings = SettingsState.getInstance();
    settingsComponent.setUseChatCompletionSelected(settings.isChatCompletionOptionSelected);
    settingsComponent.setUseTextCompletionSelected(settings.isTextCompletionOptionSelected);
    settingsComponent.setApiKey(settings.apiKey);
    settingsComponent.setOrganization(settings.organization);
    settingsComponent.setDisplayName(settings.displayName);
    settingsComponent.setUseOpenAIAccountNameCheckBox(settings.useOpenAIAccountName);
    settingsComponent.setChatCompletionBaseModel(settings.chatCompletionBaseModel);
    settingsComponent.setTextCompletionBaseModel(settings.textCompletionBaseModel);
  }

  @Override
  public void disposeUIResources() {
    settingsComponent = null;
  }

  private boolean isClientChanged(SettingsState settings) {
    return settingsComponent.isChatCompletionOptionSelected()
        != settings.isChatCompletionOptionSelected ||
        settingsComponent.isTextCompletionOptionSelected()
            != settings.isTextCompletionOptionSelected;
  }

  private boolean isModelChanged(SettingsState settings) {
    return !settingsComponent.getChatCompletionBaseModel().getCode()
        .equals(settings.chatCompletionBaseModel) ||
        !settingsComponent.getTextCompletionBaseModel().getCode()
            .equals(settings.textCompletionBaseModel);
  }
}
