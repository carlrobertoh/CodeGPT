package ee.carlrobert.codegpt.toolwindow.chat.standard;

import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;
import static java.lang.String.format;
import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.components.ResponsePanel;
import ee.carlrobert.codegpt.util.SwingUtils;
import java.awt.event.MouseEvent;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;

class StandardChatToolWindowLandingPanel extends ResponsePanel {

  private static final Logger LOG = Logger.getInstance(StandardChatToolWindowLandingPanel.class);

  private final EditorActionEvent onAction;

  StandardChatToolWindowLandingPanel(EditorActionEvent onAction) {
    this.onAction = onAction;
    addContent(createContent());
  }

  private JTextPane createContent() {
    var textPane = SwingUtils.createTextPane(
        "<html>"
            + format(
                "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
                    + "Welcome <strong>%s</strong>, I'm your intelligent code companion, here to be"
                    + " your partner-in-crime for getting things done in a flash. Together, we'll "
                    + "tackle tasks swiftly and efficiently, making your coding experience a joy."
                    + "</p>",
                SettingsState.getInstance().getDisplayName())
            + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
            + "Feel free to ask me anything you'd like, but my true superpower lies in assisting "
            + "you with your code! Here are a few examples of how I can assist you:"
            + "</p>"

            + "<ul style=\"margin-top: 4px; margin-bottom: 4px;\">"
            + "<li>"
            + "<a href=\"GENERATE_UNIT_TESTS\">Generate unit tests for the selected code</a>"
            + "</li>"
            + "<li>"
            + "<a href=\"EXPLAIN_CODE\">Explain the selected code</a>"
            + "</li>"
            + "<li>"
            + "<a href=\"FIND_BUGS\">Find bugs in the selected code</a>"
            + "</li>"
            + "</ul"

            + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
            + "Being an AI-powered assistant, I may occasionally have surprises or make mistakes. "
            + "Therefore, it's wise to double-check any code or suggestions I provide."
            + "</p>"
            + "</html>",
        this::handleHyperlinkClicked);
    textPane.setBackground(getPanelBackgroundColor());
    return textPane;
  }

  private void handleHyperlinkClicked(HyperlinkEvent event) {
    if (ACTIVATED.equals(event.getEventType())) {
      if (event.getURL() == null) {
        var mouseLocation = ((MouseEvent) event.getInputEvent()).getLocationOnScreen();
        mouseLocation.y = mouseLocation.y - 10;
        switch (event.getDescription()) {
          case "GENERATE_UNIT_TESTS":
            onAction.handleAction(EditorAction.WRITE_TESTS, mouseLocation);
            break;
          case "EXPLAIN_CODE":
            onAction.handleAction(EditorAction.EXPLAIN, mouseLocation);
            break;
          case "FIND_BUGS":
            onAction.handleAction(EditorAction.FIND_BUGS, mouseLocation);
            break;
          default:
            LOG.error("Could not trigger action {}", event.getDescription());
        }
      } else {
        SwingUtils.handleHyperlinkClicked(event);
      }
    }
  }
}
