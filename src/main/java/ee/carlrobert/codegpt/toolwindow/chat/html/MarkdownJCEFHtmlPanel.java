package ee.carlrobert.codegpt.toolwindow.chat.html;

import static ee.carlrobert.codegpt.util.ThemeUtils.getBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getButtonBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getDisabledButtonBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getDisabledTextColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getFontColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getFontSize;
import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getScrollBarForegroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getScrollBarRadius;
import static ee.carlrobert.codegpt.util.ThemeUtils.getSeparatorColorRGB;
import static icons.Icons.DefaultImageIcon;
import static java.lang.String.format;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.UIUtil;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import ee.carlrobert.codegpt.state.conversations.message.Message;
import ee.carlrobert.codegpt.state.settings.SettingsState;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.NotNull;

public class MarkdownJCEFHtmlPanel extends JCEFHtmlPanel {

  private final CompletableFuture<Void> isLoaded = new CompletableFuture<>();
  private final JBCefJSQuery copyCodeQuery = JBCefJSQuery.create((JBCefBrowserBase) this);
  private final JBCefJSQuery deleteMessageQuery = JBCefJSQuery.create((JBCefBrowserBase) this);
  private final JBCefJSQuery replaceInEditorQuery;
  private final BrowserContentManager browserContentManager;
  private final Project project;
  private UUID previousResponseId;

  public MarkdownJCEFHtmlPanel(@NotNull Project project) {
    super(null);
    this.project = project;
    this.browserContentManager = new BrowserContentManager(getCefBrowser());
    this.replaceInEditorQuery = new ReplaceInEditorQuery(
        project, this, editor -> updateReplaceButton()).getQuery();
    this.copyCodeQuery.addHandler((text) -> {
      StringSelection stringSelection = new StringSelection(text);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, null);
      return null;
    });
    this.deleteMessageQuery.addHandler((messageId) -> {
      SwingUtilities.invokeLater(() -> {
        int answer = Messages.showYesNoDialog(
            "Are you sure you want to delete this message?", "Delete Message", DefaultImageIcon);
        if (answer == Messages.YES) {
          browserContentManager.deleteMessage(UUID.fromString(messageId));
          var conversation = ConversationsState.getCurrentConversation();
          if (conversation != null) {
            conversation.removeMessage(UUID.fromString(messageId));
            ConversationsState.getInstance().saveConversation(conversation);
          }
        }
      });
      return null;
    });

    setHtml(getIndexContent());
    addBrowserLoadHandler();
    addLookAndFeelChangeListener();
    addPopupMenu();
  }

  public void displayUserMessage(Message message) {
    var name = SettingsState.getInstance().displayName;
    browserContentManager.displayUserMessage(
        name == null || name.isEmpty() ? "User" : name, message);
  }

  public UUID prepareResponse(UUID messageId) {
    return prepareResponse(messageId, false, false);
  }

  public UUID prepareResponse(UUID messageId, boolean animate, boolean isRetry) {
    if (isRetry) {
      browserContentManager.clearResponse(previousResponseId);
      browserContentManager.animateSvg(previousResponseId);
    } else {
      previousResponseId = UUID.randomUUID();
      browserContentManager.displayResponse(messageId, previousResponseId, animate);
    }
    return previousResponseId;
  }

  public void displayConversation(Conversation conversation) {
    runWhenLoaded(() -> {
      conversation.getMessages().forEach(message -> {
        displayUserMessage(message);
        replaceHtml(prepareResponse(message.getId()), message.getResponse());
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

  public void displayMissingCredential(UUID messageId) {
    browserContentManager.displayMissingCredential(prepareResponse(messageId));
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

  public void updateReplaceButton() {
    ApplicationManager.getApplication().invokeLater(() -> {
      var editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
      if (editor == null) {
        browserContentManager.updateRegenerateButton("Active editor not found", true);
      } else {
        var selectedText = editor.getSelectionModel().getSelectedText();
        var isDisabled = selectedText == null || selectedText.isEmpty();
        browserContentManager.updateRegenerateButton(
            isDisabled ? "No text highlighted" : "", isDisabled);
      }
    });
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
        .replace("[scrollbar-color]", getScrollBarForegroundColorRGB())
        .replace("[scrollbar-radius]", String.valueOf(getScrollBarRadius()))
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
                  "copyCode : function(code) {" +
                  copyCodeQuery.inject("code") +
                  "}," +
                  "replaceCode : function(code) {" +
                  replaceInEditorQuery.inject("code") +
                  "}," +
                  "deleteMessage : function(messageId) {" +
                  deleteMessageQuery.inject("messageId") +
                  "}" +
                  "};",
              myCefBrowser.getURL(), 0);
          myCefBrowser.executeJavaScript(
              FileUtils.getResource("/html/js/main.js"), myCefBrowser.getURL(), 0);
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
            .map((entry) -> format("document.documentElement.style.setProperty('%s', '%s');",
                entry.getKey(), entry.getValue()))
            .collect(Collectors.joining());
        getCefBrowser().executeJavaScript(query, null, 0);
      }
    });
  }

  private void addPopupMenu() {
    var popupMenu = new HtmlPanelPopupMenu(getCefBrowser());
    getComponent().addMouseListener(popupMenu.getMouseAdapter());
    getComponent().setComponentPopupMenu(popupMenu);
  }
}
