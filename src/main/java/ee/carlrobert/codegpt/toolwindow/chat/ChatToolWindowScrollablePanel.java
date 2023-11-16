package ee.carlrobert.codegpt.toolwindow.chat;

import static ee.carlrobert.codegpt.util.ThemeUtils.getPanelBackgroundColor;

import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.roots.ui.componentsList.layout.VerticalStackLayout;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.codegpt.toolwindow.chat.components.ResponsePanel;
import ee.carlrobert.codegpt.util.SwingUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;

public class ChatToolWindowScrollablePanel extends ScrollablePanel {

  private final SettingsState settings;
  private final YouUserManager youUserManager;
  private final Map<UUID, JPanel> visibleMessagePanels;

  ChatToolWindowScrollablePanel(SettingsState settings) {
    super(new VerticalStackLayout());
    this.settings = settings;
    this.youUserManager = YouUserManager.getInstance();
    this.visibleMessagePanels = new HashMap<>();
  }

  public void displayLandingView(JComponent landingView) {
    clearAll();
    add(landingView);
    if (settings.getSelectedService() == ServiceType.YOU
        && (!youUserManager.isAuthenticated() || !youUserManager.isSubscribed())) {
      add(new ResponsePanel().addContent(createYouCouponTextPane()));
    }
  }

  public ResponsePanel getMessageResponsePanel(UUID messageId) {
    return (ResponsePanel) Arrays.stream(visibleMessagePanels.get(messageId).getComponents())
        .filter(component -> component instanceof ResponsePanel)
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

  // TODO: Move
  private JTextPane createYouCouponTextPane() {
    var textPane = SwingUtils.createTextPane(
        "<html>\n"
            + "<body>\n"
            + "  <p style=\"margin: 4px 0;\">Use CodeGPT coupon for free month of GPT-4.</p>\n"
            + "  <p style=\"margin: 4px 0;\">\n"
            + "    <a href=\"https://you.com/plans\">Sign up here</a>\n"
            + "  </p>\n"
            + "</body>\n"
            + "</html>"
    );
    textPane.setBackground(getPanelBackgroundColor());
    textPane.setFocusable(false);
    return textPane;
  }
}
