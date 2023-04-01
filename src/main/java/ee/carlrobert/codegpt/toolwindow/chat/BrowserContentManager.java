package ee.carlrobert.codegpt.toolwindow.chat;

import static org.apache.commons.text.StringEscapeUtils.escapeEcmaScript;

import com.intellij.util.ui.UIUtil;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import org.cef.browser.CefBrowser;

// TODO: Find a better way to work with JS
public class BrowserContentManager {

  private final CefBrowser browser;

  BrowserContentManager(CefBrowser browser) {
    this.browser = browser;
  }

  // language=javascript
  public void displayUserMessage(String accountName, String prompt) {
    clearLandingViewIfPresent();
    executeIIFE(
        "const wrapper = document.createElement('div');" +
        "wrapper.classList.add('user-message');" +

        "const nameLabel = document.createElement('p');" +
        "nameLabel.textContent = '" + accountName + "';" +
        "wrapper.appendChild(nameLabel);" +

        "const message = document.createElement('div');" +
        "message.innerHTML = \"" + convertToHtml(prompt) + "\";" +
        "wrapper.appendChild(message);" +

        "document.body.appendChild(wrapper);" +
        "Prism.highlightAll();"
    );
    scrollToBottom();
  }

  // language=javascript
  public void displayResponse(UUID responseId, boolean animate) {
    executeIIFE(
        "const wrapper = document.createElement('div');" +
        "wrapper.classList.add('response');" +

        "const iconLabelContainer = document.createElement('p');" +
        "const svgWrapper = document.createElement('span');" +
        "svgWrapper.classList.add('icon-wrapper');" +
        "svgWrapper.innerHTML = '" + escapeEcmaScript(getSvgIcon()) + "';" +
        (animate ? "svgWrapper.style.animation = 'roll 2.4s infinite linear';" : "") +
        "iconLabelContainer.appendChild(svgWrapper);" +
        "const label = document.createElement('strong');" +
        "label.textContent = 'CodeGPT';" +
        "iconLabelContainer.appendChild(label);" +
        "wrapper.appendChild(iconLabelContainer);" +

        "const responseWrapper = document.createElement('div');" +
        "responseWrapper.setAttribute('id', '" + responseId + "');" +
        "responseWrapper.innerHTML = \"<p>&#8205;</p>\";" + // display invisible character so that the height doesn't jump on the first response
        "wrapper.appendChild(responseWrapper);" +
        "document.body.appendChild(wrapper);"
    );
    scrollToBottom();
  }

  // language=javascript
  public void replaceResponseContent(UUID responseId, String message) {
    executeIIFE(
        "const responseWrapper = document.getElementById('" + responseId + "');" +
        "responseWrapper.innerHTML = \"" + convertToHtml(message) + "\";" +
        "Prism.highlightAll();"
    );
    scrollToBottom();
  }

  // language=javascript
  public void displayLandingView() {
    executeIIFE(
        "const wrapper = document.createElement('div');" +
        "wrapper.setAttribute('id', 'landing-view');" +
        "const buttonsWrapper = document.createElement('div');" +
        "buttonsWrapper.innerHTML = " +
        "'<h1>Examples</h1>" +
        "<span class=\"example-text\">\"How do I make an HTTP request in Javascript?\"</span>" +
        "<span class=\"example-text\">\"What is the difference between px, dip, dp, and sp?\"</span>" +
        "<span class=\"example-text\">\"How do I undo the most recent local commits in Git?\"</span>" +
        "<span class=\"example-text\">\"What is the difference between stack and heap?\"</span>';" +
        "wrapper.appendChild(buttonsWrapper);" +
        "document.body.appendChild(wrapper);"
    );
  }

  // language=javascript
  public void displayErrorMessage(UUID responseId, String errorMessage) {
    executeIIFE(
        "const errorLabel = document.createElement('p');" +
        "errorLabel.textContent = '" + convertToHtml(errorMessage) + "';" +
        "const responseWrapper = document.getElementById('" + responseId + "');" +
        "responseWrapper.appendChild(errorLabel);"
    );
  }

  // language=javascript
  public void displayMissingCredential(UUID responseId) {
    executeIIFE(
        "const responseWrapper = document.getElementById('" + responseId + "');" +
        // TODO: Add anchor link for opening the settings panel
        "responseWrapper.innerHTML = \"<p>API key not provided.</p>\";"
    );
  }

  // language=javascript
  public void stopTyping() {
    executeIIFE(
        "const icons = Array.from(document.getElementsByClassName('icon-wrapper'));" +
        "for (const icon of icons) {" +
        "  icon.style = null;" +
        "}"
    );
  }

  // language=javascript
  public void clearLandingViewIfPresent() {
    executeIIFE("document.getElementById(\"landing-view\")?.remove();");
  }

  // language=javascript
  public void clearResponse(UUID responseId) {
    executeIIFE(
        "var responseWrapper = document.getElementById('" + responseId + "');" +
        "responseWrapper.innerHTML = \"<p>&#8205;</p>\";"
    );
  }

  // language=javascript
  public void animateSvg(UUID responseId) {
    executeIIFE(
        "var response = document.getElementById('" + responseId + "');" +
        "var svg = response.parentElement.getElementsByClassName('icon-wrapper')[0];" +
        "svg.style.animation = 'roll 2.4s infinite linear';"
    );
  }

  // language=javascript
  private void scrollToBottom() {
    executeJS(
        "window.scrollTo({" +
        "  top: document.body.scrollHeight," +
        "  behavior: 'smooth'" +
        "});"
    );
  }

  // language=javascript
  private void executeIIFE(String code) {
    executeJS("(function () {" + code + "})();");
  }

  private void executeJS(String code) {
    browser.executeJavaScript(code, null, 0);
  }

  private String convertToHtml(String message) {
    MutableDataSet options = new MutableDataSet();
    var document = Parser.builder(options).build().parse(message);
    var html = HtmlRenderer.builder(options)
        .build()
        .render(document);
    return escapeEcmaScript(html);
  }

  private String getSvgIcon() {
    try {
      var stream = Objects.requireNonNull(BrowserContentManager.class.getResourceAsStream(
          UIUtil.isUnderDarcula() ? "/html/icons/codegpt-icon_dark.svg" : "/html/icons/codegpt-icon.svg"));
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
