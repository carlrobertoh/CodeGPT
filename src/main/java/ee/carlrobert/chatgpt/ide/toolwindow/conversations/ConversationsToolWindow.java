package ee.carlrobert.chatgpt.ide.toolwindow.conversations;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import ee.carlrobert.chatgpt.ide.conversations.Conversation;
import ee.carlrobert.chatgpt.ide.conversations.ConversationsState;
import ee.carlrobert.chatgpt.ide.toolwindow.ToolWindowService;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.jetbrains.annotations.NotNull;

public class ConversationsToolWindow {

  private final Project project;
  private final ToolWindow toolWindow;
  private JPanel conversationsToolWindowContent;
  private JScrollPane scrollPane;
  private ScrollablePanel scrollablePanel;

  public ConversationsToolWindow(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    this.project = project;
    this.toolWindow = toolWindow;
    refresh();
  }

  public JPanel getContent() {
    return conversationsToolWindowContent;
  }

  public void refresh() {
    scrollablePanel.removeAll();
    ConversationsState.getInstance()
        .conversationsContainer
        .getConversationsMapping()
        .forEach((key, value) -> value.stream()
            .sorted(Comparator.comparing(Conversation::getUpdatedOn).reversed())
            .forEach(this::addContent));
  }

  private void addContent(Conversation conversation) {
    var mainPanel = new RootConversationPanel(() -> {
      ConversationsState.getInstance().setCurrentConversation(conversation);

      var toolWindowService = project.getService(ToolWindowService.class);
      var contentManager = toolWindow.getContentManager();
      Arrays.stream(contentManager.getContents())
          .filter(content -> "Chat".equals(content.getTabName()))
          .findFirst()
          .ifPresentOrElse(
              contentManager::setSelectedContent,
              () -> contentManager.setSelectedContent(Objects.requireNonNull(contentManager.getContent(0)))
          );
      toolWindowService.displayConversation(conversation);
    });

    var currentConversation = ConversationsState.getCurrentConversation();
    var isSelected = currentConversation != null && currentConversation.getId().equals(conversation.getId());
    mainPanel.setBackground(conversationsToolWindowContent.getBackground());
    mainPanel.add(new ConversationPanel(conversation, isSelected));
    scrollablePanel.add(mainPanel);
  }

  private void createUIComponents() {
    scrollablePanel = new ScrollablePanel();
    scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));

    scrollPane = new JBScrollPane();
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewportView(scrollablePanel);
    scrollPane.setBorder(null);
    scrollPane.setViewportBorder(null);
  }
}
