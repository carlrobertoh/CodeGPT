package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.ui.components.JBTabbedPane;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ChatTabbedPane extends JBTabbedPane {

  private final Map<Integer, ChatToolWindowPanel> activeTabMapping = new HashMap<>();

  public ChatTabbedPane() {
    setTabComponentInsets(null);
  }

  public void addTab(String title, ChatToolWindowPanel toolWindowPanel) {
    super.addTab(title, toolWindowPanel.getContent());
    activeTabMapping.put(getTabCount() - 1, toolWindowPanel);
  }

  public Optional<Integer> tryFindActiveConversationIndex(UUID conversationId) {
    return activeTabMapping.entrySet().stream()
        .filter(entry -> conversationId.equals(entry.getValue().getConversationId()))
        .findFirst()
        .map(Map.Entry::getKey);
  }
}
