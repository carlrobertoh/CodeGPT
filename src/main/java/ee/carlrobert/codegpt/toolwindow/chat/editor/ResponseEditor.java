package ee.carlrobert.codegpt.toolwindow.chat.editor;

import static ee.carlrobert.codegpt.util.file.FileUtils.findLanguageExtensionMapping;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.ContextMenuPopupHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.actions.toolwindow.ReplaceCodeInMainEditorAction;
import ee.carlrobert.codegpt.toolwindow.chat.components.IconActionButton;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.CopyAction;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.DiffAction;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.EditAction;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.NewFileAction;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.ReplaceSelectionAction;
import ee.carlrobert.codegpt.util.EditorUtils;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.JPanel;

public class ResponseEditor extends JPanel implements Disposable {

  private final Editor editor;
  private final String language;
  private final String extension;

  public ResponseEditor(
      Project project,
      String code,
      String markdownLanguage,
      Disposable disposableParent) {
    super(new BorderLayout());

    var extensionMapping = findLanguageExtensionMapping(markdownLanguage);
    language = extensionMapping.getKey();
    extension = extensionMapping.getValue();
    editor = EditorUtils.createEditor(project, extension, code);

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

    var settings = editorEx.getSettings();
    settings.setAdditionalColumnsCount(0);
    settings.setAdditionalLinesCount(1);
    settings.setAdditionalPageAtBottom(false);
    settings.setVirtualSpace(false);
    settings.setUseSoftWraps(false);
    settings.setLineMarkerAreaShown(false);
    settings.setGutterIconsShown(false);

    add(createHeaderComponent(), BorderLayout.NORTH);
    add(editor.getComponent(), BorderLayout.SOUTH);

    Disposer.register(disposableParent, this);
  }

  @Override
  public void dispose() {
    EditorFactory.getInstance().releaseEditor(editor);
  }

  public Editor getEditor() {
    return editor;
  }

  private JPanel createHeaderComponent() {
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
    wrapper.add(new IconActionButton(new DiffAction(editor)));
    wrapper.add(Box.createHorizontalStrut(8));
    wrapper.add(new IconActionButton(new EditAction(editor)));
    wrapper.add(Box.createHorizontalStrut(8));
    wrapper.add(new IconActionButton(new NewFileAction(editor, extension)));
    wrapper.add(Box.createHorizontalStrut(8));
    wrapper.add(new IconActionButton(new CopyAction(editor)));
    wrapper.add(Box.createHorizontalStrut(8));
    wrapper.add(new IconActionButton(new ReplaceSelectionAction(editor)));
    return wrapper;
  }
}
