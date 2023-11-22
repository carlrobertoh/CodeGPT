package ee.carlrobert.codegpt.toolwindow.chat.components;

import static ee.carlrobert.codegpt.util.UIUtil.getPanelBackgroundColor;
import static java.lang.String.format;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
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
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.settings.SettingsConfigurable;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.ResponseNodeRenderer;
import ee.carlrobert.codegpt.toolwindow.chat.StreamParser;
import ee.carlrobert.codegpt.toolwindow.chat.StreamResponseType;
import ee.carlrobert.codegpt.toolwindow.chat.editor.ResponseEditorPanel;
import ee.carlrobert.codegpt.util.MarkdownUtil;
import ee.carlrobert.codegpt.util.UIUtil;
import ee.carlrobert.llm.client.you.completion.YouSerpResult;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;

public class ChatMessageResponseBody extends JPanel {

  private final Project project;
  private final Disposable parentDisposable;
  private final StreamParser streamParser;
  private final boolean readOnly;
  private JPanel currentlyProcessedElement;
  private ResponseEditorPanel currentlyProcessedEditor;
  private JTextPane currentlyProcessedTextPane;
  private boolean responseReceived;

  public ChatMessageResponseBody(Project project, Disposable parentDisposable) {
    this(project, getPanelBackgroundColor(), false, parentDisposable);
  }

  public ChatMessageResponseBody(
      Project project,
      boolean withGhostText,
      Disposable parentDisposable) {
    this(project, getPanelBackgroundColor(), withGhostText, parentDisposable);
  }

  public ChatMessageResponseBody(
      Project project,
      Color backgroundColor,
      boolean withGhostText,
      Disposable parentDisposable) {
    this(project, backgroundColor, withGhostText, false, parentDisposable);
  }

  public ChatMessageResponseBody(
      Project project,
      Color backgroundColor,
      boolean withGhostText,
      boolean readOnly,
      Disposable parentDisposable) {
    super(new BorderLayout());
    this.project = project;
    this.parentDisposable = parentDisposable;
    this.streamParser = new StreamParser();
    this.readOnly = readOnly;
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setBackground(backgroundColor);

    if (withGhostText) {
      prepareProcessingTextResponse(!readOnly);
      currentlyProcessedTextPane.setText(
          "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">&#8205;</p></html>");
    }

    UIManager.addPropertyChangeListener(propertyChangeEvent -> setBackground(backgroundColor));
  }

  public ChatMessageResponseBody withResponse(String response) {
    for (var message : MarkdownUtil.splitCodeBlocks(response)) {
      processResponse(message, message.startsWith("```"), false);
    }

    return this;
  }

  public void update(String partialMessage) {
    for (var item : streamParser.parse(partialMessage)) {
      processResponse(item.getResponse(), StreamResponseType.CODE.equals(item.getType()), true);
    }
  }

  public void displayMissingCredential() {
    var message = "API key not provided. Open <a href=\"#\">Settings</a> to set one.";
    currentlyProcessedTextPane.setText(
        format("<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">%s</p></html>", message));
    currentlyProcessedTextPane.addHyperlinkListener(e -> {
      if (e.getEventType() == ACTIVATED) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, SettingsConfigurable.class);
      }
    });
    currentlyProcessedTextPane.getCaret().setVisible(false);
  }

  public void displayQuotaExceeded() {
    currentlyProcessedTextPane.setText("<html>"
        + "<p style=\"margin-top: 4px; margin-bottom: 8px;\">"
        + "You exceeded your current quota, please check your plan and billing details, "
        + "or <a href=\"#CHANGE_PROVIDER\">change</a> to a different LLM provider.</p>"
        + "</html>");

    currentlyProcessedTextPane.addHyperlinkListener(e -> {
      if (e.getEventType() == ACTIVATED) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, SettingsConfigurable.class);
        TelemetryAction.IDE_ACTION.createActionMessage()
            .property("action", ActionType.CHANGE_PROVIDER.name())
            .send();
      }
    });
    hideCaret();
  }

  public void hideCaret() {
    if (currentlyProcessedTextPane != null) {
      currentlyProcessedTextPane.getCaret().setVisible(false);
    }
  }

  public void displayError(String message) {
    var errorText = format(
        "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">%s</p></html>",
        message);
    if (responseReceived) {
      add(new ResponseWrapper().add(createTextPane(errorText, false)));
    } else {
      currentlyProcessedTextPane.setText(errorText);
    }
  }

  public void displayDefaultError() {
    displayError("Something went wrong.");
  }

  public void displaySerpResults(List<YouSerpResult> serpResults) {
    var html = getSearchResultsHtml(serpResults);
    if (responseReceived) {
      add(new ResponseWrapper().add(createTextPane(html, false)));
    } else {
      currentlyProcessedTextPane.setText(html);
    }
  }

  private String getSearchResultsHtml(List<YouSerpResult> serpResults) {
    var titles = serpResults.stream()
        .map(result -> format("<li style=\"margin-bottom: 4px;\"><a href=\"%s\">%s</a></li>",
            result.getUrl(), result.getName()))
        .collect(Collectors.joining());
    return format(
        "<html>"
            + "<p><strong>Search results:</strong></p>"
            + "<ol>%s</ol>"
            + "</html>", titles);
  }

  public void clear() {
    removeAll();

    streamParser.clear();
    // TODO: First message might be code block
    prepareProcessingTextResponse(true);
    currentlyProcessedTextPane.setText(
        "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">&#8205;</p></html>");

    repaint();
    revalidate();
  }

  private void processResponse(String markdownInput, boolean codeResponse, boolean caretVisible) {
    responseReceived = true;

    if (codeResponse) {
      processCode(markdownInput);
    } else {
      processText(markdownInput, caretVisible);
    }
  }

  private void processCode(String markdownCode) {
    var document = Parser.builder().build().parse(markdownCode);
    var child = document.getChildOfType(FencedCodeBlock.class);
    if (child != null) {
      var codeBlock = ((FencedCodeBlock) child);
      var code = codeBlock.getContentChars().unescape();
      if (!code.isEmpty()) {
        if (currentlyProcessedEditor == null) {
          prepareProcessingCodeResponse(code, codeBlock.getInfo().unescape());
        }
        updateEditorDocument(code);
      }
    }
  }

  private void processText(String markdownText, boolean caretVisible) {
    if (currentlyProcessedTextPane == null) {
      prepareProcessingTextResponse(caretVisible);
    }
    currentlyProcessedTextPane.setText(convertMdToHtml(markdownText));
  }

  private void prepareProcessingTextResponse(boolean caretVisible) {
    currentlyProcessedEditor = null;
    currentlyProcessedTextPane = createTextPane("", caretVisible);
    currentlyProcessedElement = new ResponseWrapper();
    currentlyProcessedElement.add(currentlyProcessedTextPane);
    add(currentlyProcessedElement);
  }

  private void prepareProcessingCodeResponse(String code, String markdownLanguage) {
    currentlyProcessedTextPane.getCaret().setVisible(false);
    currentlyProcessedTextPane = null;
    currentlyProcessedEditor = new ResponseEditorPanel(
        project,
        code,
        markdownLanguage,
        readOnly,
        getPanelBackgroundColor(),
        parentDisposable);
    currentlyProcessedElement = new ResponseWrapper();
    currentlyProcessedElement.add(currentlyProcessedEditor);
    add(currentlyProcessedElement);
  }

  private void updateEditorDocument(String code) {
    var editor = currentlyProcessedEditor.getEditor();
    var document = editor.getDocument();
    var application = ApplicationManager.getApplication();
    Runnable updateDocumentRunnable = () -> application.runWriteAction(() ->
        WriteCommandAction.runWriteCommandAction(project, () ->
            document.replaceString(0, document.getTextLength(), code)));

    if (application.isUnitTestMode()) {
      application.invokeAndWait(updateDocumentRunnable);
    } else {
      application.invokeLater(updateDocumentRunnable);
    }
  }

  private JTextPane createTextPane(String text, boolean caretVisible) {
    var textPane = UIUtil.createTextPane(text, event -> {
      if (FileUtil.exists(event.getDescription()) && ACTIVATED.equals(event.getEventType())) {
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(event.getDescription());
        FileEditorManager.getInstance(project).openFile(Objects.requireNonNull(file), true);
        return;
      }

      UIUtil.handleHyperlinkClicked(event);
    });
    if (caretVisible) {
      textPane.getCaret().setVisible(true);
      textPane.setCaretPosition(textPane.getDocument().getLength());
    }
    textPane.setBorder(JBUI.Borders.empty());
    textPane.setBackground(getBackground());
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
