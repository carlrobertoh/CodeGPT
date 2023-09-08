package ee.carlrobert.codegpt.toolwindow.conversations;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.actions.toolwindow.DeleteAllConversationsAction;
import ee.carlrobert.codegpt.actions.toolwindow.DeleteConversationAction;
import ee.carlrobert.codegpt.actions.toolwindow.MoveDownAction;
import ee.carlrobert.codegpt.actions.toolwindow.MoveUpAction;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.settings.state.ModelSettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowContentManager;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowTabPanel;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import org.jetbrains.annotations.NotNull;

public class ConversationsToolWindow extends JPanel {

  private final Project project;
  private final ConversationService conversationService;
  private final ScrollablePanel scrollablePanel;
  private final JScrollPane scrollPane;

  public ConversationsToolWindow(@NotNull Project project) {
    this.project = project;
    this.conversationService = ConversationService.getInstance();
    scrollablePanel = new ScrollablePanel();
    scrollablePanel.setLayout(new BoxLayout(scrollablePanel, BoxLayout.Y_AXIS));

    scrollPane = new JBScrollPane();
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewportView(scrollablePanel);
    scrollPane.setBorder(null);
    scrollPane.setViewportBorder(null);
    refresh();
  }

  public JPanel getContent() {
    SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true);
    panel.setContent(scrollPane);

    var actionGroup = new DefaultActionGroup("TOOLBAR_ACTION_GROUP", false);
    actionGroup.add(new MoveDownAction(this::refresh));
    actionGroup.add(new MoveUpAction(this::refresh));
    actionGroup.addSeparator();
    actionGroup.add(new DeleteConversationAction(this::refresh));
    actionGroup.add(new DeleteAllConversationsAction(this::refresh));

    var toolbar = ActionManager.getInstance()
        .createActionToolbar("NAVIGATION_BAR_TOOLBAR", actionGroup, true);
    toolbar.setTargetComponent(panel);
    panel.setToolbar(toolbar.getComponent());
    return panel;
  }

  public void refresh() {
    scrollablePanel.removeAll();

    var sortedConversations = conversationService.getSortedConversations();
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
    var rootPanel = new JPanel(new BorderLayout());
    rootPanel.setBorder(JBUI.Borders.empty(10, 20));
    rootPanel.setBackground(JBColor.background());
    rootPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        ModelSettingsState.getInstance().sync(conversation);

        var toolWindowContentManager = StandardChatToolWindowContentManager.getInstance(project);
        toolWindowContentManager.displayChatTab();
        toolWindowContentManager.tryFindChatTabbedPane()
            .ifPresent(tabbedPane -> tabbedPane.tryFindActiveConversationTitle(conversation.getId())
                .ifPresentOrElse(
                    title -> tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(title)),
                    () -> {
                      var panel = new StandardChatToolWindowTabPanel(project);
                      panel.displayConversation(conversation);
                      tabbedPane.addNewTab(panel);
                    }));
      }
    });

    var currentConversation = ConversationsState.getCurrentConversation();
    var isSelected = currentConversation != null && currentConversation.getId().equals(conversation.getId());
    rootPanel.add(new ConversationPanel(conversation, isSelected));
    scrollablePanel.add(rootPanel);
  }
}
