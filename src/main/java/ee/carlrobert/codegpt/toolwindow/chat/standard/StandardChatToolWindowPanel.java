package ee.carlrobert.codegpt.toolwindow.chat.standard;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultCompactActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.actions.IncludeFilesInContextNotifier;
import ee.carlrobert.codegpt.actions.toolwindow.ClearChatWindowAction;
import ee.carlrobert.codegpt.actions.toolwindow.CreateNewConversationAction;
import ee.carlrobert.codegpt.actions.toolwindow.OpenInEditorAction;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.ui.SelectedFilesNotification;
import ee.carlrobert.embedding.ReferencedFile;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class StandardChatToolWindowPanel extends SimpleToolWindowPanel {

  private final SelectedFilesNotification selectedFilesNotification;
  private StandardChatToolWindowTabbedPane tabbedPane;

  public StandardChatToolWindowPanel(
      @NotNull Project project,
      @NotNull Disposable parentDisposable) {
    super(true);
    selectedFilesNotification = new SelectedFilesNotification(project);
    init(project, selectedFilesNotification, parentDisposable);

    project.getMessageBus()
        .connect()
        .subscribe(IncludeFilesInContextNotifier.FILES_INCLUDED_IN_CONTEXT_TOPIC,
            (IncludeFilesInContextNotifier) this::displaySelectedFilesNotification);
  }

  public void displaySelectedFilesNotification(List<ReferencedFile> referencedFiles) {
    selectedFilesNotification.displaySelectedFilesNotification(referencedFiles);
  }

  public void clearSelectedFilesNotification() {
    selectedFilesNotification.clearSelectedFilesNotification();
  }

  private void init(
      Project project,
      SelectedFilesNotification selectedFilesNotification,
      Disposable parentDisposable) {
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
    setContent(
        JBUI.Panels.simplePanel(tabbedPane).addToBottom(selectedFilesNotification));

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

  public StandardChatToolWindowTabbedPane getChatTabbedPane() {
    return tabbedPane;
  }
}
