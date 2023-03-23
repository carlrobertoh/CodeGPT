package ee.carlrobert.codegpt.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatContentManagerService;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowTabPanel;
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
      var contentManagerService = project.getService(ChatContentManagerService.class);
      contentManagerService.displayChatTab(project);
      contentManagerService.tryFindChatTabbedPane(project)
          .ifPresent(tabbedPane -> {
            ConversationsState.getInstance().setCurrentConversation(null);
            var panel = new ChatToolWindowTabPanel(project);
            panel.displayLandingView();
            tabbedPane.addNewTab(panel);
          });
    }
  }
}
