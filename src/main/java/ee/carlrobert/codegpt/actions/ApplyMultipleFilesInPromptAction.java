package ee.carlrobert.codegpt.actions;

import static com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE_ARRAY;
import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;
import static java.lang.String.format;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApplyMultipleFilesInPromptAction extends AnAction {

  private static final Logger LOG = Logger.getInstance(ApplyMultipleFilesInPromptAction.class);

  public ApplyMultipleFilesInPromptAction() {
    super("Include In Context...");
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    var project = e.getProject();
    if (project == null) {
      return;
    }

    var checkboxTree = getCheckboxTree(e.getDataContext());
    if (checkboxTree == null) {
      throw new RuntimeException("Could not obtain file tree");
    }

    var totalTokensLabel = new TotalTokensLabel(checkboxTree.getCheckedFiles());
    checkboxTree.addCheckboxTreeListener(new CheckboxTreeListener() {
      @Override
      public void nodeStateChanged(@NotNull CheckedTreeNode node) {
        totalTokensLabel.updateState(node);
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

  private @Nullable FileCheckboxTree getCheckboxTree(DataContext dataContext) {
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

  private static class TotalTokensLabel extends JBLabel {

    private static final EncodingManager encodingManager = EncodingManager.getInstance();

    private int fileCount;
    private int totalTokens;

    TotalTokensLabel(List<CheckedFile> checkedFiles) {
      fileCount = checkedFiles.size();
      totalTokens = calculateTotalTokens(checkedFiles);
      updateText();
    }

    void updateState(CheckedTreeNode checkedNode) {
      var fileContent = getNodeFileContent(checkedNode);
      if (fileContent != null) {
        int tokenCount = encodingManager.countTokens(fileContent);
        if (checkedNode.isChecked()) {
          totalTokens += tokenCount;
          fileCount++;
        } else {
          totalTokens -= tokenCount;
          fileCount--;
        }

        SwingUtilities.invokeLater(this::updateText);
      }
    }

    private @Nullable String getNodeFileContent(CheckedTreeNode checkedNode) {
      var userObject = checkedNode.getUserObject();
      if (userObject instanceof PsiElement) {
        var psiFile = ((PsiElement) userObject).getContainingFile();
        if (psiFile != null) {
          var virtualFile = psiFile.getVirtualFile();
          if (virtualFile != null) {
            try {
              return new String(Files.readAllBytes(Paths.get(virtualFile.getPath())));
            } catch (IOException ex) {
              LOG.error(ex);
            }
          }
        }
      }
      return null;
    }

    private void updateText() {
      setText(format(
          "<html><strong>%d</strong> %s totaling <strong>%d</strong> tokens</html>",
          fileCount,
          fileCount == 1 ? "file" : "files",
          totalTokens));
    }

    private int calculateTotalTokens(List<CheckedFile> checkedFiles) {
      return checkedFiles.stream()
          .mapToInt(file -> encodingManager.countTokens(file.getFileContent()))
          .sum();
    }
  }
}