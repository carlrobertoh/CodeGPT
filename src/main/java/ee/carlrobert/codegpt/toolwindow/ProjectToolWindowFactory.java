package ee.carlrobert.codegpt.toolwindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import ee.carlrobert.codegpt.account.AccountDetailsState;
import ee.carlrobert.codegpt.client.ClientFactory;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatTabbedPane;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowPanel;
import ee.carlrobert.codegpt.toolwindow.chat.actions.CreateNewConversationAction;
import ee.carlrobert.codegpt.toolwindow.chat.actions.OpenInEditorAction;
import ee.carlrobert.codegpt.toolwindow.chat.actions.UsageToolbarLabelAction;
import ee.carlrobert.codegpt.toolwindow.conversations.ConversationsToolWindow;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class ProjectToolWindowFactory implements ToolWindowFactory, DumbAware {

  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    var chatToolWindow = new ChatToolWindowPanel(project);
    var conversationsToolWindow = new ConversationsToolWindow(project);
    var toolWindowPanel = new SimpleToolWindowPanel(true);

    var chatTabbedPane = new ChatTabbedPane();
    chatTabbedPane.addTab("Chat 1", chatToolWindow);

    toolWindowPanel.setToolbar(createActionToolbar(project, chatTabbedPane, toolWindowPanel).getComponent());
    toolWindowPanel.setContent(chatTabbedPane);

    addContent(toolWindow, toolWindowPanel, "Chat");
    addContent(toolWindow, conversationsToolWindow.getContent(), "Conversation History");
    toolWindow.addContentManagerListener(new ContentManagerListener() {
      public void selectionChanged(@NotNull ContentManagerEvent event) {
        var content = event.getContent();
        if ("Conversation History".equals(content.getTabName()) && content.isSelected()) {
          conversationsToolWindow.refresh();
        } else if ("Chat".equals(content.getTabName()) && content.isSelected()) {
          ClientFactory.getBillingClient()
              .getCreditUsageAsync(creditUsage -> {
                var accountDetails = AccountDetailsState.getInstance();
                accountDetails.totalAmountGranted = creditUsage.getTotalGranted();
                accountDetails.totalAmountUsed = creditUsage.getTotalUsed();
              });
        }
      }
    });

    var contentManagerService = project.getService(ContentManagerService.class);
    if (contentManagerService.isChatTabSelected(toolWindow.getContentManager())) {
      var conversation = ConversationsState.getCurrentConversation();
      if (conversation == null) {
        chatToolWindow.displayLandingView();
      } else {
        chatToolWindow.displayConversation(conversation);
      }
    }
  }

  public void addContent(ToolWindow toolWindow, JComponent panel, String displayName) {
    var contentManager = toolWindow.getContentManager();
    var content = contentManager.getFactory().createContent(panel, displayName, false);
    contentManager.addContent(content);
  }

  private ActionToolbar createActionToolbar(Project project, ChatTabbedPane tabbedPane, SimpleToolWindowPanel toolWindowPanel) {
    var actionGroup = new DefaultActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new CreateNewConversationAction(() -> {
      tabbedPane.addTab("Chat " + (tabbedPane.getTabCount() + 1), new ChatToolWindowPanel(project));
      tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
      toolWindowPanel.repaint();
      toolWindowPanel.revalidate();
    }));
    actionGroup.add(new OpenInEditorAction());
    actionGroup.addSeparator();
    actionGroup.add(new UsageToolbarLabelAction());

    // TODO: Data usage not enabled in stream mode https://community.openai.com/t/usage-info-in-api-responses/18862/11
    // actionGroup.add(new TokenToolbarLabelAction());

    return ActionManager.getInstance().createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, false);
  }
}
