package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultCompactActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.ActionLink;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.CurrentTheme.Link;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.actions.toolwindow.ClearChatWindowAction;
import ee.carlrobert.codegpt.actions.toolwindow.CreateNewConversationAction;
import ee.carlrobert.codegpt.actions.toolwindow.OpenInEditorAction;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.prompts.PersonaPromptDetailsState;
import ee.carlrobert.codegpt.settings.prompts.PromptsConfigurable;
import ee.carlrobert.codegpt.settings.prompts.PromptsSettings;
import ee.carlrobert.codegpt.settings.service.ProviderChangeNotifier;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTUserDetailsNotifier;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ToolWindowFooterNotification;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.AttachImageNotifier;
import ee.carlrobert.llm.client.codegpt.PricingPlan;
import java.awt.BorderLayout;
import java.nio.file.Path;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class ChatToolWindowPanel extends SimpleToolWindowPanel {

  private final ToolWindowFooterNotification imageFileAttachmentNotification;
  private final ActionLink upgradePlanLink;
  private ChatToolWindowTabbedPane tabbedPane;

  public ChatToolWindowPanel(
      @NotNull Project project,
      @NotNull Disposable parentDisposable) {
    super(true);
    imageFileAttachmentNotification = new ToolWindowFooterNotification(() ->
        project.putUserData(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH, ""));
    upgradePlanLink = new ActionLink("Upgrade your plan", event -> {
      BrowserUtil.browse("https://codegpt.ee/#pricing");
    });
    upgradePlanLink.setFont(JBUI.Fonts.smallFont());
    upgradePlanLink.setExternalLinkIcon();
    upgradePlanLink.setVisible(false);

    ApplicationManager.getApplication().invokeLater(() -> {
      init(project, parentDisposable);
    });

    var messageBusConnection = project.getMessageBus().connect();
    messageBusConnection.subscribe(AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC,
        (AttachImageNotifier) filePath -> imageFileAttachmentNotification.show(
            Path.of(filePath).getFileName().toString(),
            "File path: " + filePath));
    messageBusConnection.subscribe(ProviderChangeNotifier.getTOPIC(),
        (ProviderChangeNotifier) provider -> {
          if (provider == ServiceType.CODEGPT) {
            var userDetails = CodeGPTKeys.CODEGPT_USER_DETAILS.get(project);
            upgradePlanLink.setVisible(
                userDetails != null && userDetails.getPricingPlan() != PricingPlan.INDIVIDUAL);
          } else {
            upgradePlanLink.setVisible(false);
          }
        });
    messageBusConnection.subscribe(CodeGPTUserDetailsNotifier.getCODEGPT_USER_DETAILS_TOPIC(),
        (CodeGPTUserDetailsNotifier) userDetails -> {
          if (userDetails != null) {
            var provider = ApplicationManager.getApplication().getService(GeneralSettings.class)
                .getState()
                .getSelectedService();
            upgradePlanLink.setVisible(provider == ServiceType.CODEGPT
                && userDetails.getPricingPlan() != PricingPlan.INDIVIDUAL);
          }
        });
  }

  public ChatToolWindowTabbedPane getChatTabbedPane() {
    return tabbedPane;
  }

  public void clearImageNotifications(Project project) {
    imageFileAttachmentNotification.hideNotification();

    project.putUserData(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH, "");
  }

  private void init(Project project, Disposable parentDisposable) {
    var conversation = ConversationsState.getCurrentConversation();
    if (conversation == null) {
      conversation = ConversationService.getInstance().startConversation();
    }

    var tabPanel = new ChatToolWindowTabPanel(project, conversation);
    tabbedPane = createTabbedPane(tabPanel, parentDisposable);
    Runnable onAddNewTab = () -> {
      tabbedPane.addNewTab(new ChatToolWindowTabPanel(
          project,
          ConversationService.getInstance().startConversation()));
      repaint();
      revalidate();
    };
    var actionToolbarPanel = new JPanel(new BorderLayout());
    actionToolbarPanel.add(
        createActionToolbar(project, tabbedPane, onAddNewTab).getComponent(),
        BorderLayout.LINE_START);
    actionToolbarPanel.add(upgradePlanLink, BorderLayout.LINE_END);

    setToolbar(actionToolbarPanel);
    var notificationContainer = new JPanel(new BorderLayout());
    notificationContainer.setLayout(new BoxLayout(notificationContainer, BoxLayout.PAGE_AXIS));
    notificationContainer.add(imageFileAttachmentNotification);
    setContent(JBUI.Panels.simplePanel(tabbedPane).addToBottom(notificationContainer));

    Disposer.register(parentDisposable, tabPanel);
  }

  private ActionToolbar createActionToolbar(
      Project project,
      ChatToolWindowTabbedPane tabbedPane,
      Runnable onAddNewTab) {
    var actionGroup = new DefaultCompactActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new CreateNewConversationAction(onAddNewTab));
    actionGroup.add(
        new ClearChatWindowAction(() -> tabbedPane.resetCurrentlyActiveTabPanel(project)));
    actionGroup.addSeparator();
    actionGroup.add(new OpenInEditorAction());
    actionGroup.addSeparator();
    actionGroup.add(new SelectedPersonaActionLink(project));

    var toolbar = ActionManager.getInstance()
        .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, true);
    toolbar.setTargetComponent(this);
    return toolbar;
  }

  private ChatToolWindowTabbedPane createTabbedPane(
      ChatToolWindowTabPanel tabPanel,
      Disposable parentDisposable) {
    var tabbedPane = new ChatToolWindowTabbedPane(parentDisposable);
    tabbedPane.addNewTab(tabPanel);
    return tabbedPane;
  }

  private static class SelectedPersonaActionLink extends DumbAwareAction implements
      CustomComponentAction {

    private final Project project;

    SelectedPersonaActionLink(Project project) {
      this.project = project;
    }

    private void showPromptsSettingsDialog() {
      ShowSettingsUtil.getInstance()
          .showSettingsDialog(project, PromptsConfigurable.class);
    }

    @Override
    @NotNull
    public JComponent createCustomComponent(
        @NotNull Presentation presentation,
        @NotNull String place) {
      var selectedPersona = getSelectedPersona();
      var personaName = selectedPersona.getName();
      if (personaName == null) {
        personaName = "No persona selected";
      }

      var link = new ActionLink(personaName, (e) -> {
        showPromptsSettingsDialog();
      });
      link.setExternalLinkIcon();
      if (selectedPersona.getDisabled()) {
        link.setToolTipText("Persona is disabled");
        link.setForeground(JBUI.CurrentTheme.Label.disabledForeground());
      }
      link.setFont(JBUI.Fonts.smallFont());
      link.setBorder(JBUI.Borders.empty(0, 4));
      return link;
    }

    @Override
    public void updateCustomComponent(
        @NotNull JComponent component,
        @NotNull Presentation presentation) {
      if (component instanceof ActionLink actionLink) {
        var selectedPersona = getSelectedPersona();
        var personaName = selectedPersona.getName();
        if (personaName == null) {
          personaName = "No persona selected";
        }

        if (selectedPersona.getDisabled()) {
          actionLink.setText(personaName + " (disabled)");
          actionLink.setForeground(Link.Foreground.DISABLED);
        } else {
          actionLink.setText(personaName);
          actionLink.setForeground(Link.Foreground.ENABLED);
        }
      }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
    }

    private PersonaPromptDetailsState getSelectedPersona() {
      return ApplicationManager.getApplication().getService(PromptsSettings.class)
          .getState()
          .getPersonas()
          .getSelectedPersona();
    }
  }
}