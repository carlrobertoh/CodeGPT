package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.util.FileUtils.findFileNameExtensionMapping;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffDialogHints;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.diff.util.DiffUserDataKeys;
import com.intellij.diff.util.Side;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.ContextMenuPopupHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.actions.toolwindow.ReplaceCodeInMainEditorAction;
import ee.carlrobert.codegpt.toolwindow.IconActionButton;
import ee.carlrobert.codegpt.util.DiffUtils;
import ee.carlrobert.codegpt.util.EditorUtils;
import ee.carlrobert.codegpt.util.FileUtils;
import ee.carlrobert.codegpt.util.OverlayUtils;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class ChatToolWindowTabPanelEditor implements Disposable {

  private final Editor editor;
  private final Map.Entry<String, String> fileNameExtensionMapping;

  private final DiffManager diffManagerInstance = DiffManager.getInstance();

  public ChatToolWindowTabPanelEditor(Project project, String code, String language, Disposable disposableParent) {
    this.fileNameExtensionMapping = findFileNameExtensionMapping(language);

    var fileName = "temp_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + fileNameExtensionMapping.getValue();
    var lightVirtualFile = new LightVirtualFile(String.format("%s/%s", PathManager.getTempPath(), fileName), code);
    var document = FileDocumentManager.getInstance().getDocument(lightVirtualFile);
    if (document == null) {
      document = EditorFactory.getInstance().createDocument(code);
    }
    EditorUtils.disableHighlighting(project, document);
    editor = EditorFactory.getInstance().createEditor(document, project, lightVirtualFile, true, EditorKind.UNTYPED);
    String originalGroupId = ((EditorEx) editor).getContextMenuGroupId();
    AnAction originalGroup = originalGroupId == null ? null : ActionManager.getInstance().getAction(originalGroupId);
    DefaultActionGroup group = new DefaultActionGroup();
    if (originalGroup instanceof ActionGroup) {
      group.addAll(((ActionGroup) originalGroup).getChildren(null));
    }
    group.add(new ReplaceCodeInMainEditorAction());

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
    wrapper.add(new IconActionButton("Diff", AllIcons.Actions.DiffWithClipboard, new AnAction() {
      @Override
      public void actionPerformed(@NotNull AnActionEvent event) {
          String text = editor.getDocument().getText();
          Application application = ApplicationManager.getApplication();
          var locationOnScreen = ((MouseEvent) event.getInputEvent()).getLocationOnScreen();
          locationOnScreen.y = locationOnScreen.y - 16;
          application.invokeLater(() -> application.runWriteAction(() -> WriteCommandAction.runWriteCommandAction(event.getProject(), () -> {
              try {
                  assert event.getProject() != null;
                  Editor editor = FileEditorManager.getInstance(event.getProject()).getSelectedTextEditor();
                  openDiffView(event.getProject(), editor, text);
                  OverlayUtils.showInfoBalloon("Diff opened!", locationOnScreen);
              } catch (Exception ignore) {
                  OverlayUtils.showInfoBalloon("Diff failed!", locationOnScreen);
              }
          })));
      }
    }));
    wrapper.add(new IconActionButton("Copy", AllIcons.Actions.Copy, new AnAction() {
      @Override
      public void actionPerformed(@NotNull AnActionEvent event) {
        StringSelection stringSelection = new StringSelection(editor.getDocument().getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        var locationOnScreen = ((MouseEvent) event.getInputEvent()).getLocationOnScreen();
        locationOnScreen.y = locationOnScreen.y - 16;

        OverlayUtils.showInfoBalloon("Code copied!", locationOnScreen);
      }
    }));
    wrapper.add(Box.createHorizontalStrut(8));
    wrapper.add(new IconActionButton("Replace in Main Editor", AllIcons.Actions.Replace, new AnAction() {
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
                EditorUtils.getSelectedEditor(project) == null ? "Unable to locate a selected editor" : "Please select a target code before proceeding",
                locationOnScreen);
          }
        }
      }
    }));
    return wrapper;
  }
  public void openDiffView(@NotNull Project project, Editor editor, String text) {
    try {
        SelectionModel selectionModel = editor.getSelectionModel();
        Document document = editor.getDocument();
        int startLine = document.getLineNumber(selectionModel.getSelectionStart());
        int endLine = document.getLineNumber(selectionModel.getSelectionEnd());
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        assert virtualFile != null;
        String fileName = virtualFile.getName();
        String filePath = virtualFile.getPath();
        int startLineOffset = document.getLineStartOffset(startLine);
        int endLineOffset = document.getLineEndOffset(endLine);
        final String selectedText = document.getText().substring(startLineOffset, endLineOffset);
        String newText = FileUtils.addWriteSpace(text, selectedText);
        VirtualFile rightFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(filePath);
        Path path = Paths.get(PathManager.getTempPath() + File.separator + DiffUtils.TEMP_DIR_NAME);
        Files.createDirectories(path);
        var tempFileName = FileUtils.getName(fileName,"") + "_" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + "." + virtualFile.getExtension();
        String tempFilePath = PathManager.getTempPath() + File.separator + DiffUtils.TEMP_DIR_NAME + File.separator + tempFileName;
        FileUtils.replaceContentInFile(filePath, tempFilePath, selectedText, newText, startLine, endLine);
        File leftFileTemp = new File(tempFilePath);
        VirtualFile leftFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(leftFileTemp.getCanonicalPath());
        assert leftFile != null;
        DiffContent leftContent = DiffContentFactory.getInstance().create(project, leftFile);
        assert rightFile != null;
        DiffContent rightContent = DiffContentFactory.getInstance().create(project, rightFile);
        String leftFileTitle = leftFile.getName() + DiffUtils.LEFT_TITLE;
        String rightFileTitle = rightFile.getName() + DiffUtils.RIGHT_TITLE;
        SimpleDiffRequest simpleDiffRequest = new SimpleDiffRequest(DiffUtils.DIFF_TITLE, leftContent, rightContent, leftFileTitle, rightFileTitle);
        simpleDiffRequest.putUserData(DiffUserDataKeys.SCROLL_TO_LINE, Pair.create(Side.RIGHT, startLine));
        simpleDiffRequest.putUserData(DiffUserDataKeys.PREFERRED_FOCUS_SIDE, Side.RIGHT);
        simpleDiffRequest.putUserData(DiffUtils.DIFF_FILENAME, fileName);
        simpleDiffRequest.putUserData(DiffUtils.DIFF_FILEPATH_LEFT, leftFile.getPath());
        simpleDiffRequest.putUserData(DiffUtils.DIFF_FILEPATH_RIGHT, rightFile.getPath());
        simpleDiffRequest.putUserData(DiffUtils.DIFF_FILE_UNIQUE_ID, DiffUtils.DIFF_ID);
        DiffUtils.closeDiffViewIfAlreadyOpened(project);
        diffManagerInstance.showDiff(project, simpleDiffRequest, DiffDialogHints.DEFAULT);
    } catch (Exception e) {
        throw new RuntimeException("Open diff failed");
    }
  }

  @Override
  public void dispose() {
    EditorFactory.getInstance().releaseEditor(editor);
  }
}
