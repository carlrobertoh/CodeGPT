package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.roots.ui.componentsList.layout.VerticalStackLayout;
import ee.carlrobert.codegpt.toolwindow.chat.components.ResponsePanel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ChatToolWindowScrollablePanel extends ScrollablePanel {

  private final Map<UUID, JPanel> visibleMessagePanels = new HashMap<>();

  ChatToolWindowScrollablePanel() {
    super(new VerticalStackLayout());
  }

  public void displayLandingView(JComponent landingView) {
    clearAll();
    add(landingView);
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
}
