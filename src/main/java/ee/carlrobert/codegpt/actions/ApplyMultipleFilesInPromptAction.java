package ee.carlrobert.codegpt.actions;

import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import ee.carlrobert.codegpt.actions.editor.CustomPromptAction.CustomPromptDialog;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.ui.checkbox.FileCheckboxTree;
import ee.carlrobert.codegpt.ui.checkbox.PsiElementCheckboxTree;
import ee.carlrobert.codegpt.ui.checkbox.VirtualFileCheckboxTree;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.NotNull;

public class ApplyMultipleFilesInPromptAction extends AnAction {

  public ApplyMultipleFilesInPromptAction() {
    super("Include In Prompt", "Include in prompt description", Actions.AddFile);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    var project = e.getProject();
    if (project == null) {
      return;
    }

    var checkboxTree = getCheckboxTree(e);
    var show = OverlayUtil.showMultiFilePromptDialog(project, checkboxTree);
    if (show == OK_EXIT_CODE) {
      var context = checkboxTree.getCheckedFiles().stream()
          .map(item -> format(
              "\nPath:\n"
                  + "[File Path](%s)\n\n"
                  + "Content:\n\n"
                  + "%s\n",
              item.getFilePath(),
              format("```%s\n%s\n```\n", item.getFileExtension(), item.getFileContent().trim())))
          .collect(joining());

      var dialog = new CustomPromptDialog("");
      if (dialog.showAndGet()) {
        var userPrompt = dialog.getUserPrompt();
        var prompt = format(
            "Use the following context to answer question at the end:\n"
                + "%s\n"
                + "Question: %s",
            context,
            userPrompt);
        var message = new Message(prompt);
        message.setUserMessage(userPrompt);
        SwingUtilities.invokeLater(() ->
            project.getService(StandardChatToolWindowContentManager.class)
                .createNewTabPanel()
                .sendMessage(message));
      }
    }
  }

  private FileCheckboxTree getCheckboxTree(AnActionEvent event) {
    var dataContext = event.getDataContext();
    var psiElement = CommonDataKeys.PSI_ELEMENT.getData(dataContext);
    if (psiElement == null) {
      var selectedVirtualFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
      if (selectedVirtualFiles == null) {
        throw new RuntimeException("Something went wrong");
      }
      return new VirtualFileCheckboxTree(selectedVirtualFiles);
    }
    return new PsiElementCheckboxTree(psiElement);
  }
}