package ee.carlrobert.codegpt.toolwindow.chat.contextual;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.actions.toolwindow.ClearChatWindowAction;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.indexes.CodebaseIndexingAction;
import org.jetbrains.annotations.NotNull;

public class ContextualChatToolWindowPanel extends SimpleToolWindowPanel {

  public ContextualChatToolWindowPanel(
      @NotNull Project project,
      @NotNull Conversation conversation,
      @NotNull Disposable parentDisposable) {
    super(true);

    var tabPanel = new ContextualChatToolWindowTabPanel(project, conversation);
    setToolbar(createActionToolbar(tabPanel).getComponent());
    setContent(tabPanel.getContent());
    Disposer.register(parentDisposable, tabPanel);
  }

  private ActionToolbar createActionToolbar(ContextualChatToolWindowTabPanel tabPanel) {
    var actionGroup = new DefaultActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new ClearChatWindowAction(tabPanel::displayLandingView));
    actionGroup.addSeparator();
    actionGroup.add(new CodebaseIndexingAction());

    var toolbar = ActionManager.getInstance()
        .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, false);
    toolbar.setTargetComponent(this);
    return toolbar;
  }
}
