package ee.carlrobert.codegpt.actions;

import static com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE_ARRAY;
import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;
import static java.lang.String.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.components.JBLabel;
import ee.carlrobert.codegpt.CodeGPTKeys;
import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.ui.checkbox.FileCheckboxTree;
import ee.carlrobert.codegpt.ui.checkbox.PsiElementCheckboxTree;
import ee.carlrobert.codegpt.ui.checkbox.VirtualFileCheckboxTree;
import ee.carlrobert.embedding.CheckedFile;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplyMultipleFilesInPromptAction extends AnAction {

  public ApplyMultipleFilesInPromptAction() {
    super("Include In Context...");
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    var project = e.getProject();
    if (project == null) {
      return;
    }

    var checkboxTree = getCheckboxTree(e);
    var totalTokensLabel = new JBLabel(getTotalTokensText(checkboxTree.getCheckedFiles()));
    checkboxTree.addCheckboxTreeListener(new CheckboxTreeListener() {
      @Override
      public void nodeStateChanged(@NotNull CheckedTreeNode node) {
        totalTokensLabel.setText(getTotalTokensText(checkboxTree.getCheckedFiles()));
      }
    });
    var show = OverlayUtil.showMultiFilePromptDialog(project, totalTokensLabel, checkboxTree);
    if (show == OK_EXIT_CODE) {
      project.putUserData(CodeGPTKeys.SELECTED_FILES, checkboxTree.getCheckedFiles());
      project.getService(StandardChatToolWindowContentManager.class)
          .tryFindChatToolWindowPanel()
          .ifPresent(chatToolWindowPanel ->
              chatToolWindowPanel.displaySelectedFilesNotification(checkboxTree.getCheckedFiles()));
    }
  }

  private @Nullable FileCheckboxTree getCheckboxTree(AnActionEvent event) {
    var dataContext = event.getDataContext();

    var psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
    if (psiElement != null) {
      return new PsiElementCheckboxTree(psiElement);
    }

    var selectedVirtualFiles = VIRTUAL_FILE_ARRAY.getData(dataContext);
    if (selectedVirtualFiles != null) {
      return new VirtualFileCheckboxTree(selectedVirtualFiles);
    }

    return null;
  }

  private String getTotalTokensText(List<CheckedFile> checkedFiles) {
    var totalTokens = checkedFiles.stream()
        .map(file -> EncodingManager.getInstance().countTokens(file.getFileContent()))
        .mapToInt(Integer::intValue)
        .sum();

    return format(
        "<html><strong>%d</strong> %s totaling <strong>%d</strong> tokens</html>",
        checkedFiles.size(),
        checkedFiles.size() == 1 ? "file" : "files",
        totalTokens);
  }
}