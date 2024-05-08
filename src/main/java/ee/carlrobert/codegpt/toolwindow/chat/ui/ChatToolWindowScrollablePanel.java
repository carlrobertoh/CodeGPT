package ee.carlrobert.codegpt.toolwindow.chat.ui;

import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.roots.ui.componentsList.layout.VerticalStackLayout;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.ui.UIUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ChatToolWindowScrollablePanel extends ScrollablePanel {

  private final Map<UUID, JPanel> visibleMessagePanels = new HashMap<>();

  public ChatToolWindowScrollablePanel() {
    super(new VerticalStackLayout());
  }

  public void displayLandingView(JComponent landingView) {
    clearAll();
    add(landingView);
    if (GeneralSettings.getCurrentState().getSelectedService() == ServiceType.CODEGPT
        && !CredentialsStore.INSTANCE.isCredentialSet(CredentialKey.CODEGPT_API_KEY)) {

      var panel = new ResponsePanel()
          .addContent(UIUtil.createTextPane("""
              <html>
              <p style="margin-top: 4px; margin-bottom: 4px;">
                It looks like you haven't configured your API key yet. Visit the <a href="https://codegpt.carlrobert.ee/account">CodeGPT settings</a> to do so.
              </p>
              <p style="margin-top: 4px; margin-bottom: 4px;">
                Don't have an account? <a href="https://codegpt.carlrobert.ee/signin">Sign up</a> now for free access to all open-source models.
              </p>
              </html>""", false, UIUtil::handleHyperlinkClicked));
      panel.setBorder(JBUI.Borders.customLine(JBColor.border(), 1, 0, 0, 0));
      add(panel);
    }
  }

  public ResponsePanel getMessageResponsePanel(UUID messageId) {
    return (ResponsePanel) Arrays.stream(visibleMessagePanels.get(messageId).getComponents())
        .filter(ResponsePanel.class::isInstance)
        .findFirst().orElseThrow();
  }

  public JPanel addMessage(UUID messageId) {
    var messageWrapper = new JPanel();
    messageWrapper.setLayout(new BoxLayout(messageWrapper, BoxLayout.PAGE_AXIS));
    add(messageWrapper);
    visibleMessagePanels.put(messageId, messageWrapper);
    return messageWrapper;
  }

  public void removeMessage(UUID messageId) {
    remove(visibleMessagePanels.get(messageId));
    update();
    visibleMessagePanels.remove(messageId);
  }

  public void clearAll() {
    visibleMessagePanels.clear();
    removeAll();
    update();
  }

  public void update() {
    repaint();
    revalidate();
  }
}
