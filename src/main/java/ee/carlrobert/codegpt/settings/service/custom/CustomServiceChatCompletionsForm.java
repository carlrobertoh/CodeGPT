package ee.carlrobert.codegpt.settings.service.custom;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.openapi.ui.MessageType;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.CallParameters;
import ee.carlrobert.codegpt.completions.CompletionRequestProvider;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.awt.BorderLayout;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import okhttp3.sse.EventSource;

public class CustomServiceChatCompletionsForm {

  private final JBTextField urlField;
  private final CustomServiceFormTabbedPane tabbedPane;
  private final JButton testConnectionButton;

  public CustomServiceChatCompletionsForm(
      CustomServiceSettingsState.ChatCompletionsSettings state) {
    urlField = new JBTextField(state.getUrl(), 30);
    tabbedPane = new CustomServiceFormTabbedPane(state.getHeaders(), state.getBody());
    testConnectionButton = new JButton(CodeGPTBundle.get(
        "settingsConfigurable.service.custom.openai.testConnection.label"));
    testConnectionButton.addActionListener(e -> testConnection());
  }

  public void setUrl(String url) {
    urlField.setText(url);
  }

  public void setHeaders(Map<String, String> headers) {
    tabbedPane.setHeaders(headers);
  }

  public void setBody(Map<String, Object> body) {
    tabbedPane.setBody(body);
  }

  public JPanel getForm() {
    var urlPanel = new JPanel(new BorderLayout(8, 0));
    urlPanel.add(urlField, BorderLayout.CENTER);
    urlPanel.add(testConnectionButton, BorderLayout.EAST);

    var form = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.custom.openai.url.label"),
            urlPanel)
        .addComponent(tabbedPane)
        .getPanel();

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("/v1/chat/completions"))
        .addComponent(withEmptyLeftBorder(form))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public void populateState(CustomServiceSettingsState.ChatCompletionsSettings settings) {
    settings.setUrl(urlField.getText());
    settings.setHeaders(tabbedPane.getHeaders());
    settings.setBody(tabbedPane.getBody());
  }

  public void resetForm(CustomServiceSettingsState.ChatCompletionsSettings settings) {
    urlField.setText(settings.getUrl());
    tabbedPane.setHeaders(settings.getHeaders());
    tabbedPane.setBody(settings.getBody());
  }

  private void testConnection() {
    var customConfiguration = new CustomServiceSettingsState();
    populateState(customConfiguration.getChatCompletionSettings());

    var conversation = new Conversation();
    var request = new CompletionRequestProvider(conversation)
        .buildCustomOpenAIChatCompletionRequest(
            customConfiguration,
            new CallParameters(conversation, new Message("Hello!")));
    CompletionRequestService.getInstance()
        .getCustomOpenAIChatCompletionAsync(request, new TestConnectionEventListener());
  }

  class TestConnectionEventListener implements CompletionEventListener<String> {

    @Override
    public void onMessage(String value, EventSource eventSource) {
      if (value != null && !value.isEmpty()) {
        SwingUtilities.invokeLater(() -> {
          OverlayUtil.showBalloon(
              CodeGPTBundle.get("settingsConfigurable.service.custom.openai.connectionSuccess"),
              MessageType.INFO,
              testConnectionButton);
          eventSource.cancel();
        });
      }
    }

    @Override
    public void onError(ErrorDetails error, Throwable ex) {
      SwingUtilities.invokeLater(() ->
          OverlayUtil.showBalloon(
              CodeGPTBundle.get("settingsConfigurable.service.custom.openai.connectionFailed")
                  + "\n\n"
                  + error.getMessage(),
              MessageType.ERROR,
              testConnectionButton));
    }
  }
}
