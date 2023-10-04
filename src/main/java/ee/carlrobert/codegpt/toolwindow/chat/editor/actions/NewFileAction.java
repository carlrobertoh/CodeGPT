package ee.carlrobert.codegpt.toolwindow.chat.editor.actions;

import static com.intellij.openapi.ui.DialogWrapper.OK_EXIT_CODE;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.ide.util.EditorHelper;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.actions.TrackableAction;
import ee.carlrobert.codegpt.util.file.FileUtils;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class NewFileAction extends TrackableAction {

  private final String fileExtension;

  public NewFileAction(@NotNull Editor editor, String fileExtension) {
    super(
        editor,
        CodeGPTBundle.get("toolwindow.chat.editor.action.newFile.title"),
        CodeGPTBundle.get("toolwindow.chat.editor.action.newFile.description"),
        Actions.AddFile,
        ActionType.CREATE_NEW_FILE);
    this.fileExtension = fileExtension;
  }

  @Override
  public void handleAction(@NotNull AnActionEvent e) {
    var project = Objects.requireNonNull(e.getProject());
    var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
    fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);
    var textFieldWithBrowseButton = new TextFieldWithBrowseButton();
    textFieldWithBrowseButton.setText(project.getBasePath());
    textFieldWithBrowseButton.addBrowseFolderListener(
        new TextBrowseFolderListener(fileChooserDescriptor, project));
    var fileNameTextField = new JBTextField("Untitled" + fileExtension);
    fileNameTextField.setColumns(30);

    if (showDialog(project, textFieldWithBrowseButton, fileNameTextField) == OK_EXIT_CODE) {
      var file = FileUtils.createFile(
          textFieldWithBrowseButton.getText(),
          fileNameTextField.getText(),
          editor.getDocument().getText());
      var virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
      if (virtualFile == null) {
        throw new RuntimeException("Couldn't find the saved virtual file");
      }
      var psiFile = PsiManager.getInstance(project).findFile(virtualFile);
      if (psiFile == null) {
        throw new RuntimeException("Couldn't find the saved psi file");
      }

      EditorHelper.openInEditor(psiFile);
    }
  }

  private int showDialog(
      Project project,
      TextFieldWithBrowseButton textFieldWithBrowseButton,
      JBTextField fileNameTextField) {
    var dialogBuilder = new DialogBuilder(project)
        .title("New File")
        .centerPanel(FormBuilder.createFormBuilder()
            .addLabeledComponent("File name:", fileNameTextField)
            .addLabeledComponent("Destination:", textFieldWithBrowseButton)
            .getPanel());
    dialogBuilder.addOkAction();
    dialogBuilder.addCancelAction();
    return dialogBuilder.show();
  }
}
