package ee.carlrobert.codegpt.toolwindow.conversations;

import static ee.carlrobert.codegpt.util.SwingUtils.justifyLeft;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.conversations.Conversation;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

class ConversationPanel extends JPanel {

  private final Conversation conversation;

  ConversationPanel(Conversation conversation, boolean isSelected) {
    this.conversation = conversation;
    addStyles(isSelected);

    var constraints = new GridBagConstraints();
    constraints.insets = JBUI.insets(0, 10);
    addChatIcon(constraints);
    addTextPanel(constraints);
  }

  private void addStyles(boolean isSelected) {
    setBackground(JBColor.background().darker());
    if (isSelected) {
      setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(4, 4, 4, 4, JBColor.green),
          JBUI.Borders.empty(10)));
    } else {
      setBorder(JBUI.Borders.empty(10));
    }
    setLayout(new GridBagLayout());
    setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  private void addChatIcon(GridBagConstraints constraints) {
    constraints.gridx = 0;
    constraints.gridy = 0;
    constraints.weightx = 0.0;
    constraints.fill = GridBagConstraints.NONE;
    add(new JLabel(AllIcons.Actions.Annotate), constraints);
  }

  private void addTextPanel(GridBagConstraints constraints) {
    constraints.gridx = 1;
    constraints.weightx = 1.0;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    add(createTextPanel(), constraints);
  }

  private JPanel createTextPanel() {
    var title = new JLabel(getFirstPrompt());
    title.setBorder(JBUI.Borders.emptyBottom(8));
    title.setFont(title.getFont().deriveFont(title.getFont().getStyle() | Font.BOLD));

    var textPanel = new JPanel();
    textPanel.setBackground(getBackground());
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
    textPanel.add(justifyLeft(title));

    var bottomPanel = new JPanel();
    bottomPanel.setBackground(getBackground());
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
    bottomPanel.add(new JLabel(conversation.getUpdatedOn()
        .format(DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a"))));
    bottomPanel.add(Box.createHorizontalGlue());
    if (conversation.getModel() != null) {
      bottomPanel.add(new JLabel(conversation.getModel()));
    }
    textPanel.add(bottomPanel);
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
