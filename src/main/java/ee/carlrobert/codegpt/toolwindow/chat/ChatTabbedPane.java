package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.ui.components.JBTabbedPane;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.swing.JPanel;

public class ChatTabbedPane extends JBTabbedPane {

  private final Map<Integer, ChatToolWindowTabPanel> activeTabMapping = new HashMap<>();

  public ChatTabbedPane() {
    setTabComponentInsets(null);
    addChangeListener(e -> tryFindCurrentlyActiveConversation()
        .ifPresent(conversation -> ConversationsState.getInstance().setCurrentConversation(conversation)));
  }

  public void addNewTab(ChatToolWindowTabPanel toolWindowPanel) {
    var tabIndices = activeTabMapping.keySet().toArray(new Integer[0]);
    var nextIndex = 0;
    for (Integer val : tabIndices) {
      if (val == nextIndex) {
        nextIndex++;
      } else {
        break;
      }
    }

    var title = "Chat " + (nextIndex + 1);
    super.insertTab(title, null, toolWindowPanel.getContent(), null, nextIndex);
    activeTabMapping.put(nextIndex, toolWindowPanel);
    super.setSelectedIndex(nextIndex);

    if (nextIndex > 0) {
      setTabComponentAt(nextIndex, createCloseableTabButtonPanel(title, nextIndex));
    }
  }

  public Optional<Integer> tryFindActiveConversationIndex(UUID conversationId) {
    return activeTabMapping.entrySet().stream()
        .filter(entry -> conversationId.equals(entry.getValue().getConversationId()))
        .findFirst()
        .map(Map.Entry::getKey);
  }

  public Optional<Conversation> tryFindCurrentlyActiveConversation() {
    var toolWindowPanel = activeTabMapping.get(getSelectedIndex());
    if (toolWindowPanel == null || toolWindowPanel.getConversationId() == null) {
      return Optional.empty();
    }
    return ConversationsState.getInstance().getConversation(toolWindowPanel.getConversationId());
  }

  private JPanel createCloseableTabButtonPanel(String title, int tabIndex) {
    var button = new CloseableTabButton(title);
    button.addActionListener(new CloseActionListener(title, tabIndex));
    return button.getComponent();
  }

  class CloseActionListener implements ActionListener {

    private final String title;
    private final int tabMappingIndex;

    public CloseActionListener(String title, int tabMappingIndex) {
      this.title = title;
      this.tabMappingIndex = tabMappingIndex;
    }

    public void actionPerformed(ActionEvent evt) {
      var tabIndex = indexOfTab(title);
      if (tabIndex >= 0) {
        removeTabAt(tabIndex);
        activeTabMapping.remove(tabMappingIndex);
      }
    }
  }

  public void clearAll() {
    removeAll();
    activeTabMapping.clear();
  }
}
