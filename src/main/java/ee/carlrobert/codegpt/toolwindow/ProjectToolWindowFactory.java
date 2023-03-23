package ee.carlrobert.codegpt.toolwindow;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import ee.carlrobert.codegpt.account.AccountDetailsState;
import ee.carlrobert.codegpt.client.ClientProvider;
import ee.carlrobert.codegpt.toolwindow.chat.ChatToolWindowPanel;
import ee.carlrobert.codegpt.toolwindow.conversations.ConversationsToolWindow;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public class ProjectToolWindowFactory implements ToolWindowFactory, DumbAware {

  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    var conversationsToolWindow = new ConversationsToolWindow(project);
    var chatToolWindowPanel = new ChatToolWindowPanel(project, toolWindow);

    addContent(toolWindow, chatToolWindowPanel, "Chat");
    addContent(toolWindow, conversationsToolWindow.getContent(), "Conversation History");
    toolWindow.addContentManagerListener(new ContentManagerListener() {
      public void selectionChanged(@NotNull ContentManagerEvent event) {
        var content = event.getContent();
        if ("Conversation History".equals(content.getTabName()) && content.isSelected()) {
          conversationsToolWindow.refresh();
        } else if ("Chat".equals(content.getTabName()) && content.isSelected()) {
          ClientProvider.getBillingClient()
              .getCreditUsageAsync(creditUsage -> {
                var accountDetails = AccountDetailsState.getInstance();
                accountDetails.totalAmountGranted = creditUsage.getTotalGranted();
                accountDetails.totalAmountUsed = creditUsage.getTotalUsed();
              });
        }
      }
    });
  }

  public void addContent(ToolWindow toolWindow, JComponent panel, String displayName) {
    var contentManager = toolWindow.getContentManager();
    var content = contentManager.getFactory().createContent(panel, displayName, false);
    contentManager.addContent(content);
  }
}
