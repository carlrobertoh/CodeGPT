package ee.carlrobert.codegpt.actions.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import org.jetbrains.annotations.NotNull;

public class ClearChatWindowAction extends AnAction {

  private final Runnable onActionPerformed;

  public ClearChatWindowAction(Runnable onActionPerformed) {
    super("Clear Window", "Clears a chat window", AllIcons.General.Reset);
    this.onActionPerformed = onActionPerformed;
    EditorActionsUtil.registerOrReplaceAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    super.update(event);
    var currentConversation = ConversationsState.getCurrentConversation();
    var isEnabled = currentConversation != null && !currentConversation.getMessages().isEmpty();
    event.getPresentation().setEnabled(isEnabled);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    try {
      onActionPerformed.run();
    } finally {
      TelemetryAction.IDE_ACTION.createActionMessage()
          .property("action", ActionType.CLEAR_CHAT_WINDOW.name())
          .send();
    }
  }
}