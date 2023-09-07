package ee.carlrobert.codegpt.toolwindow.chat.standard;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.DefaultCompactActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.actions.toolwindow.ClearChatWindowAction;
import ee.carlrobert.codegpt.actions.toolwindow.CreateNewConversationAction;
import ee.carlrobert.codegpt.actions.toolwindow.OpenInEditorAction;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import java.awt.FlowLayout;
import org.jetbrains.annotations.NotNull;

public class StandardChatToolWindowPanel extends SimpleToolWindowPanel {

  public StandardChatToolWindowPanel(@NotNull Project project, @NotNull Disposable parentDisposable) {
    super(true);
    initialize(project, parentDisposable);
  }


  private void initialize(Project project, Disposable parentDisposable) {
    var tabPanel = new StandardChatToolWindowTabPanel(project);
    var conversation = ConversationsState.getCurrentConversation();
    if (conversation == null) {
      tabPanel.displayLandingView();
    } else {
      tabPanel.displayConversation(conversation);
    }

    var tabbedPane = createTabbedPane(tabPanel, parentDisposable);

    var toolbarComponent = createActionToolbar(project, tabbedPane).getComponent();
    toolbarComponent.setLayout(new FlowLayout());
    setToolbar(toolbarComponent);
    setContent(tabbedPane);

    Disposer.register(parentDisposable, tabPanel);
  }

  private ActionToolbar createActionToolbar(Project project, StandardChatToolWindowTabbedPane tabbedPane) {
    var actionGroup = new DefaultCompactActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new CreateNewConversationAction(() -> {
      var panel = new StandardChatToolWindowTabPanel(project);
      panel.displayLandingView();
      tabbedPane.addNewTab(panel);
      repaint();
      revalidate();
    }));
    actionGroup.add(new ClearChatWindowAction(tabbedPane::resetCurrentlyActiveTabPanel));
    actionGroup.addSeparator();
    actionGroup.add(new OpenInEditorAction());

    var toolbar = ActionManager.getInstance()
        .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, true);
    toolbar.setTargetComponent(this);
    return toolbar;
  }

  private StandardChatToolWindowTabbedPane createTabbedPane(StandardChatToolWindowTabPanel tabPanel, Disposable parentDisposable) {
    var tabbedPane = new StandardChatToolWindowTabbedPane(parentDisposable);
    tabbedPane.addNewTab(tabPanel);
    return tabbedPane;
  }
}
