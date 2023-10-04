package ee.carlrobert.codegpt.actions.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.util.OverlayUtils;
import org.jetbrains.annotations.NotNull;

public class DeleteConversationAction extends AnAction {

  private final Runnable onDelete;

  public DeleteConversationAction(Runnable onDelete) {
    super("Delete Conversation", "Delete single conversation", AllIcons.Actions.GC);
    this.onDelete = onDelete;
    EditorActionsUtil.registerOrReplaceAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    event.getPresentation().setEnabled(ConversationsState.getCurrentConversation() != null);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    if (OverlayUtils.showDeleteConversationDialog() == Messages.YES) {
      var project = event.getProject();
      if (project != null) {
        TelemetryAction.IDE_ACTION.createActionMessage()
            .property("action", ActionType.DELETE_CONVERSATION.name())
            .send();
        onDelete.run();
      }
    }
  }
}
