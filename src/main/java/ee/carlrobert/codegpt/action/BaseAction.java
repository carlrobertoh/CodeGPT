package ee.carlrobert.codegpt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatContentManagerService;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowTabPanel;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseAction extends AnAction {

  public BaseAction(
      @Nullable @NlsActions.ActionText String text,
      @Nullable @NlsActions.ActionDescription String description,
      @Nullable Icon icon) {
    super(text, description, icon);
  }

  public BaseAction(@Nullable @NlsActions.ActionText String text) {
    super(text);
  }

  protected abstract void actionPerformed(Project project, Editor editor, String selectedText);

  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    var editor = event.getData(PlatformDataKeys.EDITOR);
    if (editor != null && project != null) {
      actionPerformed(project, editor, editor.getSelectionModel().getSelectedText());
    }
  }

  public void update(AnActionEvent event) {
    Project project = event.getProject();
    Editor editor = event.getData(PlatformDataKeys.EDITOR);
    boolean menuAllowed = false;
    if (editor != null && project != null) {
      menuAllowed = editor.getSelectionModel().getSelectedText() != null;
    }
    event.getPresentation().setEnabled(menuAllowed);
  }

  protected void sendMessage(@NotNull Project project, String prompt) {
    var contentManagerService = project.getService(ChatContentManagerService.class);
    contentManagerService.displayChatTab(project);
    contentManagerService.tryFindChatTabbedPane(project)
        .ifPresent(tabbedPane -> {
          var conversation = ConversationsState.getInstance().startConversation();
          var panel = new ChatToolWindowTabPanel(project);
          panel.setConversationId(conversation.getId());
          panel.displayUserMessage(prompt);
          tabbedPane.addNewTab(panel);
          panel.sendMessage(prompt, project);
        });
  }
}
