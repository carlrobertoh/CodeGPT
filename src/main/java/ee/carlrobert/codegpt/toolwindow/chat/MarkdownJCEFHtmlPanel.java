package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.util.ThemeUtils.getBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getFontColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getFontSize;
import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColorRGB;
import static ee.carlrobert.codegpt.util.ThemeUtils.getSeparatorColorRGB;
import static java.lang.String.format;

import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.UIUtil;
import ee.carlrobert.codegpt.state.AccountDetailsState;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.swing.UIManager;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;

public class MarkdownJCEFHtmlPanel extends JCEFHtmlPanel {

  private final CompletableFuture<Void> isLoaded = new CompletableFuture<>();
  private final BrowserContentManager browserContentManager;
  private UUID previousResponseId;

  public MarkdownJCEFHtmlPanel() {
    super(null);
    this.browserContentManager = new BrowserContentManager(getCefBrowser());
    setHtml(getIndexContent());
    getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
      public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
        if (httpStatusCode == 200) {
          isLoaded.complete(null);
        }
      }
    }, getCefBrowser());

    UIManager.addPropertyChangeListener(e -> {
      if ("lookAndFeel".equals(e.getPropertyName())) {
        var browser = getCefBrowser();
        browser.executeJavaScript(
            // TODO: Handle prism theme change
            format("document.documentElement.style.setProperty('--bg', '%s');", getBackgroundColorRGB()) +
                format("document.documentElement.style.setProperty('--font-color', '%s');", getFontColorRGB()) +
                format("document.documentElement.style.setProperty('--font-size', '%dpx');", getFontSize()) +
                format("document.documentElement.style.setProperty('--separator-color', '%s');", getSeparatorColorRGB()) +
                format("document.documentElement.style.setProperty('--panel-background-color', '%s');", getPanelBackgroundColorRGB()),
            null,
            0
        );
      }
    });

    /*var popupMenu = new HtmlPanelPopupMenu();
    getComponent().addMouseListener(popupMenu.getMouseAdapter());
    getComponent().setComponentPopupMenu(popupMenu);*/
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
          .replace("[panel-background-color]", getPanelBackgroundColorRGB());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
