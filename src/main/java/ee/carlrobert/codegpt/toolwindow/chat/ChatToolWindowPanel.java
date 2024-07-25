package ee.carlrobert.codegpt.toolwindow.chat;

import static java.lang.String.format;
import static java.util.Collections.emptyList;

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
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.ReferencedFile;
import ee.carlrobert.codegpt.actions.IncludeFilesInContextNotifier;
import ee.carlrobert.codegpt.actions.toolwindow.ClearChatWindowAction;
import ee.carlrobert.codegpt.actions.toolwindow.CreateNewConversationAction;
import ee.carlrobert.codegpt.actions.toolwindow.OpenInEditorAction;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.persona.PersonaSettings;
import ee.carlrobert.codegpt.settings.persona.PersonasConfigurable;
import ee.carlrobert.codegpt.settings.service.ProviderChangeNotifier;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.codegpt.CodeGPTUserDetailsNotifier;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ToolWindowFooterNotification;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.AttachImageNotifier;
import ee.carlrobert.llm.client.codegpt.PricingPlan;
import java.awt.BorderLayout;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class ChatToolWindowPanel extends SimpleToolWindowPanel {

  private final ToolWindowFooterNotification selectedFilesNotification;
  private final ToolWindowFooterNotification imageFileAttachmentNotification;
  private final ActionLink upgradePlanLink;
  private ChatToolWindowTabbedPane tabbedPane;

  public ChatToolWindowPanel(
      @NotNull Project project,
      @NotNull Disposable parentDisposable) {
    super(true);
    selectedFilesNotification = new ToolWindowFooterNotification(
        () -> clearSelectedFilesNotification(project));
    imageFileAttachmentNotification = new ToolWindowFooterNotification(() ->
        project.putUserData(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH, ""));
    upgradePlanLink = new ActionLink("Upgrade your plan", event -> {
      BrowserUtil.browse("https://codegpt.ee/#pricing");
    });
    upgradePlanLink.setFont(JBUI.Fonts.smallFont());
    upgradePlanLink.setExternalLinkIcon();
    upgradePlanLink.setVisible(false);

    init(project, parentDisposable);

    var messageBusConnection = project.getMessageBus().connect();
    messageBusConnection.subscribe(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC,
        (IncludeFilesInContextNotifier) this::updateSelectedFilesNotification);
    messageBusConnection.subscribe(AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC,
        (AttachImageNotifier) filePath -> imageFileAttachmentNotification.show(
            Path.of(filePath).getFileName().toString(),
            "File path: " + filePath));
    messageBusConnection.subscribe(ProviderChangeNotifier.getPROVIDER_CHANGE_TOPIC(),
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

  public void updateSelectedFilesNotification(List<ReferencedFile> referencedFiles) {
    if (referencedFiles.isEmpty()) {
      selectedFilesNotification.hideNotification();
      return;
    }

    var referencedFilePaths = referencedFiles.stream()
        .map(ReferencedFile::getFilePath)
        .toList();
    selectedFilesNotification.show(
        referencedFiles.size() + " files selected",
        selectedFilesNotificationDescription(referencedFilePaths));
  }

  private String selectedFilesNotificationDescription(List<String> referencedFilePaths) {
    var html = referencedFilePaths.stream()
        .map(filePath -> format("<li>%s</li>", Paths.get(filePath).getFileName().toString()))
        .collect(Collectors.joining());
    return format("<ul style=\"margin: 4px 12px;\">%s</ul>", html);
  }

  public void clearNotifications(Project project) {
    selectedFilesNotification.hideNotification();
    imageFileAttachmentNotification.hideNotification();

    project.putUserData(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH, "");
    project.putUserData(CodeGPTKeys.SELECTED_FILES, emptyList());
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
    notificationContainer.add(selectedFilesNotification);
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

  public void clearSelectedFilesNotification(Project project) {
    project.putUserData(CodeGPTKeys.SELECTED_FILES, emptyList());
    project.getMessageBus()
        .syncPublisher(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC)
        .filesIncluded(emptyList());
  }

  private static class SelectedPersonaActionLink extends DumbAwareAction implements
      CustomComponentAction {

    private final Project project;

    SelectedPersonaActionLink(Project project) {
      this.project = project;
    }

    @Override
    @NotNull
    public JComponent createCustomComponent(
        @NotNull Presentation presentation,
        @NotNull String place) {
      var link = new ActionLink(getSelectedPersonaName(), (e) -> {
        ShowSettingsUtil.getInstance()
            .showSettingsDialog(project, PersonasConfigurable.class);
      });
      link.setExternalLinkIcon();
      link.setFont(JBUI.Fonts.smallFont());
      link.setBorder(JBUI.Borders.empty(0, 4));
      return link;
    }

    @Override
    public void updateCustomComponent(
        @NotNull JComponent component,
        @NotNull Presentation presentation) {
      ((ActionLink) component).setText(getSelectedPersonaName());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
    }

    private String getSelectedPersonaName() {
      return ApplicationManager.getApplication().getService(PersonaSettings.class)
          .getState()
          .getSelectedPersona()
          .getName();
    }
  }
}
