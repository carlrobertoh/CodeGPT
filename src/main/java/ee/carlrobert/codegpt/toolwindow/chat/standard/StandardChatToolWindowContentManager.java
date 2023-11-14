package ee.carlrobert.codegpt.toolwindow.chat.standard;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowTabPanel;
import java.util.Arrays;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
public final class StandardChatToolWindowContentManager {

  private final Project project;

  public StandardChatToolWindowContentManager(Project project) {
    this.project = project;
  }

  public void sendMessage(Message message) {
    var chatTabbedPane = tryFindChatTabbedPane();
    if (chatTabbedPane.isPresent()) {
      var activeTabPanel = chatTabbedPane.get().tryFindActiveTabPanel();
      if (activeTabPanel.isPresent()) {
        sendMessage(activeTabPanel.get(), message);
        return;
      }
    }
    sendMessage(createNewTabPanel(), message);
  }

  public void sendMessage(ChatToolWindowTabPanel toolWindowTabPanel, Message message) {
    var toolWindow = getToolWindow();
    if (toolWindow != null) {
      toolWindow.show();
    }

    if (ConfigurationState.getInstance().isCreateNewChatOnEachAction()
        || ConversationsState.getCurrentConversation() == null) {
      ConversationService.getInstance().startConversation();
    } else {
      toolWindowTabPanel.sendMessage(message);
    }
  }

  public void displayConversation(@NotNull Conversation conversation) {
    displayChatTab();
    tryFindChatTabbedPane()
        .ifPresent(tabbedPane -> tabbedPane.tryFindTabTitle(conversation.getId())
            .ifPresentOrElse(
                title -> tabbedPane.setSelectedIndex(tabbedPane.indexOfTab(title)),
                () -> tabbedPane.addNewTab(
                    new StandardChatToolWindowTabPanel(project, conversation))));
  }

  public StandardChatToolWindowTabPanel createNewTabPanel() {
    displayChatTab();
    var tabbedPane = tryFindChatTabbedPane();
    if (tabbedPane.isPresent()) {
      var panel = new StandardChatToolWindowTabPanel(
          project,
          ConversationService.getInstance().startConversation());
      tabbedPane.get().addNewTab(panel);
      return panel;
    }
    return null;
  }

  public void displayChatTab() {
    var toolWindow = getToolWindow();
    toolWindow.show();

    var contentManager = toolWindow.getContentManager();
    tryFindChatTabContent().ifPresentOrElse(
        contentManager::setSelectedContent,
        () -> contentManager.setSelectedContent(requireNonNull(contentManager.getContent(0)))
    );
  }

  public Optional<StandardChatToolWindowTabbedPane> tryFindChatTabbedPane() {
    var chatTabContent = tryFindChatTabContent();
    if (chatTabContent.isPresent()) {
      var tabbedPane = Arrays.stream(chatTabContent.get().getComponent().getComponents())
          .filter(component -> component instanceof StandardChatToolWindowTabbedPane)
          .findFirst();
      if (tabbedPane.isPresent()) {
        return Optional.of((StandardChatToolWindowTabbedPane) tabbedPane.get());
      }
    }
    return Optional.empty();
  }

  public void resetAll() {
    tryFindChatTabbedPane().ifPresent(tabbedPane -> {
      tabbedPane.clearAll();
      tabbedPane.addNewTab(new StandardChatToolWindowTabPanel(
          project,
          ConversationService.getInstance().startConversation()));
    });
  }

  public ToolWindow getToolWindow() {
    return requireNonNull(ToolWindowManager.getInstance(project).getToolWindow("CodeGPT"));
  }

  private Optional<Content> tryFindChatTabContent() {
    return Arrays.stream(getToolWindow().getContentManager().getContents())
        .filter(content -> "Chat".equals(content.getTabName()))
        .findFirst();
  }
}
