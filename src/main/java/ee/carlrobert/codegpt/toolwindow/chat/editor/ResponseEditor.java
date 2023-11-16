package ee.carlrobert.codegpt.toolwindow.chat.editor;

import static ee.carlrobert.codegpt.util.file.FileUtils.findLanguageExtensionMapping;
import static java.lang.String.format;

import com.intellij.icons.AllIcons.General;
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
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.actions.toolwindow.ReplaceCodeInMainEditorAction;
import ee.carlrobert.codegpt.toolwindow.chat.components.IconActionButton;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.CopyAction;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.DiffAction;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.EditAction;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.NewFileAction;
import ee.carlrobert.codegpt.toolwindow.chat.editor.actions.ReplaceSelectionAction;
import ee.carlrobert.codegpt.util.EditorUtils;
import java.awt.BorderLayout;
import java.awt.Color;
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
      boolean readOnly,
      Color backgroundColor,
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
    if (readOnly) {
      editorEx.setOneLineMode(true);
      editorEx.setHorizontalScrollbarVisible(false);
    }
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

    add(createHeaderComponent(readOnly), BorderLayout.NORTH);
    add(editor.getComponent(), BorderLayout.CENTER);
    add(createFooterComponent(readOnly ? getBackground() : backgroundColor), BorderLayout.SOUTH);

    Disposer.register(disposableParent, this);
  }

  @Override
  public void dispose() {
    EditorFactory.getInstance().releaseEditor(editor);
  }

  public Editor getEditor() {
    return editor;
  }

  private JPanel createHeaderComponent(boolean readOnly) {
    var headerComponent = new JPanel(new BorderLayout());
    headerComponent.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border(), 1, 1, 1, 1),
        JBUI.Borders.empty(4, 8)));
    headerComponent.add(new JBLabel(language), BorderLayout.LINE_START);
    if (!readOnly) {
      headerComponent.add(createHeaderActions(), BorderLayout.LINE_END);
    }
    return headerComponent;
  }

  private String getLinkText(boolean expanded) {
    return expanded
        ? format(
            CodeGPTBundle.get("toolwindow.chat.editor.action.expand"),
            ((EditorEx) editor).getDocument().getLineCount() - 1)
        : CodeGPTBundle.get("toolwindow.chat.editor.action.collapse");
  }

  private JPanel createFooterComponent(Color backgroundColor) {
    var editorEx = ((EditorEx) editor);
    var linkText = getLinkText(editorEx.isOneLineMode());
    var expandLink = new ActionLink(
        linkText,
        event -> {
          var oneLineMode = editorEx.isOneLineMode();
          var source = (ActionLink) event.getSource();
          source.setText(getLinkText(!oneLineMode));
          source.setIcon(oneLineMode ? General.ArrowUp : General.ArrowDown, true);

          editorEx.setOneLineMode(!oneLineMode);
          editorEx.setHorizontalScrollbarVisible(oneLineMode);
          editorEx.getContentComponent().revalidate();
          editorEx.getContentComponent().repaint();
        });
    expandLink.setIcon(editorEx.isOneLineMode() ? General.ArrowDown : General.ArrowUp, true);

    var panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panel.setBackground(backgroundColor);
    panel.add(expandLink);
    return panel;
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
