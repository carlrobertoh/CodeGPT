package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.util.FileUtils.findFileNameExtensionMapping;

import com.intellij.icons.AllIcons;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.ide.util.EditorHelper;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.ContextMenuPopupHandler;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.actions.toolwindow.ReplaceCodeInMainEditorAction;
import ee.carlrobert.codegpt.toolwindow.IconActionButton;
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.FileUtils;
import ee.carlrobert.codegpt.util.OverlayUtils;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class ChatToolWindowTabPanelEditor implements Disposable {

  private final Project project;
  private final Editor editor;
  private final Map.Entry<String, String> fileNameExtensionMapping;

  public ChatToolWindowTabPanelEditor(
      Project project,
      String code,
      String language,
      Disposable disposableParent) {
    this.project = project;
    this.fileNameExtensionMapping = findFileNameExtensionMapping(language);

    var timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
    var fileName = "temp_" + timestamp + fileNameExtensionMapping.getValue();
    var lightVirtualFile = new LightVirtualFile(
        String.format("%s/%s", PathManager.getTempPath(), fileName), code);
    var document = FileDocumentManager.getInstance().getDocument(lightVirtualFile);
    if (document == null) {
      document = EditorFactory.getInstance().createDocument(code);
    }
    EditorUtils.disableHighlighting(project, document);
    editor = EditorFactory.getInstance().createEditor(
        document,
        project,
        lightVirtualFile,
        true,
        EditorKind.UNTYPED);

    DefaultActionGroup group = new DefaultActionGroup();
    group.add(new ReplaceCodeInMainEditorAction());

    String originalGroupId = ((EditorEx) editor).getContextMenuGroupId();
    if (originalGroupId != null) {
    AnAction originalGroup = ActionManager.getInstance().getAction(originalGroupId);
      if (originalGroup instanceof ActionGroup) {
        group.addAll(((ActionGroup) originalGroup).getChildren(null));
      }
    }

    var editorEx = ((EditorEx) editor);
    editorEx.installPopupHandler(new ContextMenuPopupHandler.Simple(group));
    editorEx.setColorsScheme(EditorColorsManager.getInstance().getSchemeForCurrentUITheme());

    var settings = editor.getSettings();
    settings.setAdditionalColumnsCount(0);
    settings.setAdditionalLinesCount(0);
    settings.setAdditionalPageAtBottom(false);
    settings.setVirtualSpace(false);
    settings.setUseSoftWraps(false);

    Disposer.register(disposableParent, this);
  }

  public JComponent getComponent() {
    var wrapper = new JPanel(new BorderLayout());
    wrapper.add(createHeaderComponent(fileNameExtensionMapping.getKey()), BorderLayout.NORTH);
    wrapper.add(editor.getComponent(), BorderLayout.SOUTH);
    return wrapper;
  }

  public Editor getEditor() {
    return editor;
  }

  private JPanel createHeaderComponent(String language) {
    var headerComponent = new JPanel(new BorderLayout());
    headerComponent.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 1, 1, 1),
        JBUI.Borders.empty(8)));
    headerComponent.add(new JBLabel(language), BorderLayout.LINE_START);
    headerComponent.add(createHeaderActions(), BorderLayout.LINE_END);
    return headerComponent;
  }

  private JPanel createHeaderActions() {
    var wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    wrapper.add(new IconActionButton("New File", Actions.AddFile, new NewFileAction()));
    wrapper.add(Box.createHorizontalStrut(8));
    wrapper.add(new IconActionButton("Copy", AllIcons.Actions.Copy, new CopyAction()));
    wrapper.add(Box.createHorizontalStrut(8));
    wrapper.add(new IconActionButton(
        "Replace in Main Editor",
        AllIcons.Actions.Replace,
        new ReplaceInMainEditorAction()));
    return wrapper;
  }

  @Override
  public void dispose() {
    EditorFactory.getInstance().releaseEditor(editor);
  }

  class NewFileAction extends AnAction {

    private final TextFieldWithBrowseButton textFieldWithBrowseButton;
    private final JBTextField fileNameTextField;

    NewFileAction() {
      var fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
      fileChooserDescriptor.setForcedToUseIdeaFileChooser(true);

      textFieldWithBrowseButton = new TextFieldWithBrowseButton();
      textFieldWithBrowseButton.addBrowseFolderListener(
          new TextBrowseFolderListener(fileChooserDescriptor, project));
      fileNameTextField = new JBTextField("Untitled" + fileNameExtensionMapping.getValue());
      fileNameTextField.setColumns(30);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
      if (getDialogBuilder().show() == 0) {
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

    private DialogBuilder getDialogBuilder() {
      var dialogBuilder = new DialogBuilder(project);
      dialogBuilder.setTitle("New File");
      dialogBuilder.setCenterPanel(FormBuilder.createFormBuilder()
          .addLabeledComponent("File name:", fileNameTextField)
          .addLabeledComponent("Destination:", textFieldWithBrowseButton)
          .getPanel());
      dialogBuilder.addOkAction();
      dialogBuilder.addCancelAction();
      return dialogBuilder;
    }
  }

  class CopyAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
      StringSelection stringSelection = new StringSelection(editor.getDocument().getText());
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, null);

      var locationOnScreen = ((MouseEvent) event.getInputEvent()).getLocationOnScreen();
      locationOnScreen.y = locationOnScreen.y - 16;

      OverlayUtils.showInfoBalloon("Code copied!", locationOnScreen);
    }
  }

  class ReplaceInMainEditorAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
      var project = event.getProject();
      if (project != null) {
        if (EditorUtils.isMainEditorTextSelected(project)) {
          EditorUtils.replaceMainEditorSelection(project, editor.getDocument().getText());
        } else {
          var locationOnScreen = ((MouseEvent) event.getInputEvent()).getLocationOnScreen();
          locationOnScreen.y = locationOnScreen.y - 16;

          OverlayUtils.showWarningBalloon(
              EditorUtils.getSelectedEditor(project) == null
                  ? "Unable to locate a selected editor"
                  : "Please select a target code before proceeding",
              locationOnScreen);
        }
      }
    }
  }
}
