package ee.carlrobert.codegpt.toolwindow.conversations;

import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.toolwindow.ModelIconLabel;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

class ConversationPanel extends JPanel {

  ConversationPanel(@NotNull Project project, @NotNull Conversation conversation) {
    super(new BorderLayout());
    setBorder(JBUI.Borders.empty(10, 20));
    setBackground(JBColor.background());
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        SettingsState.getInstance().sync(conversation);
        StandardChatToolWindowContentManager.getInstance(project).displayConversation(conversation);
      }
    });
    addStyles(isSelected(conversation));
    addTextPanel(conversation);
    setCursor(new Cursor(Cursor.HAND_CURSOR));
  }

  private boolean isSelected(Conversation conversation) {
    var currentConversation = ConversationsState.getCurrentConversation();
    return currentConversation != null && currentConversation.getId().equals(conversation.getId());
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

  private void addTextPanel(Conversation conversation) {
    var constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.weightx = 1.0;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    add(createTextPanel(conversation), constraints);
  }

  private JPanel createTextPanel(Conversation conversation) {
    var title = new JBLabel(getFirstPrompt(conversation))
        .withBorder(JBUI.Borders.emptyBottom(12))
        .withFont(JBFont.label().asBold());

    var bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setBackground(getPanelBackgroundColor());
    bottomPanel.add(new JLabel(conversation.getUpdatedOn()
        .format(DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss a"))), BorderLayout.WEST);
    if (conversation.getModel() != null) {
      bottomPanel.add(new ModelIconLabel(conversation.getClientCode(), conversation.getModel()), BorderLayout.EAST);
    }

    var textPanel = new JPanel(new BorderLayout());
    textPanel.setBackground(getPanelBackgroundColor());
    textPanel.add(title, BorderLayout.NORTH);
    textPanel.add(bottomPanel, BorderLayout.SOUTH);
    return textPanel;
  }

  private String getFirstPrompt(Conversation conversation) {
    var messages = conversation.getMessages();
    if (messages.isEmpty()) {
      return "";
    }
    return messages.get(0).getPrompt();
  }
}
