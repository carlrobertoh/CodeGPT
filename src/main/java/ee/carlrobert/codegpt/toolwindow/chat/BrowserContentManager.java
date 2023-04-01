package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.util.ui.UIUtil;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
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
  }

  // language=javascript
  public void displayResponse(UUID responseId, boolean animate) {
    executeIIFE(
        "const wrapper = document.createElement('div');" +
        "wrapper.classList.add('response');" +

        "const iconLabelContainer = document.createElement('p');" +

        "const img = new Image();" +
        "img.src = 'data:image/svg+xml;base64," + getBase64CodeGPTIcon() + "';" +
        "const svgString = atob('" + getBase64CodeGPTIcon() + "');" +
        "const svgDoc = new DOMParser().parseFromString(svgString, 'image/svg+xml');" +
        "const svg = svgDoc.documentElement;" +
        "svg.classList.add('svg-icon');" +
        (animate ? "svg.style.animation = 'roll 2.4s infinite linear';" : "") +
        "iconLabelContainer.appendChild(svg);" +

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
        "const icons = Array.from(document.getElementsByClassName('svg-icon'));" +
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
        "var svg = response.parentElement.getElementsByClassName('svg-icon')[0];" +
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
    return normalizeHtml(html);
  }

  private String normalizeHtml(String text) {
    return text.replace("'", "\\'")
        .replace("\n", "\\n")
        .replace("\"", "\\\"");
  }

  private String getBase64CodeGPTIcon() {
    if (UIUtil.isUnderDarcula()) {
      return "PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2aWV3Qm94PSIwLDAsMjU2LDI1NiIgd2lkdGg9IjIwcHgiIGhlaWdodD0iMjBweCIKICAgIGZpbGwtcnVsZT0ibm9uemVybyI+CiAgICA8ZGVmcz4KICAgICAgICA8bGluZWFyR3JhZGllbnQgeDE9IjMyIiB5MT0iNyIgeDI9IjMyIiB5Mj0iNTgiIGdyYWRpZW50VW5pdHM9InVzZXJTcGFjZU9uVXNlIiBpZD0iY29sb3ItMSI+CiAgICAgICAgICAgIDxzdG9wIG9mZnNldD0iMCIgc3RvcC1jb2xvcj0iI2ZmZmZmZiI+PC9zdG9wPgogICAgICAgICAgICA8c3RvcCBvZmZzZXQ9IjAuNjk5IiBzdG9wLWNvbG9yPSIjZmZmZmZmIj48L3N0b3A+CiAgICAgICAgPC9saW5lYXJHcmFkaWVudD4KICAgICAgICA8bGluZWFyR3JhZGllbnQgeDE9IjMyIiB5MT0iMC44NzIiIHgyPSIzMiIgeTI9IjYyLjY3OSIgZ3JhZGllbnRVbml0cz0idXNlclNwYWNlT25Vc2UiIGlkPSJjb2xvci0yIj4KICAgICAgICAgICAgPHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjMDAwMDAwIj48L3N0b3A+CiAgICAgICAgICAgIDxzdG9wIG9mZnNldD0iMSIgc3RvcC1jb2xvcj0iIzAwMDAwMCI+PC9zdG9wPgogICAgICAgIDwvbGluZWFyR3JhZGllbnQ+CiAgICA8L2RlZnM+CiAgICA8ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMTYsLTE2KSBzY2FsZSgxLjEyNSwxLjEyNSkiPgogICAgICAgIDxnIGZpbGw9IiNmZmZmZmYiIGZpbGwtcnVsZT0ibm9uemVybyIgc3Ryb2tlPSIjZmZmZmZmIiBzdHJva2Utd2lkdGg9IjQiIHN0cm9rZS1saW5lY2FwPSJidXR0IiBzdHJva2UtbGluZWpvaW49InJvdW5kIiBzdHJva2UtbWl0ZXJsaW1pdD0iMTAiCiAgICAgICAgICAgIHN0cm9rZS1kYXNoYXJyYXk9IiIgc3Ryb2tlLWRhc2hvZmZzZXQ9IjAiIGZvbnQtZmFtaWx5PSJub25lIiBmb250LXdlaWdodD0ibm9uZSIgZm9udC1zaXplPSJub25lIiB0ZXh0LWFuY2hvcj0ibm9uZSIKICAgICAgICAgICAgc3R5bGU9Im1peC1ibGVuZC1tb2RlOiBub3JtYWwiPgogICAgICAgICAgICA8cGF0aCB0cmFuc2Zvcm09InNjYWxlKDQsNCkiCiAgICAgICAgICAgICAgICBkPSJNNTYuOTYsMzUuNzdjMCwzLjI2Njc3IC0xLjI0Nzk4LDYuMjM1MDYgLTMuMzAyNzYsOC40NDkzMmMtMS45OTQyOSwyLjE1MTY0IC00Ljc1MDcyLDMuNTkxMTkgLTcuODY3NTcsMy45MDIyMmMtMC44ODIwOCw0LjE3OTkgLTMuODU4MDIsNy41NTAxMiAtNy43MDY2Nyw5LjA0NDE0Yy0xLjM4OTE3LDAuNTM5NDQgLTIuODkxOCwwLjgzNDMyIC00LjQ1MDAxLDAuODM0MzJjLTAuMDAwNSwwIC0wLjAwMTAxLDAgLTAuMDAxNTEsMGMtMC4wMDA1LDAgLTAuMDAwOTksMCAtMC4wMDE0OSwwYy0zLjcsMCAtNy4xMywtMS42MSAtOS41MSwtNC40NGMtMC4xNzM1NSwwLjAyMDgzIC0wLjMzOTg4LDAuMDM2ODMgLTAuNTAxNDgsMC4wNDg4NmMtMC4zMDUxMywwLjAyNDY1IC0wLjU5MTAzLDAuMDM2MTQgLTAuODczNTIsMC4wMzYxNGMtMy42NzIxOCwwIC02Ljk3NzczLC0xLjYwMDUzIC05LjI1NTksLTQuMTQwMzljLTEuOTc1NDcsLTIuMjAwNzYgLTMuMTc5MSwtNS4xMDg1NiAtMy4xNzkxLC04LjI5NDYxYzAsLTEuMzU5OTIgMC4yMTk5NywtMi42OTk4NCAwLjY1OTkyLC0zLjk5OTc3Yy0wLjE0NDAzLC0wLjEzNTUgLTAuMjg0NDksLTAuMjc0MDIgLTAuNDIxMywtMC40MTU0MmMtMi4yMzg2NSwtMi4zMTIyIC0zLjUwODYzLC01LjQwMzU3IC0zLjUwODYzLC04LjY1NDgxYzAsLTYuMDkgNC4zOSwtMTEuMjQgMTAuMzIsLTEyLjI1YzEuMjI3OTksLTQuMTQ4MzkgNC40Njk4LC03LjI3NDM4IDguNTAxMTUsLTguNDE3MDdjMS4wODkxNywtMC4zMDg5NCAyLjIzNjE0LC0wLjQ3MjkzIDMuNDE2ODUsLTAuNDcyOTNjMC4wMDAzMywwIDAuMDAwNjcsMCAwLjAwMSwwYzAuMDAwMzMsMCAwLjAwMDY2LDAgMC4wMDEsMGMwLjI5ODMzLDAgMC41OTQ3OSwwLjAxMDQgMC44ODg5OSwwLjAzMWMyLjI5NDA1LDAuMTYwMzEgNC40NDcwNywwLjkzODI5IDYuMjc0MTMsMi4yMzk3NGMwLjkxMDYzLDAuNjQ3NTcgMS43NDA0NCwxLjQyNDM5IDIuNDY2MiwyLjMxODQxYzAuMDU2NTksLTAuMDExMDYgMC4xMTMxNCwtMC4wMjE3MyAwLjE2OTYzLC0wLjAzMjAxYzAuMjQ2ODgsLTAuMDQ2MDMgMC40OTM2MywtMC4wODM0MSAwLjczOTYzLC0wLjExMjc0YzAuNDg1MDYsLTAuMDU5NDUgMC45NjQ3MywtMC4wODk0MSAxLjQzNjQzLC0wLjA4OTQxYzAuMTE4NzYsMCAwLjIzNzE1LDAuMDAxNjcgMC4zNTUxMiwwLjAwNWMzLjUyOTgsMC4wOTk1MSA2LjY5NjI5LDEuNjc3NjcgOC45MDA3OCw0LjEzNTM5YzEuOTc1NDcsMi4yMDA3NiAzLjE3OTEsNS4xMDg1NiAzLjE3OTEsOC4yOTQ2MWMwLDAuNjc4NjEgLTAuMDU3MzgsMS4zNTcyMyAtMC4xNzIxNCwyLjAzMzIyYy0wLjA2MzcxLDAuMzc4ODQgLTAuMTQ1MDgsMC43NTY0IC0wLjI0Mzg2LDEuMTMxNzhjMS4xMjkxOCwxLjEyMTAxIDIuMDIzODUsMi40Mzc2NyAyLjY0ODMsMy44NzAwM2MwLjY3NjQsMS41NDUyNCAxLjAzNzcsMy4yMjYyMyAxLjAzNzcsNC45NDQ5N3oiCiAgICAgICAgICAgICAgICBpZD0ic3Ryb2tlTWFpblNWRyI+PC9wYXRoPgogICAgICAgIDwvZz4KICAgICAgICA8ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9Im5vbnplcm8iIHN0cm9rZT0ibm9uZSIgc3Ryb2tlLXdpZHRoPSIxIiBzdHJva2UtbGluZWNhcD0iYnV0dCIgc3Ryb2tlLWxpbmVqb2luPSJtaXRlciIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIgogICAgICAgICAgICBzdHJva2UtZGFzaGFycmF5PSIiIHN0cm9rZS1kYXNob2Zmc2V0PSIwIiBmb250LWZhbWlseT0ibm9uZSIgZm9udC13ZWlnaHQ9Im5vbmUiIGZvbnQtc2l6ZT0ibm9uZSIgdGV4dC1hbmNob3I9Im5vbmUiCiAgICAgICAgICAgIHN0eWxlPSJtaXgtYmxlbmQtbW9kZTogbm9ybWFsIj4KICAgICAgICAgICAgPGcgdHJhbnNmb3JtPSJzY2FsZSg0LDQpIj4KICAgICAgICAgICAgICAgIDxwYXRoCiAgICAgICAgICAgICAgICAgICAgZD0iTTUzLjI3LDI2Ljk2YzAuMjgsLTEuMDUgMC40MiwtMi4xMSAwLjQyLC0zLjE3YzAsLTYuODYgLTUuNTgsLTEyLjQzIC0xMi40MywtMTIuNDNjLTAuNzcsMCAtMS41NiwwLjA3IC0yLjM1LDAuMjNjLTIuMzcsLTIuOTIgLTUuODUsLTQuNTkgLTkuNjMsLTQuNTljLTUuNTUsMCAtMTAuMzYsMy42MiAtMTEuOTIsOC44OWMtNS45MywxLjAxIC0xMC4zMiw2LjE2IC0xMC4zMiwxMi4yNWMwLDMuNDUgMS40Myw2LjcyIDMuOTMsOS4wN2MtMC40NCwxLjMgLTAuNjYsMi42NCAtMC42Niw0YzAsNi44NiA1LjU4LDEyLjQzIDEyLjQzLDEyLjQzYzAuNDQsMCAwLjg4LC0wLjAyIDEuMzgsLTAuMDhjMi4zOCwyLjgzIDUuODEsNC40NCA5LjUxLDQuNDRjNS44OCwwIDEwLjk2LC00LjE5IDEyLjE2LC05Ljg4YzYuMzIsLTAuNjMgMTEuMTcsLTUuOTEgMTEuMTcsLTEyLjM1YzAsLTMuMzEgLTEuMzQsLTYuNDggLTMuNjksLTguODF6TTM4LjI1LDM1Ljg4bC02LjYzLDRsLTYuNSwtNHYtNy4yNmw2LjYzLC0zLjg3bDYuNjMsMy43NXoiCiAgICAgICAgICAgICAgICAgICAgZmlsbD0idXJsKCNjb2xvci0xKSI+PC9wYXRoPgogICAgICAgICAgICAgICAgPHBhdGgKICAgICAgICAgICAgICAgICAgICBkPSJNNTMuMjc0LDI2Ljk1NWMwLjI3NSwtMS4wNDUgMC40MTUsLTIuMTA3IDAuNDE1LC0zLjE2NmMwLC02Ljg1NSAtNS41NzgsLTEyLjQzNCAtMTIuNDM0LC0xMi40MzRjLTAuNzY2LDAgLTEuNTUzLDAuMDc5IC0yLjM1LDAuMjM1Yy0yLjM2OSwtMi45MjUgLTUuODQzLC00LjU5IC05LjYyNywtNC41OWMtNS41NDksMCAtMTAuMzUzLDMuNjIyIC0xMS45MTMsOC44OTFjLTUuOTMsMS4wMTIgLTEwLjMyLDYuMTYzIC0xMC4zMiwxMi4yNTRjMCwzLjQ0OCAxLjQyNCw2LjcxNSAzLjkzLDkuMDdjLTAuNDQsMS4yOTkgLTAuNjY0LDIuNjQgLTAuNjY0LDMuOTk2YzAsNi44NTUgNS41NzgsMTIuNDM0IDEyLjQzNCwxMi40MzRjMC40MzMsMCAwLjg3NCwtMC4wMjcgMS4zOCwtMC4wODdjMi4zNzYsMi44MzEgNS44MDksNC40NDIgOS41MDgsNC40NDJjNS44NzUsMCAxMC45NiwtNC4xOTIgMTIuMTUyLC05Ljg3OGM2LjMyNywtMC42MjkgMTEuMTcsLTUuOTA4IDExLjE3LC0xMi4zNTVjMC4wMDEsLTMuMzA0IC0xLjMzMywtNi40ODEgLTMuNjgxLC04LjgxMnpNNTEuNjg5LDIzLjc4OWMwLDAuNjQ2IC0wLjA3LDEuMjkzIC0wLjE5MywxLjkzN2wtMTIuMjkzLC03LjE4NWwtMTMuMTQ2LDcuOTkxdi00LjkxbDEyLjgxNCwtNy45NzJjMC44MTMsLTAuMTkxIDEuNjE1LC0wLjI5NSAyLjM4MywtMC4yOTVjNS43NTQsMCAxMC40MzUsNC42ODEgMTAuNDM1LDEwLjQzNHpNMzcuMzk3LDM1LjE3MWwtNS41NjMsMy4zMTZsLTUuNzc2LC0zLjMwM3YtNi4zMTFsNS40NjUsLTMuMzIybC0wLjAzMSwwLjA1Mmw1LjkwNSwzLjQ4ek0yOS4yNzgsOWMyLjk5NywwIDUuNzU1LDEuMjUxIDcuNzI4LDMuNDU3bC0xMi45NDgsOC4wNTR2MTMuNTI5bC00Ljg5OCwtMi44MDF2LTE0LjMxN2MxLjE1OSwtNC42NjggNS4zMDIsLTcuOTIyIDEwLjExOCwtNy45MjJ6TTkuMDQ0LDI4LjE0NWMwLC00LjkyMyAzLjQxOSwtOS4xMDkgOC4xMTYsLTEwLjE2OXYxNC40MjRsMTIuNzAxLDcuMjY0bC01LjIyNywzLjExNWwtMTEuODk3LC02LjY3NGMtMi4zNDUsLTEuOTk4IC0zLjY5MywtNC44OTQgLTMuNjkzLC03Ljk2ek0xMi4zMTEsNDEuMjExYzAsLTAuOTU1IDAuMTM4LC0xLjkwMiAwLjQsLTIuODI4bDExLjk1NCw2LjcwNmwxMi43MzIsLTcuNTg4djYuMjdsLTEzLjE3Miw3Ljc1NGMtMC41NywwLjA3OCAtMS4wNDMsMC4xMiAtMS40OCwwLjEyYy01Ljc1MywwIC0xMC40MzQsLTQuNjgxIC0xMC40MzQsLTEwLjQzNHpNMzMuNjMzLDU2Yy0yLjg4NiwwIC01LjU3OCwtMS4xNzUgLTcuNTQ2LC0zLjI1MmwxMy4zMSwtNy44MzV2LTE0LjY1Mmw0LjUzOSwyLjY3NXYxNC4xNTRjLTAuNzQ0LDUuMDgzIC01LjE2Myw4LjkxIC0xMC4zMDMsOC45MXpNNDUuOTM2LDQ2LjA5MXYtMTQuMjk4bC02LjUzOSwtMy44NTN2LTAuMDY4aC0wLjExNWwtNS44NzksLTMuNDY1bDUuODIxLC0zLjUzOGwxMi4zMDksNy4xOTVjMi4xNzQsMS45ODEgMy40MjIsNC43ODIgMy40MjIsNy43MDNjMC4wMDEsNS4yODggLTMuODg1LDkuNjM5IC05LjAxOSwxMC4zMjR6IgogICAgICAgICAgICAgICAgICAgIGZpbGw9InVybCgjY29sb3ItMikiPjwvcGF0aD4KICAgICAgICAgICAgPC9nPgogICAgICAgIDwvZz4KICAgIDwvZz4KPC9zdmc+Cg==";
    }
    return "PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB2aWV3Qm94PSIwLDAsMjU2LDI1NiIgd2lkdGg9IjIwcHgiIGhlaWdodD0iMjBweCIgZmlsbC1ydWxlPSJub256ZXJvIj48ZGVmcz48bGluZWFyR3JhZGllbnQgeDE9IjMyIiB5MT0iNyIgeDI9IjMyIiB5Mj0iNTgiIGdyYWRpZW50VW5pdHM9InVzZXJTcGFjZU9uVXNlIiBpZD0iY29sb3ItMSI+PHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjMDAwMDAwIj48L3N0b3A+PHN0b3Agb2Zmc2V0PSIwLjY5OSIgc3RvcC1jb2xvcj0iIzAwMDAwMCI+PC9zdG9wPjwvbGluZWFyR3JhZGllbnQ+PGxpbmVhckdyYWRpZW50IHgxPSIzMiIgeTE9IjAuODcyIiB4Mj0iMzIiIHkyPSI2Mi42NzkiIGdyYWRpZW50VW5pdHM9InVzZXJTcGFjZU9uVXNlIiBpZD0iY29sb3ItMiI+PHN0b3Agb2Zmc2V0PSIwIiBzdG9wLWNvbG9yPSIjZmZmZmZmIj48L3N0b3A+PHN0b3Agb2Zmc2V0PSIxIiBzdG9wLWNvbG9yPSIjZmZmZmZmIj48L3N0b3A+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTE2LC0xNikgc2NhbGUoMS4xMjUsMS4xMjUpIj48ZyBmaWxsPSIjMDAwMDAwIiBmaWxsLXJ1bGU9Im5vbnplcm8iIHN0cm9rZT0iIzAwMDAwMCIgc3Ryb2tlLXdpZHRoPSI0IiBzdHJva2UtbGluZWNhcD0iYnV0dCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIgc3Ryb2tlLW1pdGVybGltaXQ9IjEwIiBzdHJva2UtZGFzaGFycmF5PSIiIHN0cm9rZS1kYXNob2Zmc2V0PSIwIiBmb250LWZhbWlseT0ibm9uZSIgZm9udC13ZWlnaHQ9Im5vbmUiIGZvbnQtc2l6ZT0ibm9uZSIgdGV4dC1hbmNob3I9Im5vbmUiIHN0eWxlPSJtaXgtYmxlbmQtbW9kZTogbm9ybWFsIj48cGF0aCB0cmFuc2Zvcm09InNjYWxlKDQsNCkiIGQ9Ik01Ni45NiwzNS43N2MwLDMuMjY2NzcgLTEuMjQ3OTgsNi4yMzUwNiAtMy4zMDI3Niw4LjQ0OTMyYy0xLjk5NDI5LDIuMTUxNjQgLTQuNzUwNzIsMy41OTExOSAtNy44Njc1NywzLjkwMjIyYy0wLjg4MjA4LDQuMTc5OSAtMy44NTgwMiw3LjU1MDEyIC03LjcwNjY3LDkuMDQ0MTRjLTEuMzg5MTcsMC41Mzk0NCAtMi44OTE4LDAuODM0MzIgLTQuNDUwMDEsMC44MzQzMmMtMC4wMDA1LDAgLTAuMDAxMDEsMCAtMC4wMDE1MSwwYy0wLjAwMDUsMCAtMC4wMDA5OSwwIC0wLjAwMTQ5LDBjLTMuNywwIC03LjEzLC0xLjYxIC05LjUxLC00LjQ0Yy0wLjE3MzU1LDAuMDIwODMgLTAuMzM5ODgsMC4wMzY4MyAtMC41MDE0OCwwLjA0ODg2Yy0wLjMwNTEzLDAuMDI0NjUgLTAuNTkxMDMsMC4wMzYxNCAtMC44NzM1MiwwLjAzNjE0Yy0zLjY3MjE4LDAgLTYuOTc3NzMsLTEuNjAwNTMgLTkuMjU1OSwtNC4xNDAzOWMtMS45NzU0NywtMi4yMDA3NiAtMy4xNzkxLC01LjEwODU2IC0zLjE3OTEsLTguMjk0NjFjMCwtMS4zNTk5MiAwLjIxOTk3LC0yLjY5OTg0IDAuNjU5OTIsLTMuOTk5NzdjLTAuMTQ0MDMsLTAuMTM1NSAtMC4yODQ0OSwtMC4yNzQwMiAtMC40MjEzLC0wLjQxNTQyYy0yLjIzODY1LC0yLjMxMjIgLTMuNTA4NjMsLTUuNDAzNTcgLTMuNTA4NjMsLTguNjU0ODFjMCwtNi4wOSA0LjM5LC0xMS4yNCAxMC4zMiwtMTIuMjVjMS4yMjc5OSwtNC4xNDgzOSA0LjQ2OTgsLTcuMjc0MzggOC41MDExNSwtOC40MTcwN2MxLjA4OTE3LC0wLjMwODk0IDIuMjM2MTQsLTAuNDcyOTMgMy40MTY4NSwtMC40NzI5M2MwLjAwMDMzLDAgMC4wMDA2NywwIDAuMDAxLDBjMC4wMDAzMywwIDAuMDAwNjYsMCAwLjAwMSwwYzAuMjk4MzMsMCAwLjU5NDc5LDAuMDEwNCAwLjg4ODk5LDAuMDMxYzIuMjk0MDUsMC4xNjAzMSA0LjQ0NzA3LDAuOTM4MjkgNi4yNzQxMywyLjIzOTc0YzAuOTEwNjMsMC42NDc1NyAxLjc0MDQ0LDEuNDI0MzkgMi40NjYyLDIuMzE4NDFjMC4wNTY1OSwtMC4wMTEwNiAwLjExMzE0LC0wLjAyMTczIDAuMTY5NjMsLTAuMDMyMDFjMC4yNDY4OCwtMC4wNDYwMyAwLjQ5MzYzLC0wLjA4MzQxIDAuNzM5NjMsLTAuMTEyNzRjMC40ODUwNiwtMC4wNTk0NSAwLjk2NDczLC0wLjA4OTQxIDEuNDM2NDMsLTAuMDg5NDFjMC4xMTg3NiwwIDAuMjM3MTUsMC4wMDE2NyAwLjM1NTEyLDAuMDA1YzMuNTI5OCwwLjA5OTUxIDYuNjk2MjksMS42Nzc2NyA4LjkwMDc4LDQuMTM1MzljMS45NzU0NywyLjIwMDc2IDMuMTc5MSw1LjEwODU2IDMuMTc5MSw4LjI5NDYxYzAsMC42Nzg2MSAtMC4wNTczOCwxLjM1NzIzIC0wLjE3MjE0LDIuMDMzMjJjLTAuMDYzNzEsMC4zNzg4NCAtMC4xNDUwOCwwLjc1NjQgLTAuMjQzODYsMS4xMzE3OGMxLjEyOTE4LDEuMTIxMDEgMi4wMjM4NSwyLjQzNzY3IDIuNjQ4MywzLjg3MDAzYzAuNjc2NCwxLjU0NTI0IDEuMDM3NywzLjIyNjIzIDEuMDM3Nyw0Ljk0NDk3eiIgaWQ9InN0cm9rZU1haW5TVkciPjwvcGF0aD48L2c+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJub256ZXJvIiBzdHJva2U9Im5vbmUiIHN0cm9rZS13aWR0aD0iMSIgc3Ryb2tlLWxpbmVjYXA9ImJ1dHQiIHN0cm9rZS1saW5lam9pbj0ibWl0ZXIiIHN0cm9rZS1taXRlcmxpbWl0PSIxMCIgc3Ryb2tlLWRhc2hhcnJheT0iIiBzdHJva2UtZGFzaG9mZnNldD0iMCIgZm9udC1mYW1pbHk9Im5vbmUiIGZvbnQtd2VpZ2h0PSJub25lIiBmb250LXNpemU9Im5vbmUiIHRleHQtYW5jaG9yPSJub25lIiBzdHlsZT0ibWl4LWJsZW5kLW1vZGU6IG5vcm1hbCI+PGcgdHJhbnNmb3JtPSJzY2FsZSg0LDQpIj48cGF0aCBkPSJNNTMuMjcsMjYuOTZjMC4yOCwtMS4wNSAwLjQyLC0yLjExIDAuNDIsLTMuMTdjMCwtNi44NiAtNS41OCwtMTIuNDMgLTEyLjQzLC0xMi40M2MtMC43NywwIC0xLjU2LDAuMDcgLTIuMzUsMC4yM2MtMi4zNywtMi45MiAtNS44NSwtNC41OSAtOS42MywtNC41OWMtNS41NSwwIC0xMC4zNiwzLjYyIC0xMS45Miw4Ljg5Yy01LjkzLDEuMDEgLTEwLjMyLDYuMTYgLTEwLjMyLDEyLjI1YzAsMy40NSAxLjQzLDYuNzIgMy45Myw5LjA3Yy0wLjQ0LDEuMyAtMC42NiwyLjY0IC0wLjY2LDRjMCw2Ljg2IDUuNTgsMTIuNDMgMTIuNDMsMTIuNDNjMC40NCwwIDAuODgsLTAuMDIgMS4zOCwtMC4wOGMyLjM4LDIuODMgNS44MSw0LjQ0IDkuNTEsNC40NGM1Ljg4LDAgMTAuOTYsLTQuMTkgMTIuMTYsLTkuODhjNi4zMiwtMC42MyAxMS4xNywtNS45MSAxMS4xNywtMTIuMzVjMCwtMy4zMSAtMS4zNCwtNi40OCAtMy42OSwtOC44MXpNMzguMjUsMzUuODhsLTYuNjMsNGwtNi41LC00di03LjI2bDYuNjMsLTMuODdsNi42MywzLjc1eiIgZmlsbD0idXJsKCNjb2xvci0xKSI+PC9wYXRoPjxwYXRoIGQ9Ik01My4yNzQsMjYuOTU1YzAuMjc1LC0xLjA0NSAwLjQxNSwtMi4xMDcgMC40MTUsLTMuMTY2YzAsLTYuODU1IC01LjU3OCwtMTIuNDM0IC0xMi40MzQsLTEyLjQzNGMtMC43NjYsMCAtMS41NTMsMC4wNzkgLTIuMzUsMC4yMzVjLTIuMzY5LC0yLjkyNSAtNS44NDMsLTQuNTkgLTkuNjI3LC00LjU5Yy01LjU0OSwwIC0xMC4zNTMsMy42MjIgLTExLjkxMyw4Ljg5MWMtNS45MywxLjAxMiAtMTAuMzIsNi4xNjMgLTEwLjMyLDEyLjI1NGMwLDMuNDQ4IDEuNDI0LDYuNzE1IDMuOTMsOS4wN2MtMC40NCwxLjI5OSAtMC42NjQsMi42NCAtMC42NjQsMy45OTZjMCw2Ljg1NSA1LjU3OCwxMi40MzQgMTIuNDM0LDEyLjQzNGMwLjQzMywwIDAuODc0LC0wLjAyNyAxLjM4LC0wLjA4N2MyLjM3NiwyLjgzMSA1LjgwOSw0LjQ0MiA5LjUwOCw0LjQ0MmM1Ljg3NSwwIDEwLjk2LC00LjE5MiAxMi4xNTIsLTkuODc4YzYuMzI3LC0wLjYyOSAxMS4xNywtNS45MDggMTEuMTcsLTEyLjM1NWMwLjAwMSwtMy4zMDQgLTEuMzMzLC02LjQ4MSAtMy42ODEsLTguODEyek01MS42ODksMjMuNzg5YzAsMC42NDYgLTAuMDcsMS4yOTMgLTAuMTkzLDEuOTM3bC0xMi4yOTMsLTcuMTg1bC0xMy4xNDYsNy45OTF2LTQuOTFsMTIuODE0LC03Ljk3MmMwLjgxMywtMC4xOTEgMS42MTUsLTAuMjk1IDIuMzgzLC0wLjI5NWM1Ljc1NCwwIDEwLjQzNSw0LjY4MSAxMC40MzUsMTAuNDM0ek0zNy4zOTcsMzUuMTcxbC01LjU2MywzLjMxNmwtNS43NzYsLTMuMzAzdi02LjMxMWw1LjQ2NSwtMy4zMjJsLTAuMDMxLDAuMDUybDUuOTA1LDMuNDh6TTI5LjI3OCw5YzIuOTk3LDAgNS43NTUsMS4yNTEgNy43MjgsMy40NTdsLTEyLjk0OCw4LjA1NHYxMy41MjlsLTQuODk4LC0yLjgwMXYtMTQuMzE3YzEuMTU5LC00LjY2OCA1LjMwMiwtNy45MjIgMTAuMTE4LC03LjkyMnpNOS4wNDQsMjguMTQ1YzAsLTQuOTIzIDMuNDE5LC05LjEwOSA4LjExNiwtMTAuMTY5djE0LjQyNGwxMi43MDEsNy4yNjRsLTUuMjI3LDMuMTE1bC0xMS44OTcsLTYuNjc0Yy0yLjM0NSwtMS45OTggLTMuNjkzLC00Ljg5NCAtMy42OTMsLTcuOTZ6TTEyLjMxMSw0MS4yMTFjMCwtMC45NTUgMC4xMzgsLTEuOTAyIDAuNCwtMi44MjhsMTEuOTU0LDYuNzA2bDEyLjczMiwtNy41ODh2Ni4yN2wtMTMuMTcyLDcuNzU0Yy0wLjU3LDAuMDc4IC0xLjA0MywwLjEyIC0xLjQ4LDAuMTJjLTUuNzUzLDAgLTEwLjQzNCwtNC42ODEgLTEwLjQzNCwtMTAuNDM0ek0zMy42MzMsNTZjLTIuODg2LDAgLTUuNTc4LC0xLjE3NSAtNy41NDYsLTMuMjUybDEzLjMxLC03LjgzNXYtMTQuNjUybDQuNTM5LDIuNjc1djE0LjE1NGMtMC43NDQsNS4wODMgLTUuMTYzLDguOTEgLTEwLjMwMyw4Ljkxek00NS45MzYsNDYuMDkxdi0xNC4yOThsLTYuNTM5LC0zLjg1M3YtMC4wNjhoLTAuMTE1bC01Ljg3OSwtMy40NjVsNS44MjEsLTMuNTM4bDEyLjMwOSw3LjE5NWMyLjE3NCwxLjk4MSAzLjQyMiw0Ljc4MiAzLjQyMiw3LjcwM2MwLjAwMSw1LjI4OCAtMy44ODUsOS42MzkgLTkuMDE5LDEwLjMyNHoiIGZpbGw9InVybCgjY29sb3ItMikiPjwvcGF0aD48L2c+PC9nPjwvZz48L3N2Zz4K";
  }
}
