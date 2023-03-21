package ee.carlrobert.codegpt.toolwindow.chat.actions;

import static java.util.Objects.requireNonNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.testFramework.LightVirtualFile;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class OpenInEditorAction extends AnAction {

  public OpenInEditorAction() {
    super("Open In Editor", "Open conversation in editor", AllIcons.Actions.SplitVertically);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    super.update(event);
    var currentConversation = ConversationsState.getCurrentConversation();
    var isEnabled = currentConversation != null && !currentConversation.getMessages().isEmpty();
    event.getPresentation().setEnabled(isEnabled);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    var project = e.getProject();
    var currentConversation = ConversationsState.getCurrentConversation();
    if (project != null && currentConversation != null) {
      var dateTimeStamp = currentConversation.getUpdatedOn().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
      var fileName = String.format("%s_%s.md", currentConversation.getModel().getCode(), dateTimeStamp);
      var fileContent = currentConversation
          .getMessages()
          .stream()
          .map(it -> String.format("### User:\n%s\n### ChatGPT:\n%s\n", it.getPrompt(), it.getResponse()))
          .collect(Collectors.joining());
      VirtualFile file = new LightVirtualFile(fileName, fileContent);
      FileEditorManager.getInstance(project).openFile(file, true);
      var toolWindow = requireNonNull(ToolWindowManager.getInstance(project).getToolWindow("CodeGPT"));
      toolWindow.hide();
    }
  }
}
