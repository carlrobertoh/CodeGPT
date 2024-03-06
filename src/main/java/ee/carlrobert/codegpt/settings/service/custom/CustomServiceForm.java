package ee.carlrobert.codegpt.settings.service.custom;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.icons.AllIcons.General;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.MessageType;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.CallParameters;
import ee.carlrobert.codegpt.completions.CompletionRequestProvider;
import ee.carlrobert.codegpt.completions.CompletionRequestService;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.codegpt.credentials.CustomServiceCredentialManager;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import ee.carlrobert.codegpt.ui.UIUtil;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.completion.CompletionEventListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Nullable;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import okhttp3.sse.EventSource;

public class CustomServiceForm {

  private final JBPasswordField apiKeyField;
  private final JBTextField urlField;
  private final CustomServiceFormTabbedPane tabbedPane;
  private final JButton testConnectionButton;
  private final JBLabel templateHelpText;
  private final ComboBox<CustomServiceTemplate> templateComboBox;

  public CustomServiceForm(CustomServiceSettingsState settings) {
    apiKeyField = new JBPasswordField();
    apiKeyField.setColumns(30);
    apiKeyField.setText(CustomServiceCredentialManager.getInstance().getCredential());
    urlField = new JBTextField(settings.getUrl(), 30);
    tabbedPane = new CustomServiceFormTabbedPane(settings);
    testConnectionButton = new JButton(CodeGPTBundle.get(
        "settingsConfigurable.service.custom.openai.testConnection.label"));
    testConnectionButton.addActionListener(e -> testConnection(getCurrentState()));
    templateHelpText = new JBLabel(General.ContextHelp);
    templateComboBox = new ComboBox<>(
        new EnumComboBoxModel<>(CustomServiceTemplate.class));
    templateComboBox.setSelectedItem(settings.getTemplate());
    templateComboBox.addItemListener(e -> {
      var template = (CustomServiceTemplate) e.getItem();
      updateTemplateHelpTextTooltip(template);
      urlField.setText(template.getUrl());
      tabbedPane.setHeaders(template.getHeaders());
      tabbedPane.setBody(template.getBody());
    });
    updateTemplateHelpTextTooltip(settings.getTemplate());
  }

  public JPanel getForm() {
    var urlPanel = new JPanel(new BorderLayout(8, 0));
    urlPanel.add(urlField, BorderLayout.CENTER);
    urlPanel.add(testConnectionButton, BorderLayout.EAST);

    var templateComboBoxWrapper = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    templateComboBoxWrapper.add(templateComboBox);
    templateComboBoxWrapper.add(Box.createHorizontalStrut(8));
    templateComboBoxWrapper.add(templateHelpText);

    var form = FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.custom.openai.presetTemplate.label"),
            templateComboBoxWrapper)
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"),
            apiKeyField)
        .addComponentToRightColumn(
            UIUtil.createComment("settingsConfigurable.service.custom.openai.apiKey.comment"))
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.custom.openai.url.label"),
            urlPanel)
        .addComponent(tabbedPane)
        .getPanel();

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(CodeGPTBundle.get("shared.configuration")))
        .addComponent(withEmptyLeftBorder(form))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public @Nullable String getApiKey() {
    var apiKey = new String(apiKeyField.getPassword());
    return apiKey.isEmpty() ? null : apiKey;
  }

  public CustomServiceSettingsState getCurrentState() {
    var state = new CustomServiceSettingsState();
    state.setUrl(urlField.getText());
    state.setTemplate(templateComboBox.getItem());
    state.setHeaders(tabbedPane.getHeaders());
    state.setBody(tabbedPane.getBody());
    return state;
  }

  public void resetForm() {
    var state = CustomServiceSettings.getCurrentState();
    apiKeyField.setText(CustomServiceCredentialManager.getInstance().getCredential());
    urlField.setText(state.getUrl());
    templateComboBox.setSelectedItem(state.getTemplate());
    tabbedPane.setHeaders(state.getHeaders());
    tabbedPane.setBody(state.getBody());
  }

  private void updateTemplateHelpTextTooltip(CustomServiceTemplate template) {
    templateHelpText.setToolTipText(null);
    try {
      new HelpTooltip()
          .setTitle(template.getName())
          .setBrowserLink(
              CodeGPTBundle.get("settingsConfigurable.service.custom.openai.linkToDocs"),
              new URL(template.getDocsUrl()))
          .installOn(templateHelpText);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private void testConnection(CustomServiceSettingsState customConfiguration) {
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
