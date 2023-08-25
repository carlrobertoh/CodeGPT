package ee.carlrobert.codegpt.toolwindow.chat;

import static org.assertj.core.api.Assertions.assertThat;

import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowTabPanel;
import ee.carlrobert.codegpt.toolwindow.chat.standard.StandardChatToolWindowTabbedPane;

public class StandardChatToolWindowTabbedPaneTest extends BasePlatformTestCase {

  public void testClearAllTabs() {
    var tabbedPane = new StandardChatToolWindowTabbedPane(Disposer.newDisposable());
    tabbedPane.addNewTab(new StandardChatToolWindowTabPanel(getProject()));

    tabbedPane.clearAll();

    assertThat(tabbedPane.getActiveTabMapping()).isEmpty();
  }

  public void testAddingNewTabs() {
    var tabbedPane = new StandardChatToolWindowTabbedPane(Disposer.newDisposable());

    tabbedPane.addNewTab(new StandardChatToolWindowTabPanel(getProject()));
    tabbedPane.addNewTab(new StandardChatToolWindowTabPanel(getProject()));
    tabbedPane.addNewTab(new StandardChatToolWindowTabPanel(getProject()));

    var tabMapping = tabbedPane.getActiveTabMapping();
    assertThat(tabMapping.keySet()).containsExactly("Chat 1", "Chat 2", "Chat 3");
    assertThat(tabMapping.values().stream().allMatch(item -> item.getConversation() == null)).isTrue();
  }

  public void testResetCurrentlyActiveTabPanel() {
    var tabbedPane = new StandardChatToolWindowTabbedPane(Disposer.newDisposable());
    var tabPanel = new StandardChatToolWindowTabPanel(getProject());
    var message = new Message("TEST_PROMPT");
    message.setResponse("TEST_RESPONSE");
    var conversation = new Conversation();
    conversation.addMessage(message);
    tabPanel.setConversation(conversation);
    tabbedPane.addNewTab(tabPanel);

    tabbedPane.resetCurrentlyActiveTabPanel();

    var tabMapping = tabbedPane.getActiveTabMapping().get("Chat 1");
    assertThat(tabMapping.getConversation()).isNull();
  }
}
