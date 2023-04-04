package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.actions.CreateNewConversationAction;
import ee.carlrobert.codegpt.toolwindow.chat.actions.OpenInEditorAction;
import ee.carlrobert.codegpt.toolwindow.chat.actions.UsageToolbarLabelAction;
import org.jetbrains.annotations.NotNull;

public class ChatToolWindowPanel extends SimpleToolWindowPanel {

  public ChatToolWindowPanel(@NotNull Project project) {
    super(true);
    initialize(project);
  }

  private void initialize(Project project) {
    var tabPanel = ToolWindowTabPanelFactory.getTabPanel(project, null);
    var conversation = ConversationsState.getCurrentConversation();
    if (conversation == null) {
      tabPanel.displayLandingView();
    } else {
      tabPanel.displayConversation(conversation);
    }

    var tabbedPane = createTabbedPane(tabPanel);
    setToolbar(createActionToolbar(project, tabbedPane).getComponent());
    setContent(tabbedPane);
  }

  private ActionToolbar createActionToolbar(Project project, ChatTabbedPane tabbedPane) {
    var actionGroup = new DefaultActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new CreateNewConversationAction(() -> {
      var panel = ToolWindowTabPanelFactory.getTabPanel(project, null);
      panel.displayLandingView();
      tabbedPane.addNewTab(panel);
      repaint();
      revalidate();
    }));
    actionGroup.add(new OpenInEditorAction());
    actionGroup.addSeparator();
    actionGroup.add(new UsageToolbarLabelAction());
    // actionGroup.addSeparator();
    // actionGroup.add(new TokenToolbarLabelAction());

    return ActionManager.getInstance().createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, false);
  }

  private ChatTabbedPane createTabbedPane(ToolWindowTabPanel tabPanel) {
    var tabbedPane = new ChatTabbedPane();
    tabbedPane.addNewTab(tabPanel);
    return tabbedPane;
  }
}
