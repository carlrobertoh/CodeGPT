package ee.carlrobert.codegpt.actions.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class MoveDownAction extends MoveAction {

  public MoveDownAction(Runnable onRefresh) {
    super("Move Down", "Move Down", AllIcons.Actions.MoveDown, onRefresh);
    EditorActionsUtil.registerOrReplaceAction(this);
  }

  @Override
  public void update(@NotNull AnActionEvent event) {
    super.update(event);
  }

  @Override
  protected Optional<Conversation> getConversation(@NotNull Project project) {
    return ConversationService.getInstance().getPreviousConversation();
  }
}
