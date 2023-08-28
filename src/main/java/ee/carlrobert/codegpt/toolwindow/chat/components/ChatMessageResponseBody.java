package ee.carlrobert.codegpt.toolwindow.chat.components;

import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;
import static java.lang.String.format;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBUI;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import ee.carlrobert.codegpt.settings.SettingsConfigurable;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowTabPanelEditor;
import ee.carlrobert.codegpt.toolwindow.chat.ResponseNodeRenderer;
import ee.carlrobert.codegpt.toolwindow.chat.StreamParser;
import ee.carlrobert.codegpt.toolwindow.chat.StreamResponseType;
import ee.carlrobert.codegpt.util.MarkdownUtils;
import ee.carlrobert.codegpt.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Objects;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;

public class ChatMessageResponseBody extends JPanel {

  private final Project project;
  private final Disposable parentDisposable;
  private final StreamParser streamParser;
  private JPanel currentlyProcessedElement;
  private ChatToolWindowTabPanelEditor currentlyProcessedEditor;
  private JTextPane currentlyProcessedTextPane;
  private boolean responseReceived;

  public ChatMessageResponseBody(Project project, Disposable parentDisposable) {
    this(project, getPanelBackgroundColor(), false, parentDisposable);
  }

  public ChatMessageResponseBody(Project project, boolean withGhostText, Disposable parentDisposable) {
    this(project, getPanelBackgroundColor(), withGhostText, parentDisposable);
  }

  public ChatMessageResponseBody(Project project, Color backgroundColor, boolean withGhostText, Disposable parentDisposable) {
    super(new BorderLayout());
    this.project = project;
    this.parentDisposable = parentDisposable;
    this.streamParser = new StreamParser();
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setBackground(backgroundColor);

    if (withGhostText) {
      prepareProcessingTextResponse();
      currentlyProcessedTextPane.setText("<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">&#8205;</p></html>");
    }

    UIManager.addPropertyChangeListener(propertyChangeEvent -> setBackground(backgroundColor));
  }

  public ChatMessageResponseBody withResponse(String response) {
    for (var message : MarkdownUtils.splitCodeBlocks(response)) {
      boolean isCodeResponse = message.startsWith("```");
      if (isCodeResponse) {
        currentlyProcessedEditor = null;
      }
      processResponse(message, isCodeResponse);
    }
    hideCarets();

    return this;
  }

  public void update(String partialMessage) {
    for (var item : streamParser.parse(partialMessage)) {
      processResponse(item.getResponse(), StreamResponseType.CODE.equals(item.getType()));
    }
  }

  public void displayMissingCredential() {
    currentlyProcessedTextPane.setText(
        "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">API key not provided. Open <a href=\"#\">Settings</a> to set one.</p></html>");
    currentlyProcessedTextPane.addHyperlinkListener(e -> {
      if (e.getEventType() == ACTIVATED) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, SettingsConfigurable.class);
      }
    });
    currentlyProcessedTextPane.getCaret().setVisible(false);
  }

  public void hideCarets() {
    if (currentlyProcessedEditor != null) {
      ((EditorEx) currentlyProcessedEditor.getEditor()).setCaretVisible(false);
      ((EditorEx) currentlyProcessedEditor.getEditor()).setCaretEnabled(false);
    }
    if (currentlyProcessedTextPane != null && currentlyProcessedTextPane.getCaret().isVisible()) {
      currentlyProcessedTextPane.getCaret().setVisible(false);
    }
  }


  public void displayError(String message) {
    var errorText = format("<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">%s</p></html>", message);
    if (responseReceived) {
      var errorPane = createTextPane();
      errorPane.setText(errorText);
      add(new ResponseWrapper().add(errorPane));
    } else {
      currentlyProcessedTextPane.setText(errorText);
    }
  }

  public void displayDefaultError() {
    displayError("Something went wrong.");
  }

  public void clear() {
    removeAll();

    streamParser.clear();
    // TODO: First message might be code block
    prepareProcessingTextResponse();
    currentlyProcessedTextPane.setText("<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">&#8205;</p></html>");

    repaint();
    revalidate();
  }

  private void processResponse(String markdownInput, boolean isCodeResponse) {
    responseReceived = true;

    if (isCodeResponse) {
      processCode(markdownInput);
    } else {
      processText(markdownInput);
    }
  }

  private void processCode(String markdownCode) {
    var document = Parser.builder().build().parse(markdownCode);
    var codeBlock = document.getChildOfType(FencedCodeBlock.class);
    if (codeBlock != null) {
      var code = ((FencedCodeBlock) codeBlock).getContentChars().unescape();
      var language = ((FencedCodeBlock) codeBlock).getInfo();
      if (!code.isEmpty()) {
        if (currentlyProcessedEditor == null) {
          prepareProcessingCodeResponse(code, language.unescape());
        }
        updateEditorDocument(code);
      }
    }
  }

  private void processText(String markdownText) {
    if (currentlyProcessedTextPane == null) {
      prepareProcessingTextResponse();
    }
    currentlyProcessedTextPane.setText(convertMdToHtml(markdownText));
  }

  private void prepareProcessingTextResponse() {
    hideCarets();
    currentlyProcessedEditor = null;
    currentlyProcessedTextPane = createTextPane();
    currentlyProcessedElement = new ResponseWrapper();
    currentlyProcessedElement.add(currentlyProcessedTextPane);
    add(currentlyProcessedElement);
  }

  private void prepareProcessingCodeResponse(String code, String language) {
    hideCarets();
    currentlyProcessedTextPane = null;
    currentlyProcessedEditor = new ChatToolWindowTabPanelEditor(project, code, language, parentDisposable);
    currentlyProcessedElement = new ResponseWrapper();

    currentlyProcessedElement.add(currentlyProcessedEditor.getComponent());
    add(currentlyProcessedElement);
  }

  private void updateEditorDocument(String code) {
    var editor = currentlyProcessedEditor.getEditor();
    var document = editor.getDocument();
    var application = ApplicationManager.getApplication();
    Runnable updateDocumentRunnable = () -> {
      application.runWriteAction(() ->
          WriteCommandAction.runWriteCommandAction(project, () -> {
            document.replaceString(0, document.getTextLength(), code);
            editor.getCaretModel().moveToOffset(code.length());
            editor.getComponent().revalidate();
            editor.getComponent().repaint();
          }));
    };

    if (application.isUnitTestMode()) {
      application.invokeAndWait(updateDocumentRunnable);
    } else {
      application.invokeLater(updateDocumentRunnable);
    }
  }

  private JTextPane createTextPane() {
    var textPane = new JTextPane();
    textPane.addHyperlinkListener(event -> {
      if (FileUtil.exists(event.getDescription()) && ACTIVATED.equals(event.getEventType())) {
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(event.getDescription());
        FileEditorManager.getInstance(project).openFile(Objects.requireNonNull(file), true);
        return;
      }

      SwingUtils.handleHyperlinkClicked(event);
    });
    textPane.setContentType("text/html");
    textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
    textPane.setCaretPosition(textPane.getDocument().getLength());
    textPane.setBackground(getBackground());
    textPane.setFocusable(true);
    textPane.getCaret().setVisible(true);
    textPane.setEditable(false);
    textPane.setBorder(JBUI.Borders.empty());
    return textPane;
  }

  private String convertMdToHtml(String message) {
    MutableDataSet options = new MutableDataSet();
    var document = Parser.builder(options).build().parse(message);
    return HtmlRenderer.builder(options)
        .nodeRendererFactory(new ResponseNodeRenderer.Factory())
        .build()
        .render(document);
  }

  private static class ResponseWrapper extends JPanel {

    ResponseWrapper() {
      super(new BorderLayout());
      setBorder(JBUI.Borders.empty());
      setBackground(getBackground());

      UIManager.addPropertyChangeListener(propertyChangeEvent -> setBackground(getBackground()));
    }
  }
}
