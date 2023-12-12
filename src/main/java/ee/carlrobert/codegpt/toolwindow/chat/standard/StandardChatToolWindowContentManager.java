package ee.carlrobert.codegpt.toolwindow.chat.standard;

import static java.util.Objects.requireNonNull;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import ee.carlrobert.codegpt.Icons;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.ConversationsState;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
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
    getToolWindow().show();

    if (ConfigurationState.getInstance().isCreateNewChatOnEachAction()
        || ConversationsState.getCurrentConversation() == null) {
      createNewTabPanel().sendMessage(message);
      return;
    }

    tryFindChatTabbedPane()
        .map(tabbedPane -> tabbedPane.tryFindActiveTabPanel().orElseGet(this::createNewTabPanel))
        .orElseGet(this::createNewTabPanel)
        .sendMessage(message);
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
    return tryFindChatTabbedPane()
        .map(item -> {
          var panel = new StandardChatToolWindowTabPanel(
              project,
              ConversationService.getInstance().startConversation());
          item.addNewTab(panel);
          return panel;
        })
        .orElseThrow();
  }

  public void displayChatTab() {
    var toolWindow = getToolWindow();
    toolWindow.show();

    var contentManager = toolWindow.getContentManager();
    tryFindFirstChatTabContent().ifPresentOrElse(
        contentManager::setSelectedContent,
        () -> contentManager.setSelectedContent(requireNonNull(contentManager.getContent(0)))
    );
  }

  public Optional<StandardChatToolWindowTabbedPane> tryFindChatTabbedPane() {
    var chatTabContent = tryFindFirstChatTabContent();
    if (chatTabContent.isPresent()) {
      var chatToolWindowPanel = (StandardChatToolWindowPanel) chatTabContent.get().getComponent();
      return Optional.of(chatToolWindowPanel.getChatTabbedPane());
    }
    return Optional.empty();
  }

  public Optional<StandardChatToolWindowPanel> tryFindChatToolWindowPanel() {
    return tryFindFirstChatTabContent()
        .map(ComponentContainer::getComponent)
        .filter(component -> component instanceof StandardChatToolWindowPanel)
        .map(component -> (StandardChatToolWindowPanel) component);
  }

  public void resetAll() {
    tryFindChatTabbedPane().ifPresent(tabbedPane -> {
      tabbedPane.clearAll();
      tabbedPane.addNewTab(new StandardChatToolWindowTabPanel(
          project,
          ConversationService.getInstance().startConversation()));
    });
  }

  public @NotNull ToolWindow getToolWindow() {
    var toolWindowManager = ToolWindowManager.getInstance(project);
    var toolWindow = toolWindowManager.getToolWindow("CodeGPT");
    if (toolWindow == null) {
      // https://intellij-support.jetbrains.com/hc/en-us/community/posts/11533368171026/comments/11538403084562
      return toolWindowManager
          .registerToolWindow(RegisterToolWindowTask.closable(
              "CodeGPT",
              () -> "CodeGPT",
              Icons.DefaultSmall,
              ToolWindowAnchor.RIGHT));
    }
    return toolWindow;
  }

  private Optional<Content> tryFindFirstChatTabContent() {
    return Arrays.stream(getToolWindow().getContentManager().getContents())
        .filter(content -> "Chat".equals(content.getTabName()))
        .findFirst();
  }
}
