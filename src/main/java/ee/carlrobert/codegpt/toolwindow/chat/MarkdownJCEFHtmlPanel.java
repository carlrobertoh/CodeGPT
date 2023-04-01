package ee.carlrobert.codegpt.toolwindow.chat;

import static java.lang.String.format;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import ee.carlrobert.codegpt.state.AccountDetailsState;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
    setPageBackgroundColor(getRGB(UIUtil.getPanelBackground()));
    setHtml(getIndexContent());
    getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
      public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
        if (httpStatusCode == 200) {
          isLoaded.complete(null);
        }
      }
    }, getCefBrowser());

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
    var panelBg = UIUtil.getPanelBackground();
    try {
      var stream = Objects.requireNonNull(MarkdownJCEFHtmlPanel.class.getResourceAsStream("/html/index.html"));
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8)
          .replace("[font-color]", getForegroundRGB())
          .replace("[font-size]", String.valueOf(JBFont.regular().getSize()))
          .replace("[separator-color]", getRGB(JBUI.CurrentTheme.CustomFrameDecorations.separatorForeground()))
          .replace("[panel-background-color]", getRGB(UIUtil.isUnderDarcula() ? toDarker(panelBg) : panelBg.brighter()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Color toDarker(Color color) {
    var factor = 0.9;
    return new Color(
        Math.max((int) (color.getRed() * factor), 0),
        Math.max((int) (color.getGreen() * factor), 0),
        Math.max((int) (color.getBlue() * factor), 0),
        color.getAlpha());
  }

  private static String getForegroundRGB() {
    return getRGB(EditorColorsManager.getInstance().getSchemeForCurrentUITheme().getDefaultForeground());
  }

  private static String getRGB(Color color) {
    return format("rgb(%d, %d, %d)", color.getRed(), color.getGreen(), color.getBlue());
  }
}
