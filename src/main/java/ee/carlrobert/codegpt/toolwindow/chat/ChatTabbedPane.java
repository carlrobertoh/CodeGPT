package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.ui.components.JBTabbedPane;
import ee.carlrobert.codegpt.state.conversations.Conversation;
import ee.carlrobert.codegpt.state.conversations.ConversationsState;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import javax.swing.JPanel;

public class ChatTabbedPane extends JBTabbedPane {

  private final Map<String, ToolWindowTabPanel> activeTabMapping = new TreeMap<>((o1, o2) -> {
    int n1 = Integer.parseInt(o1.replaceAll("\\D", ""));
    int n2 = Integer.parseInt(o2.replaceAll("\\D", ""));
    return Integer.compare(n1, n2);
  });

  public ChatTabbedPane() {
    setTabComponentInsets(null);
    addChangeListener(e -> tryFindCurrentlyActiveConversation()
        .ifPresent(conversation -> ConversationsState.getInstance().setCurrentConversation(conversation)));
  }

  public void addNewTab(ToolWindowTabPanel toolWindowPanel) {
    var tabIndices = activeTabMapping.keySet().toArray(new String[0]);
    var nextIndex = 0;
    for (String title : tabIndices) {
      int tabNum = Integer.parseInt(title.replaceAll("\\D+", ""));
      if ((tabNum - 1) == nextIndex) {
        nextIndex++;
      } else {
        break;
      }
    }

    var title = "Chat " + (nextIndex + 1);
    super.insertTab(title, null, toolWindowPanel.getContent(), null, nextIndex);
    activeTabMapping.put(title, toolWindowPanel);
    super.setSelectedIndex(nextIndex);

    if (nextIndex > 0) {
      setTabComponentAt(nextIndex, createCloseableTabButtonPanel(title));
    }
  }

  public Optional<String> tryFindActiveConversationTitle(UUID conversationId) {
    return activeTabMapping.entrySet().stream()
        .filter(entry -> {
          var panelConversation = entry.getValue().getConversation();
          return panelConversation != null && conversationId.equals(panelConversation.getId());
        })
        .findFirst()
        .map(Map.Entry::getKey);
  }

  public Optional<Conversation> tryFindCurrentlyActiveConversation() {
    var selectedIndex = getSelectedIndex();
    if (selectedIndex != -1) {
      var toolWindowPanel = activeTabMapping.get(getTitleAt(selectedIndex));
      if (toolWindowPanel != null && toolWindowPanel.getConversation() != null) {
        return ConversationsState.getInstance().getConversation(toolWindowPanel.getConversation().getId());
      }
    }
    return Optional.empty();
  }

  private JPanel createCloseableTabButtonPanel(String title) {
    var button = new CloseableTabButton(title);
    button.addActionListener(new CloseActionListener(title));
    return button.getComponent();
  }

  class CloseActionListener implements ActionListener {

    private final String title;

    public CloseActionListener(String title) {
      this.title = title;
    }

    public void actionPerformed(ActionEvent evt) {
      var tabIndex = indexOfTab(title);
      if (tabIndex >= 0) {
        removeTabAt(tabIndex);
        activeTabMapping.remove(title);
      }
    }
  }

  public void clearAll() {
    removeAll();
    activeTabMapping.clear();
  }
}
