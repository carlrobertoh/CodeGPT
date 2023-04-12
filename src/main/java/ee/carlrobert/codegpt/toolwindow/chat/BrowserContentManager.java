package ee.carlrobert.codegpt.toolwindow.chat;

import static icons.Icons.getHtmlSvgIcon;
import static org.apache.commons.text.StringEscapeUtils.escapeEcmaScript;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.UUID;
import org.cef.browser.CefBrowser;

public class BrowserContentManager {

  private final CefBrowser browser;

  BrowserContentManager(CefBrowser browser) {
    this.browser = browser;
  }

  public void displayUserMessage(String accountName, String prompt) {
    executeJS("window.CodeGPTBridge.displayUserMessage(?, ?, ?);", accountName, convertToHtml(prompt), getHtmlSvgIcon("delete-icon"));
  }

  public void displayResponse(UUID responseId, boolean animate) {
    executeJS("window.CodeGPTBridge.displayResponse(?, ?, ?);", responseId, animate, getHtmlSvgIcon("codegpt-icon"));
  }

  public void replaceResponseContent(UUID responseId, String message) {
    executeJS("window.CodeGPTBridge.replaceResponseContent(?, ?);", responseId, convertToHtml(message));
  }

  public void displayLandingView() {
    executeJS("window.CodeGPTBridge.displayLandingView();");
  }

  public void displayErrorMessage(UUID responseId, String errorMessage) {
    executeJS("window.CodeGPTBridge.displayErrorMessage(?, ?);", responseId, convertToHtml(errorMessage));
  }

  public void displayMissingCredential(UUID responseId) {
    executeJS("window.CodeGPTBridge.displayMissingCredential(?);", responseId);
  }

  public void stopTyping() {
    executeJS("window.CodeGPTBridge.stopTyping();");
  }

  public void clearResponse(UUID responseId) {
    executeJS("window.CodeGPTBridge.clearResponse(?);", responseId);
  }

  public void animateSvg(UUID responseId) {
    executeJS("window.CodeGPTBridge.animateCodeGPTSvg(?);", responseId);
  }

  public void updateRegenerateButton(String title, boolean isDisabled) {
    executeJS("window.CodeGPTBridge.updateRegenerateButton(?, ?);", title, isDisabled);
  }

  private void executeJS(String query, Object... params) {
    browser.executeJavaScript(formatQuery(query, params), null, 0);
  }

  private String formatQuery(String query, Object... params) {
    var tokens = query.split("\\?");
    StringBuilder queryBuilder = new StringBuilder();
    for (int i = 0; i < params.length; i++) {
      var param = "\"" + escapeEcmaScript(params[i].toString()) + "\"";
      if (params[i] instanceof Boolean) {
        param = params[i].toString();
      }
      queryBuilder.append(tokens[i]).append(param);
    }
    return queryBuilder.append(tokens[params.length]).toString();
  }

  private String convertToHtml(String message) {
    MutableDataSet options = new MutableDataSet();
    var document = Parser.builder(options).build().parse(message);
    return HtmlRenderer.builder(options)
        .nodeRendererFactory(new CodeBlockNodeRenderer.Factory())
        .build()
        .render(document);
  }
}
