package ee.carlrobert.codegpt.ide.toolwindow.conversations;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.ui.components.JBScrollPane;
import ee.carlrobert.codegpt.client.ClientCode;
import ee.carlrobert.codegpt.ide.conversations.Conversation;
import ee.carlrobert.codegpt.ide.conversations.ConversationsState;
import ee.carlrobert.codegpt.ide.settings.SettingsState;
import ee.carlrobert.codegpt.ide.toolwindow.ContentManagerService;
import ee.carlrobert.codegpt.ide.toolwindow.ToolWindowService;
import java.util.Comparator;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.jetbrains.annotations.NotNull;

public class ConversationsToolWindow {

  private final Project project;
  private JPanel conversationsToolWindowContent;
  private JScrollPane scrollPane;
  private ScrollablePanel scrollablePanel;

  public ConversationsToolWindow(@NotNull Project project) {
    this.project = project;
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
      changeSettings(conversation);
      ContentManagerService.getInstance(project).displayChatTab();
      project.getService(ToolWindowService.class)
          .getChatToolWindow()
          .displayConversation(conversation);
    });

    var currentConversation = ConversationsState.getCurrentConversation();
    var isSelected = currentConversation != null && currentConversation.getId().equals(conversation.getId());
    mainPanel.setBackground(conversationsToolWindowContent.getBackground());
    mainPanel.add(new ConversationPanel(conversation, isSelected));
    scrollablePanel.add(mainPanel);
  }

  private void changeSettings(Conversation conversation) {
    var settings = SettingsState.getInstance();
    var isUnofficialClient = ClientCode.UNOFFICIAL_CHATGPT.equals(conversation.getClientCode());
    settings.isChatGPTOptionSelected = isUnofficialClient;
    settings.isGPTOptionSelected = !isUnofficialClient;

    if (!isUnofficialClient) {
      var isChatCompletions = ClientCode.CHAT_COMPLETIONS.equals(conversation.getClientCode());
      if (isChatCompletions) {
        settings.chatCompletionBaseModel = conversation.getModel();
      } else {
        settings.textCompletionBaseModel = conversation.getModel();
      }
      settings.isChatCompletionOptionSelected = isChatCompletions;
      settings.isTextCompletionOptionSelected = !isChatCompletions;
    }
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
