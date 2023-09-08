package ee.carlrobert.codegpt.toolwindow.conversations;

import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.toolwindow.ModelIconLabel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.format.DateTimeFormatter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

class ConversationPanel extends JPanel {

  private final Conversation conversation;

  ConversationPanel(Conversation conversation, boolean isSelected) {
    this.conversation = conversation;
    addStyles(isSelected);

    addTextPanel();
    setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  private void addStyles(boolean isSelected) {
    var border = isSelected ?
        JBUI.Borders.customLine(JBUI.CurrentTheme.ActionButton.focusedBorder(), 2, 2, 2, 2) :
        JBUI.Borders.customLine(JBColor.border(), 1, 1, 1, 1);
    setBackground(getPanelBackgroundColor());
    setBorder(JBUI.Borders.compound(border, JBUI.Borders.empty(8)));
    setLayout(new GridBagLayout());
    setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  private void addTextPanel() {
    var constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.weightx = 1.0;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    add(createTextPanel(), constraints);
  }

  private JPanel createTextPanel() {
    var title = new JBLabel(getFirstPrompt())
        .withBorder(JBUI.Borders.emptyBottom(12))
        .withFont(JBFont.label().asBold());

    var bottomPanel = new JPanel();
    bottomPanel.setBackground(getPanelBackgroundColor());
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
    bottomPanel.add(new JLabel(conversation.getUpdatedOn()
        .format(DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a"))));
    bottomPanel.add(Box.createHorizontalGlue());
    if (conversation.getModel() != null) {
      bottomPanel.add(new ModelIconLabel(conversation.getClientCode(), conversation.getModel()));
    }

    var textPanel = new JPanel(new BorderLayout());
    textPanel.setBackground(getPanelBackgroundColor());
    textPanel.add(title, BorderLayout.NORTH);
    textPanel.add(bottomPanel, BorderLayout.SOUTH);
    return textPanel;
  }

  private String getFirstPrompt() {
    var messages = conversation.getMessages();
    var prompt = "";
    if (!messages.isEmpty()) {
      prompt = conversation.getMessages().get(0).getPrompt();
    }
    return prompt;
  }
}
