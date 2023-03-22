package ee.carlrobert.codegpt.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.toolwindow.ContentManagerService;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowPanel;
import org.jetbrains.annotations.NotNull;

public class AskAction extends AnAction {

  public AskAction() {
    super("Ask ChatGPT", "Ask ChatGPT description", AllIcons.Actions.Find);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(event.getProject() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project != null) {
      var contentManagerService = project.getService(ContentManagerService.class);
      contentManagerService.displayChatTab(project);
      contentManagerService.tryFindChatTabbedPane(project)
          .ifPresent(tabbedPane -> {
            var panel = new ChatToolWindowPanel(project);
            panel.displayLandingView();
            tabbedPane.addTab("Chat " + (tabbedPane.getTabCount() + 1), panel);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
          });
    }
  }
}
