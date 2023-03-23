package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.ui.components.JBTabbedPane;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChatTabbedPane extends JBTabbedPane {

  private final Map<Integer, ChatToolWindowTabPanel> activeTabMapping = new HashMap<>();

  public ChatTabbedPane() {
    setTabComponentInsets(null);
    addChangeListener(e -> tryFindCurrentlyActiveConversation()
        .ifPresent(conversation -> ConversationsState.getInstance().setCurrentConversation(conversation)));
  }

  public void addNewTab(ChatToolWindowTabPanel toolWindowPanel) {
    super.addTab("Chat " + (getTabCount() + 1), toolWindowPanel.getContent());
    var tabIndex = getTabCount() - 1;
    super.setSelectedIndex(tabIndex);
    activeTabMapping.put(tabIndex, toolWindowPanel);
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
}
