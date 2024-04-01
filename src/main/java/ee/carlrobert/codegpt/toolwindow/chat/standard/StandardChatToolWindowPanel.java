package ee.carlrobert.codegpt.toolwindow.chat.standard;

import static java.lang.String.format;
import static java.util.Collections.emptyList;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultCompactActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.ReferencedFile;
import ee.carlrobert.codegpt.actions.IncludeFilesInContextNotifier;
import ee.carlrobert.codegpt.actions.toolwindow.ClearChatWindowAction;
import ee.carlrobert.codegpt.actions.toolwindow.CreateNewConversationAction;
import ee.carlrobert.codegpt.actions.toolwindow.OpenInEditorAction;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ToolWindowFooterNotification;
import ee.carlrobert.codegpt.toolwindow.chat.ui.textarea.AttachImageNotifier;
import java.awt.BorderLayout;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class StandardChatToolWindowPanel extends SimpleToolWindowPanel {

  private final ToolWindowFooterNotification selectedFilesNotification;
  private final ToolWindowFooterNotification imageFileAttachmentNotification;
  private StandardChatToolWindowTabbedPane tabbedPane;

  public StandardChatToolWindowPanel(
      @NotNull Project project,
      @NotNull Disposable parentDisposable) {
    super(true);
    selectedFilesNotification = new ToolWindowFooterNotification(
        getSelectedFilesNotificationLabel(project),
        () -> clearSelectedFilesNotification(project));
    imageFileAttachmentNotification = new ToolWindowFooterNotification(() ->
        project.putUserData(CodeGPTKeys.IMAGE_ATTACHMENT_FILE_PATH, ""));
    init(project, parentDisposable);

    project.getMessageBus()
        .connect()
        .subscribe(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC,
            (IncludeFilesInContextNotifier) this::displaySelectedFilesNotification);
    project.getMessageBus()
        .connect()
        .subscribe(AttachImageNotifier.IMAGE_ATTACHMENT_FILE_PATH_TOPIC,
            (AttachImageNotifier) filePath -> imageFileAttachmentNotification.show(
                Path.of(filePath).getFileName().toString(),
                "File path: " + filePath));
  }

  public StandardChatToolWindowTabbedPane getChatTabbedPane() {
    return tabbedPane;
  }

  public void displaySelectedFilesNotification(List<ReferencedFile> referencedFiles) {
    if (referencedFiles.isEmpty()) {
      return;
    }

    var referencedFilePaths = referencedFiles.stream()
        .map(ReferencedFile::getFilePath)
        .collect(Collectors.toList());
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

    var tabPanel = new StandardChatToolWindowTabPanel(project, conversation);
    tabbedPane = createTabbedPane(tabPanel, parentDisposable);
    Runnable onAddNewTab = () -> {
      tabbedPane.addNewTab(new StandardChatToolWindowTabPanel(
          project,
          ConversationService.getInstance().startConversation()));
      repaint();
      revalidate();
    };
    var actionToolbarPanel = new JPanel(new BorderLayout());
    actionToolbarPanel.add(
        createActionToolbar(project, tabbedPane, onAddNewTab).getComponent(),
        BorderLayout.LINE_START);

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
      StandardChatToolWindowTabbedPane tabbedPane,
      Runnable onAddNewTab) {
    var actionGroup = new DefaultCompactActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new CreateNewConversationAction(onAddNewTab));
    actionGroup.add(
        new ClearChatWindowAction(() -> tabbedPane.resetCurrentlyActiveTabPanel(project)));
    actionGroup.addSeparator();
    actionGroup.add(new OpenInEditorAction());

    var toolbar = ActionManager.getInstance()
        .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, true);
    toolbar.setTargetComponent(this);
    return toolbar;
  }

  private StandardChatToolWindowTabbedPane createTabbedPane(
      StandardChatToolWindowTabPanel tabPanel,
      Disposable parentDisposable) {
    var tabbedPane = new StandardChatToolWindowTabbedPane(parentDisposable);
    tabbedPane.addNewTab(tabPanel);
    return tabbedPane;
  }

  public void clearSelectedFilesNotification(Project project) {
    project.putUserData(CodeGPTKeys.SELECTED_FILES, emptyList());
    project.getMessageBus()
        .syncPublisher(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC)
        .filesIncluded(emptyList());
  }

  private static String getSelectedFilesNotificationLabel(Project project) {
    var selectedFiles = project.getUserData(CodeGPTKeys.SELECTED_FILES);
    var fileCount = selectedFiles == null ? 0 : selectedFiles.size();
    return fileCount + " files selected";
  }
}
