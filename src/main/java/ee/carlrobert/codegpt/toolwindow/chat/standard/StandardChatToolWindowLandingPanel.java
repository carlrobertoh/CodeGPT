package ee.carlrobert.codegpt.toolwindow.chat.standard;

import static java.lang.String.format;

import com.intellij.ui.components.ActionLink;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.settings.state.GeneralSettings;
import ee.carlrobert.codegpt.toolwindow.chat.ui.ResponsePanel;
import ee.carlrobert.codegpt.ui.UIUtil;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

class StandardChatToolWindowLandingPanel extends ResponsePanel {

  StandardChatToolWindowLandingPanel(EditorActionEvent onAction) {
    addContent(createContent(onAction));
  }

  private ActionLink createEditorActionLink(EditorAction action, EditorActionEvent onAction) {
    var link = new ActionLink(action.getUserMessage(), event -> {
      onAction.handleAction(action, ((ActionLink) event.getSource()).getLocationOnScreen());
    });
    link.setIcon(Icons.Sparkle);
    return link;
  }

  private JPanel createContent(EditorActionEvent onAction) {
    var panel = new JPanel(new BorderLayout());
    panel.add(UIUtil.createTextPane(
        "<html>"
            + format(
            "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
                + "Welcome <strong>%s</strong>, I'm your intelligent code companion, here to be"
                + " your partner-in-crime for getting things done in a flash."
                + "</p>", GeneralSettings.getCurrentState().getDisplayName())
            + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
            + "Feel free to ask me anything you'd like, but my true superpower lies in assisting "
            + "you with your code! Here are a few examples of how I can assist you:"
            + "</p>"
            + "</html>",
        false), BorderLayout.NORTH);
    panel.add(createEditorActionsListPanel(onAction), BorderLayout.CENTER);
    panel.add(UIUtil.createTextPane(
        "<html>"
            + "<p style=\"margin-top: 4px; margin-bottom: 4px;\">"
            + "Being an AI-powered assistant, I may occasionally have surprises or make mistakes. "
            + "Therefore, it's wise to double-check any code or suggestions I provide."
            + "</p>"
            + "</html>",
        false), BorderLayout.SOUTH);
    return panel;
  }

  private JPanel createEditorActionsListPanel(EditorActionEvent onAction) {
    var listPanel = new JPanel();
    listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
    listPanel.setBorder(JBUI.Borders.emptyLeft(4));
    listPanel.add(Box.createVerticalStrut(4));
    listPanel.add(createEditorActionLink(EditorAction.WRITE_TESTS, onAction));
    listPanel.add(Box.createVerticalStrut(4));
    listPanel.add(createEditorActionLink(EditorAction.EXPLAIN, onAction));
    listPanel.add(Box.createVerticalStrut(4));
    listPanel.add(createEditorActionLink(EditorAction.FIND_BUGS, onAction));
    listPanel.add(Box.createVerticalStrut(4));
    return listPanel;
  }
}
