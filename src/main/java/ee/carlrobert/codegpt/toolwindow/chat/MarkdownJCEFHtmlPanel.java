package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.util.ThemeUtils.getBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getButtonBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getDisabledButtonBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getDisabledTextColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getFontColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getFontSize;
import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getSeparatorColorRGB;
import static java.lang.String.format;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.UIUtil;
import ee.carlrobert.codegpt.state.AccountDetailsState;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.swing.UIManager;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MarkdownJCEFHtmlPanel extends JCEFHtmlPanel {

  private final CompletableFuture<Void> isLoaded = new CompletableFuture<>();
  private final JBCefJSQuery copyCodeQuery = JBCefJSQuery.create((JBCefBrowserBase) this);
  private final JBCefJSQuery replaceCodeQuery = JBCefJSQuery.create((JBCefBrowserBase) this);
  private final BrowserContentManager browserContentManager;
  private UUID previousResponseId;

  public MarkdownJCEFHtmlPanel(@Nullable Editor editor) {
    super(null);
    this.browserContentManager = new BrowserContentManager(getCefBrowser(), editor);
    this.copyCodeQuery.addHandler((text) -> {
      StringSelection stringSelection = new StringSelection(text);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, null);
      return null;
    });
    if (editor != null) {
      ApplicationManager.getApplication().runReadAction(() ->
          editor.getSelectionModel().addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChanged(@NotNull SelectionEvent e) {
              SelectionListener.super.selectionChanged(e);
              var selectedText = editor.getSelectionModel().getSelectedText();
              browserContentManager.setReplaceButtonDisabled(selectedText == null || selectedText.isEmpty());
            }
          }));
      this.replaceCodeQuery.addHandler((text) -> {
        ApplicationManager.getApplication().invokeLater(() ->
            ApplicationManager.getApplication().runWriteAction(() ->
                WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> {
                  var editorDoc = editor.getDocument();
                  editorDoc.replaceString(editor.getSelectionModel().getSelectionStart(), editor.getSelectionModel().getSelectionEnd(), text);
                })));
        return null;
      });
    }

    setHtml(getIndexContent());
    addBrowserLoadHandler();
    addLookAndFeelChangeListener();

    var popupMenu = new HtmlPanelPopupMenu(editor, getCefBrowser());
    getComponent().addMouseListener(popupMenu.getMouseAdapter());
    getComponent().setComponentPopupMenu(popupMenu);
  }

  public void displayUserMessage(String prompt) {
    var name = AccountDetailsState.getInstance().accountName;
    browserContentManager.displayUserMessage(name == null || name.isEmpty() ? "User" : name, prompt);
  }

  public UUID prepareResponse(boolean animate, boolean isRetry) {
    if (isRetry) {
      browserContentManager.clearResponse(previousResponseId);
      browserContentManager.animateSvg(previousResponseId);
    } else {
      previousResponseId = UUID.randomUUID();
      browserContentManager.displayResponse(previousResponseId, animate);
    }
    return previousResponseId;
  }

  public void displayConversation(Conversation conversation) {
    runWhenLoaded(() -> conversation.getMessages().forEach(message -> {
      displayUserMessage(message.getPrompt());
      replaceHtml(prepareResponse(false, false), message.getResponse());
    }));
  }

  public void displayErrorMessage() {
    displayErrorMessage("Something went wrong. Please try again later.");
  }

  public void displayErrorMessage(String errorMessage) {
    browserContentManager.displayErrorMessage(previousResponseId, errorMessage);
  }

  public void displayMissingCredential() {
    browserContentManager.displayMissingCredential(prepareResponse(false, false));
  }

  public void displayLandingView() {
    runWhenLoaded(browserContentManager::displayLandingView);
  }

  public void replaceHtml(UUID responseId, String message) {
    browserContentManager.replaceResponseContent(responseId, message);
  }

  public void stopTyping() {
    browserContentManager.stopTyping();
  }

  public void runWhenLoaded(Runnable runnable) {
    isLoaded.thenRun(runnable);
  }

  private String getIndexContent() {
    try {
      var stream = Objects.requireNonNull(MarkdownJCEFHtmlPanel.class.getResourceAsStream("/html/index.html"));
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8)
          .replace("[prism-theme]", UIUtil.isUnderDarcula() ? "prism-darcula" : "prism-vs")
          .replace("[bg]", getBackgroundColorRGB())
          .replace("[font-color]", getFontColorRGB())
          .replace("[font-size]", String.valueOf(getFontSize()))
          .replace("[separator-color]", getSeparatorColorRGB())
          .replace("[disabled-color]", getDisabledTextColorRGB())
          .replace("[panel-background-color]", getPanelBackgroundColorRGB())
          .replace("[button-background-color]", getButtonBackgroundColorRGB())
          .replace("[button-disabled-background-color]", getDisabledButtonBackgroundColorRGB());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void addBrowserLoadHandler() {
    getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
      public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
        if (httpStatusCode == 200) {
          isLoaded.complete(null);
          myCefBrowser.executeJavaScript(
              "window.JavaPanelBridge = {" +
                  "copyCode : function(responseId) {" +
                  copyCodeQuery.inject("responseId") +
                  "}," +
                  "replaceCode : function(responseId) {" +
                  replaceCodeQuery.inject("responseId") +
                  "}" +
                  "};",
              myCefBrowser.getURL(), 0);
        }
      }
    }, getCefBrowser());
  }

  private void addLookAndFeelChangeListener() {
    UIManager.addPropertyChangeListener(e -> {
      if ("lookAndFeel".equals(e.getPropertyName())) {
        var browser = getCefBrowser();
        browser.executeJavaScript(
            // TODO: Handle prism theme change
            format("document.documentElement.style.setProperty('--bg', '%s');", getBackgroundColorRGB()) +
                format("document.documentElement.style.setProperty('--font-color', '%s');", getFontColorRGB()) +
                format("document.documentElement.style.setProperty('--font-size', '%dpx');", getFontSize()) +
                format("document.documentElement.style.setProperty('--separator-color', '%s');", getSeparatorColorRGB()) +
                format("document.documentElement.style.setProperty('--disabled-color', '%s');", getDisabledTextColorRGB()) +
                format("document.documentElement.style.setProperty('--panel-background-color', '%s');", getPanelBackgroundColorRGB()) +
                format("document.documentElement.style.setProperty('--button-background-color', '%s');", getButtonBackgroundColorRGB()) +
                format("document.documentElement.style.setProperty('--button-disabled-background-color', '%s');",
                    getDisabledButtonBackgroundColorRGB()),
            null,
            0
        );
      }
    });
  }

  public void updateReplaceButton(Editor editor) {
    if (editor != null) {
      ApplicationManager.getApplication().runReadAction(() -> {
        var selectedText = editor.getSelectionModel().getSelectedText();
        browserContentManager.setReplaceButtonDisabled(selectedText == null || selectedText.isEmpty());
      });
    }
  }
}
