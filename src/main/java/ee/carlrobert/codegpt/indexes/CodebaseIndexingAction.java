package ee.carlrobert.codegpt.indexes;

import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.util.OverlayUtils;
import org.jetbrains.annotations.NotNull;

public class CodebaseIndexingAction extends AnAction {

  public CodebaseIndexingAction() {
    super("Update Indexes", "Update indexes", AllIcons.Actions.Refresh);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    var project = event.getProject();
    if (project != null) {
      var folderStructureTreePanel = new FolderStructureTreePanel(project);
      var show = OverlayUtils.showFileStructureDialog(project, folderStructureTreePanel);
      if (show == OK_EXIT_CODE) {
        new CodebaseIndexingTask(project, folderStructureTreePanel.getCheckedFiles()).run();
      }
    }
  }
}
