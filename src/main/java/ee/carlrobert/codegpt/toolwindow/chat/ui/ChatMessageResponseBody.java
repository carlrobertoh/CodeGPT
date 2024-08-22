package ee.carlrobert.codegpt.toolwindow.chat.ui;

import static ee.carlrobert.codegpt.toolwindow.chat.StreamResponseType.CODE;
import static ee.carlrobert.codegpt.util.MarkdownUtil.convertMdToHtml;
import static java.lang.String.format;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.icons.AllIcons.General;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.parser.Parser;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.actions.ActionType;
import ee.carlrobert.codegpt.events.AnalysisCompletedEventDetails;
import ee.carlrobert.codegpt.events.AnalysisFailedEventDetails;
import ee.carlrobert.codegpt.events.CodeGPTEvent;
import ee.carlrobert.codegpt.events.EventDetails;
import ee.carlrobert.codegpt.events.WebSearchEventDetails;
import ee.carlrobert.codegpt.settings.GeneralSettingsConfigurable;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import ee.carlrobert.codegpt.toolwindow.chat.StreamParser;
import ee.carlrobert.codegpt.toolwindow.chat.editor.ResponseEditorPanel;
import ee.carlrobert.codegpt.toolwindow.ui.WebpageList;
import ee.carlrobert.codegpt.ui.UIUtil;
import ee.carlrobert.codegpt.util.EditorUtil;
import ee.carlrobert.codegpt.util.MarkdownUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Objects;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

public class ChatMessageResponseBody extends JPanel {

  private final Project project;
  private final Disposable parentDisposable;
  private final StreamParser streamParser;
  private final boolean readOnly;
  private final DefaultListModel<WebSearchEventDetails> webpageListModel = new DefaultListModel<>();
  private final WebpageList webpageList = new WebpageList(webpageListModel);
  private final JPanel webDocProgressContainer = new JPanel();
  private final AsyncProcessIcon spinner = new AsyncProcessIcon("sign_in_spinner");
  private ResponseEditorPanel currentlyProcessedEditorPanel;
  private JTextPane currentlyProcessedTextPane;
  private JPanel webpageListPanel;
  private boolean responseReceived;

  public ChatMessageResponseBody(Project project, Disposable parentDisposable) {
    this(project, false, parentDisposable);
  }

  public ChatMessageResponseBody(
      Project project,
      boolean withGhostText,
      Disposable parentDisposable) {
    this(project, withGhostText, false, false, false, parentDisposable);
  }

  public ChatMessageResponseBody(
      Project project,
      boolean withGhostText,
      boolean readOnly,
      boolean webSearchIncluded,
      boolean webDocIncluded,
      Disposable parentDisposable) {
    super(new BorderLayout());
    this.project = project;
    this.parentDisposable = parentDisposable;
    this.streamParser = new StreamParser();
    this.readOnly = readOnly;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setOpaque(false);

    if (webSearchIncluded) {
      webpageListPanel = createWebpageListPanel(webpageList);
      add(webpageListPanel);
    }

    if (webDocIncluded) {
      webDocProgressContainer.setLayout(new BoxLayout(webDocProgressContainer, BoxLayout.Y_AXIS));
      webDocProgressContainer.setBorder(JBUI.Borders.emptyBottom(8));
      add(webDocProgressContainer);
    }

    if (withGhostText) {
      prepareProcessingText(!readOnly);
      currentlyProcessedTextPane.setText(
          "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">&#8205;</p></html>");
    }
  }

  public ChatMessageResponseBody withResponse(String response) {
    for (var message : MarkdownUtil.splitCodeBlocks(response)) {
      processResponse(message, message.startsWith("```"), false);
    }

    return this;
  }

  public void update(String partialMessage) {
    for (var item : streamParser.parse(partialMessage)) {
      processResponse(item.response(), CODE.equals(item.type()), true);
    }
  }

  public void displayMissingCredential() {
    ApplicationManager.getApplication().invokeLater(() -> {
      var message = "API key not provided. Open <a href=\"#\">Settings</a> to set one.";
      currentlyProcessedTextPane.setText(
          format("<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">%s</p></html>", message));
      currentlyProcessedTextPane.addHyperlinkListener(e -> {
        if (e.getEventType() == ACTIVATED) {
          ShowSettingsUtil.getInstance()
              .showSettingsDialog(project, GeneralSettingsConfigurable.class);
        }
      });
      hideCaret();

      if (webpageListPanel != null) {
        webpageListPanel.setVisible(false);
      }
    });
  }

  public void displayQuotaExceeded() {
    ApplicationManager.getApplication().invokeLater(() -> {
      currentlyProcessedTextPane.setText("<html>"
          + "<p style=\"margin-top: 4px; margin-bottom: 8px;\">"
          + "You exceeded your current quota, please check your plan and billing details, "
          + "or <a href=\"#CHANGE_PROVIDER\">change</a> to a different LLM provider.</p>"
          + "</html>");

      currentlyProcessedTextPane.addHyperlinkListener(e -> {
        if (e.getEventType() == ACTIVATED) {
          ShowSettingsUtil.getInstance()
              .showSettingsDialog(project, GeneralSettingsConfigurable.class);
          TelemetryAction.IDE_ACTION.createActionMessage()
              .property("action", ActionType.CHANGE_PROVIDER.name())
              .send();
        }
      });
      hideCaret();

      if (webpageListPanel != null) {
        webpageListPanel.setVisible(false);
      }
    });
  }

  public void displayError(String message) {
    ApplicationManager.getApplication().invokeLater(() -> {
      var errorText = format(
          "<html><p style=\"margin-top: 4px; margin-bottom: 8px;\">%s</p></html>",
          message);
      if (responseReceived) {
        add(createTextPane(errorText, false));
      } else {
        currentlyProcessedTextPane.setText(errorText);
      }
      hideCaret();

      if (webpageListPanel != null) {
        webpageListPanel.setVisible(false);
      }
    });
  }

  public void handleCodeGPTEvent(CodeGPTEvent codegptEvent) {
    ApplicationManager.getApplication()
        .invokeLater(() -> {
          var event = codegptEvent.getEvent();
          if (event.getDetails() instanceof WebSearchEventDetails webSearchEventDetails) {
            displayWebSearchItem(webSearchEventDetails);
            return;
          }

          switch (event.getType()) {
            case WEB_SEARCH_ITEM -> {
              if (event.getDetails() != null
                  && event.getDetails() instanceof WebSearchEventDetails eventDetails) {
                displayWebSearchItem(eventDetails);
              }
            }

            case ANALYZE_WEB_DOC_STARTED -> showWebDocsProgress();
            case ANALYZE_WEB_DOC_COMPLETED -> completeWebDocsProgress(event.getDetails());
            case ANALYZE_WEB_DOC_FAILED -> failWebDocsProgress(event.getDetails());
            default -> {
            }
          }
        });
  }

  public void hideCaret() {
    if (currentlyProcessedTextPane != null) {
      currentlyProcessedTextPane.getCaret().setVisible(false);
    }
  }

  public void clear() {
    removeAll();

    streamParser.clear();
    // TODO: First message might be code block
    prepareProcessingText(true);
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
        ApplicationManager.getApplication().invokeLater(() -> {
          if (currentlyProcessedEditorPanel == null) {
            prepareProcessingCode(code, codeBlock.getInfo().unescape());
          }
          EditorUtil.updateEditorDocument(currentlyProcessedEditorPanel.getEditor(), code);
        });
      }
    }
  }

  private void processText(String markdownText, boolean caretVisible) {
    ApplicationManager.getApplication().invokeLater(() -> {
      if (currentlyProcessedTextPane == null) {
        prepareProcessingText(caretVisible);
      }
      currentlyProcessedTextPane.setText(convertMdToHtml(markdownText));
    });
  }

  private void prepareProcessingText(boolean caretVisible) {
    currentlyProcessedEditorPanel = null;
    currentlyProcessedTextPane = createTextPane("", caretVisible);
    add(currentlyProcessedTextPane);
  }

  private void prepareProcessingCode(String code, String markdownLanguage) {
    hideCaret();
    currentlyProcessedTextPane = null;
    currentlyProcessedEditorPanel =
        new ResponseEditorPanel(project, code, markdownLanguage, readOnly, parentDisposable);
    add(currentlyProcessedEditorPanel);
  }

  private void displayWebSearchItem(WebSearchEventDetails details) {
    webpageListModel.addElement(details);
    webpageList.revalidate();
    webpageList.repaint();
  }

  private void showWebDocsProgress() {
    var wrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    wrapper.add(spinner);
    wrapper.add(Box.createHorizontalStrut(4));
    wrapper.add(new JBLabel(
        CodeGPTBundle.get("chatMessageResponseBody.webDocs.startProgress.label")).withFont(
        JBFont.small()));
    updateWebDocsProgress(wrapper);
  }

  private void completeWebDocsProgress(EventDetails eventDetails) {
    if (eventDetails instanceof AnalysisCompletedEventDetails defaultEventDetails) {
      updateWebDocsProgressLabel(defaultEventDetails.getDescription(), Icons.GreenCheckmark);
    }
  }

  private void failWebDocsProgress(EventDetails eventDetails) {
    if (eventDetails instanceof AnalysisFailedEventDetails failedEventDetails) {
      updateWebDocsProgressLabel(failedEventDetails.getError(), General.Error);
    }
  }

  private void updateWebDocsProgressLabel(String text, Icon icon) {
    updateWebDocsProgress(new JBLabel(text, icon, SwingConstants.LEADING).withFont(JBFont.small()));
  }

  private void updateWebDocsProgress(Component content) {
    webDocProgressContainer.removeAll();
    webDocProgressContainer.add(JBUI.Panels.simplePanel(content));
    webDocProgressContainer.revalidate();
    webDocProgressContainer.repaint();
  }

  private JTextPane createTextPane(String text, boolean caretVisible) {
    var textPane = UIUtil.createTextPane(text, false, event -> {
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
    return textPane;
  }

  private static JPanel createWebpageListPanel(WebpageList webpageList) {
    var title = new JPanel(new BorderLayout());
    title.setOpaque(false);
    title.setBorder(JBUI.Borders.empty(8, 0));
    title.add(new JBLabel(CodeGPTBundle.get("chatMessageResponseBody.webPages.title"))
        .withFont(JBUI.Fonts.miniFont()), BorderLayout.LINE_START);
    var listPanel = new JPanel(new BorderLayout());
    listPanel.add(webpageList, BorderLayout.LINE_START);

    var panel = new JPanel(new BorderLayout());
    panel.add(title, BorderLayout.NORTH);
    panel.add(listPanel, BorderLayout.CENTER);
    return panel;
  }
}
