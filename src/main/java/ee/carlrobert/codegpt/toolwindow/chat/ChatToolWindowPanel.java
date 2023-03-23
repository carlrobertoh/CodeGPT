package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.actions.CreateNewConversationAction;
import ee.carlrobert.codegpt.toolwindow.chat.actions.OpenInEditorAction;
import ee.carlrobert.codegpt.toolwindow.chat.actions.UsageToolbarLabelAction;
import org.jetbrains.annotations.NotNull;

public class ChatToolWindowPanel extends SimpleToolWindowPanel {

  public ChatToolWindowPanel(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    super(true);
    initialize(project, toolWindow);
  }

  private void initialize(Project project, ToolWindow toolWindow) {
    var tabPanel = new ChatToolWindowTabPanel(project);
    var tabbedPane = createTabbedPane(tabPanel);
    setToolbar(createActionToolbar(project, tabbedPane).getComponent());
    setContent(tabbedPane);

    var contentManagerService = project.getService(ChatContentManagerService.class);
    if (contentManagerService.isChatTabSelected(toolWindow.getContentManager())) {
      var conversation = ConversationsState.getCurrentConversation();
      if (conversation == null) {
        tabPanel.displayLandingView();
      } else {
        tabPanel.displayConversation(conversation);
      }
    }
  }

  private ActionToolbar createActionToolbar(Project project, ChatTabbedPane tabbedPane) {
    var actionGroup = new DefaultActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new CreateNewConversationAction(() -> {
      var conversation = ConversationsState.getInstance().startConversation();
      var panel = new ChatToolWindowTabPanel(project);
      panel.setConversationId(conversation.getId());
      panel.displayLandingView();
      tabbedPane.addNewTab(panel);
      repaint();
      revalidate();
    }));
    actionGroup.add(new OpenInEditorAction());
    actionGroup.addSeparator();
    actionGroup.add(new UsageToolbarLabelAction());

    // TODO: Data usage not enabled in stream mode https://community.openai.com/t/usage-info-in-api-responses/18862/11
    // actionGroup.add(new TokenToolbarLabelAction());

    return ActionManager.getInstance().createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, false);
  }

  private ChatTabbedPane createTabbedPane(ChatToolWindowTabPanel tabPanel) {
    var tabbedPane = new ChatTabbedPane();
    tabbedPane.addNewTab(tabPanel);
    return tabbedPane;
  }
}
