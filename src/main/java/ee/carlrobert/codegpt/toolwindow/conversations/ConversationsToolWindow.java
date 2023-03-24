package ee.carlrobert.codegpt.toolwindow.conversations;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatContentManagerService;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowTabPanel;
import ee.carlrobert.codegpt.toolwindow.conversations.actions.ClearAllConversationsAction;
import ee.carlrobert.codegpt.toolwindow.conversations.actions.DeleteConversationAction;
import ee.carlrobert.codegpt.toolwindow.conversations.actions.MoveDownAction;
import ee.carlrobert.codegpt.toolwindow.conversations.actions.MoveUpAction;
import ee.carlrobert.openai.client.ClientCode;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
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
    SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true);
    panel.setContent(conversationsToolWindowContent);

    var actionGroup = new DefaultActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new MoveDownAction(this::refresh));
    actionGroup.add(new MoveUpAction(this::refresh));
    actionGroup.addSeparator();
    actionGroup.add(new DeleteConversationAction(this::refresh));
    actionGroup.add(new ClearAllConversationsAction(this::refresh));

    ActionToolbar actionToolbar = ActionManager.getInstance()
        .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, true);
    panel.setToolbar(actionToolbar.getComponent());
    return panel;
  }

  public void refresh() {
    scrollablePanel.removeAll();

    var sortedConversations = ConversationsState.getInstance().getSortedConversations();
    if (sortedConversations.isEmpty()) {
      var emptyLabel = new JLabel("No conversations exist.");
      emptyLabel.setFont(JBFont.h2());
      emptyLabel.setBorder(JBUI.Borders.empty(8));
      scrollablePanel.add(emptyLabel);
    } else {
      sortedConversations.forEach(this::addContent);
    }

    scrollablePanel.revalidate();
    scrollablePanel.repaint();
  }

  private void addContent(Conversation conversation) {
    var mainPanel = new RootConversationPanel(() -> {
      changeSettings(conversation);

      var contentManagerService = project.getService(ChatContentManagerService.class);
      contentManagerService.displayChatTab(project);
      contentManagerService.tryFindChatTabbedPane(project)
          .ifPresent(tabbedPane -> tabbedPane.tryFindActiveConversationIndex(conversation.getId())
              .ifPresentOrElse(tabbedPane::setSelectedIndex, () -> {
                var panel = new ChatToolWindowTabPanel(project);
                panel.displayConversation(conversation);
                tabbedPane.addNewTab(panel);
              }));
    });

    var currentConversation = ConversationsState.getCurrentConversation();
    var isSelected = currentConversation != null && currentConversation.getId().equals(conversation.getId());
    mainPanel.add(new ConversationPanel(conversation, isSelected));
    mainPanel.setBackground(conversationsToolWindowContent.getBackground());
    scrollablePanel.add(mainPanel);
  }

  private void changeSettings(Conversation conversation) {
    var settings = SettingsState.getInstance();
    var isChatCompletions = ClientCode.CHAT_COMPLETION.equals(conversation.getClientCode());
    if (isChatCompletions) {
      settings.chatCompletionBaseModel = conversation.getModel();
    } else {
      settings.textCompletionBaseModel = conversation.getModel();
    }
    settings.isChatCompletionOptionSelected = isChatCompletions;
    settings.isTextCompletionOptionSelected = !isChatCompletions;
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
