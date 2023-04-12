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
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.UIUtil;
import ee.carlrobert.codegpt.state.AccountDetailsState;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.util.FileUtils;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javax.swing.UIManager;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class MarkdownJCEFHtmlPanel extends JCEFHtmlPanel {

  private final CompletableFuture<Void> isLoaded = new CompletableFuture<>();
  private final JBCefJSQuery copyCodeQuery = JBCefJSQuery.create((JBCefBrowserBase) this);
  private final BrowserContentManager browserContentManager;
  private final Project project;
  private final JBCefJSQuery replaceInEditorQuery;
  private UUID previousResponseId;

  public MarkdownJCEFHtmlPanel(@NotNull Project project) {
    super(null);
    this.project = project;
    this.browserContentManager = new BrowserContentManager(getCefBrowser());
    this.replaceInEditorQuery = new ReplaceInEditorQuery(project, this, editor -> updateReplaceButton()).getQuery();
    this.copyCodeQuery.addHandler((text) -> {
      StringSelection stringSelection = new StringSelection(text);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, null);
      return null;
    });

    setHtml(getIndexContent());
    addBrowserLoadHandler();
    addLookAndFeelChangeListener();
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
    runWhenLoaded(() -> {
      conversation.getMessages().forEach(message -> {
        displayUserMessage(message.getPrompt());
        replaceHtml(prepareResponse(false, false), message.getResponse());
      });
      updateReplaceButton();
    });
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
    return FileUtils.getResource("/html/index.html")
        .replace("[prism-theme]", UIUtil.isUnderDarcula() ? "prism-darcula" : "prism-vs")
        .replace("[bg]", getBackgroundColorRGB())
        .replace("[font-color]", getFontColorRGB())
        .replace("[font-size]", String.valueOf(getFontSize()))
        .replace("[separator-color]", getSeparatorColorRGB())
        .replace("[disabled-color]", getDisabledTextColorRGB())
        .replace("[panel-background-color]", getPanelBackgroundColorRGB())
        .replace("[button-background-color]", getButtonBackgroundColorRGB())
        .replace("[button-disabled-background-color]", getDisabledButtonBackgroundColorRGB());
  }

  private void addBrowserLoadHandler() {
    getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
      public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
        if (httpStatusCode == 200) {
          myCefBrowser.executeJavaScript(
              "window.JavaPanelBridge = {" +
                  "copyCode : function(responseId) {" +
                  copyCodeQuery.inject("responseId") +
                  "}," +
                  "replaceCode : function(responseId) {" +
                  replaceInEditorQuery.inject("responseId") +
                  "}" +
                  "};",
              myCefBrowser.getURL(), 0);
          myCefBrowser.executeJavaScript(FileUtils.getResource("/html/js/main.js"), myCefBrowser.getURL(), 0);
          isLoaded.complete(null);
        }
      }
    }, getCefBrowser());
  }

  private void addLookAndFeelChangeListener() {
    UIManager.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        // TODO: Handle prism theme change
        execute(Map.of(
            "--bg", getBackgroundColorRGB(),
            "--font-color", getFontColorRGB(),
            "--font-size", getFontSize() + "px",
            "--separator-color", getSeparatorColorRGB(),
            "--disabled-color", getDisabledTextColorRGB(),
            "--panel-background-color", getPanelBackgroundColorRGB(),
            "--button-background-color", getButtonBackgroundColorRGB(),
            "--button-disabled-background-color", getDisabledButtonBackgroundColorRGB()
        ));
      }

      private void execute(Map<String, String> params) {
        var query = params.entrySet()
            .stream()
            .map((entry) -> format("document.documentElement.style.setProperty('%s', '%s');", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining());
        getCefBrowser().executeJavaScript(query, null, 0);
      }
    });
  }

  public void updateReplaceButton() {
    ApplicationManager.getApplication().invokeLater(() -> {
      var editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
      if (editor == null) {
        browserContentManager.updateRegenerateButton("Active editor not found", true);
      } else {
        var selectedText = editor.getSelectionModel().getSelectedText();
        var isDisabled = selectedText == null || selectedText.isEmpty();
        browserContentManager.updateRegenerateButton(isDisabled ? "No text highlighted" : "", isDisabled);
      }
    });
  }
}
