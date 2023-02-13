package ee.carlrobert.chatgpt.service;

import static ee.carlrobert.chatgpt.toolwindow.ToolWindowUtil.createIconLabel;
import static ee.carlrobert.chatgpt.toolwindow.ToolWindowUtil.createTextArea;
import static ee.carlrobert.chatgpt.toolwindow.ToolWindowUtil.justifyLeft;

import ee.carlrobert.chatgpt.client.ApiClient;
import ee.carlrobert.chatgpt.toolwindow.Loader;
import ee.carlrobert.chatgpt.toolwindow.ScrollablePanel;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.swing.Box;
import javax.swing.JScrollPane;

public class ToolWindowService {

  private ScrollablePanel scrollablePanel;

  public void setScrollablePanel(ScrollablePanel scrollablePanel) {
    this.scrollablePanel = scrollablePanel;
  }

  public ScrollablePanel getScrollablePanel() {
    return scrollablePanel;
  }

  public void sendMessage(String userMessage, String prompt) {
    sendMessage(userMessage, prompt, null);
  }

  public void sendMessage(String userMessage, String prompt, @Nullable SuccessCallback onSuccess) {
    scrollablePanel.add(createTextArea(userMessage, true, false));
    scrollablePanel.add(Box.createVerticalStrut(16));
    scrollablePanel.add(justifyLeft(createIconLabel(Objects.requireNonNull(getClass().getResource("/icons/chatgpt-icon.png")))));

    var loader = new Loader();
    scrollablePanel.add(justifyLeft(loader.getComponent()));
    loader.startLoading();
    scrollablePanel.add(Box.createVerticalStrut(4));

    ApiClient.getInstance().getCompletionsAsync(prompt, response -> {
      loader.stopLoading();
      scrollablePanel.add(Box.createVerticalStrut(4));
      for (var choice : response.getChoices()) {
        scrollablePanel.add(createTextArea(choice.getText().trim(), false, true));
      }
      scrollablePanel.add(Box.createVerticalStrut(32));

      if (onSuccess != null) {
        onSuccess.call();
      }
    }, apiError -> {
      loader.stopLoading();
      scrollablePanel.add(Box.createVerticalStrut(4));
      scrollablePanel.add(createTextArea(apiError.getMessage(), false, true));
      scrollablePanel.add(Box.createVerticalStrut(32));
    });
  }
}
