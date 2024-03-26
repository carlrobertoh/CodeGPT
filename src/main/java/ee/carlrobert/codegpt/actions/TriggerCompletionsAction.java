package ee.carlrobert.codegpt.actions;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.codecompletions.CodeCompletionListenerManager;
import org.jetbrains.annotations.NotNull;

public class TriggerCompletionsAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var editor = event.getData(PlatformDataKeys.EDITOR);
    if (editor != null) {
      var manager = CodeCompletionListenerManager.getInstance();
      manager.triggerCompletions(editor);
    }
  }
}
