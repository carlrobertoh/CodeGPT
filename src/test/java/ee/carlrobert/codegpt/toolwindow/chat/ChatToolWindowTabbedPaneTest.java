package ee.carlrobert.codegpt.toolwindow.chat;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.conversations.ConversationService;
import ee.carlrobert.codegpt.conversations.message.Message;

public class ChatToolWindowTabbedPaneTest extends BasePlatformTestCase {

  public void testClearAllTabs() {
    var tabbedPane = new ChatToolWindowTabbedPane(Disposer.newDisposable());
    tabbedPane.addNewTab(createNewTabPanel());

    tabbedPane.clearAll();

    assertThat(tabbedPane.getActiveTabMapping()).isEmpty();
  }


  public void testAddingNewTabs() {
    var tabbedPane = new ChatToolWindowTabbedPane(Disposer.newDisposable());

    tabbedPane.addNewTab(createNewTabPanel());
    tabbedPane.addNewTab(createNewTabPanel());
    tabbedPane.addNewTab(createNewTabPanel());

    assertThat(tabbedPane.getActiveTabMapping().keySet())
        .containsExactly("Chat 1", "Chat 2", "Chat 3");
  }

  public void testResetCurrentlyActiveTabPanel() {
    var tabbedPane = new ChatToolWindowTabbedPane(Disposer.newDisposable());
    var conversation = ConversationService.getInstance().startConversation();
    conversation.addMessage(new Message("TEST_PROMPT", "TEST_RESPONSE"));
    tabbedPane.addNewTab(new ChatToolWindowTabPanel(getProject(), conversation));

    tabbedPane.resetCurrentlyActiveTabPanel(getProject());

    var tabPanel = tabbedPane.getActiveTabMapping().get("Chat 1");
    assertThat(tabPanel.getConversation().getMessages()).isEmpty();
  }

  private ChatToolWindowTabPanel createNewTabPanel() {
    return new ChatToolWindowTabPanel(
        getProject(),
        ConversationService.getInstance().startConversation());
  }
}
