package ee.carlrobert.codegpt.settings.service.custom;

import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.CUSTOM_SERVICE_API_KEY;
import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.icons.AllIcons.General;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.FormBuilder;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.ui.UIUtil;
import java.awt.FlowLayout;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.Box;
import javax.swing.JPanel;
import org.jetbrains.annotations.Nullable;

public class CustomServiceForm {

  private final JBTabbedPane tabbedPane;
  private final CustomServiceChatCompletionsForm chatCompletionsForm;
  private final CustomServiceCompletionsForm completionsForm;

  private final JBPasswordField apiKeyField;
  private final JBLabel templateHelpText;
  private final ComboBox<CustomServiceTemplate> templateComboBox;

  public CustomServiceForm(CustomServiceSettingsState settings) {
    chatCompletionsForm = new CustomServiceChatCompletionsForm(
        settings.getChatCompletionSettings());
    completionsForm = new CustomServiceCompletionsForm(
        settings.getCompletionSettings());

    tabbedPane = new JBTabbedPane();
    tabbedPane.addTab(CodeGPTBundle.get("shared.chatCompletions"), chatCompletionsForm.getForm());
    tabbedPane.addTab(CodeGPTBundle.get("shared.codeCompletions"), completionsForm.getForm());

    apiKeyField = new JBPasswordField();
    apiKeyField.setColumns(30);
    apiKeyField.setText(CredentialsStore.INSTANCE.getCredential(CUSTOM_SERVICE_API_KEY));

    templateHelpText = new JBLabel(General.ContextHelp);
    templateComboBox = new ComboBox<>(
        new EnumComboBoxModel<>(CustomServiceTemplate.class));
    templateComboBox.setSelectedItem(settings.getTemplate());
    templateComboBox.addItemListener(e -> {
      var template = (CustomServiceTemplate) e.getItem();
      updateTemplateHelpTextTooltip(template);

      chatCompletionsForm.setUrl(template.getUrl());
      chatCompletionsForm.setHeaders(template.getHeaders());
      chatCompletionsForm.setBody(template.getBody());

      completionsForm.setUrl(template.getUrl());
      completionsForm.setHeaders(template.getHeaders());
      completionsForm.setBody(template.getBody());
    });
    updateTemplateHelpTextTooltip(settings.getTemplate());
  }

  public JPanel getForm() {
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
    chatCompletionsForm.populateState(state.getChatCompletionSettings());
    completionsForm.populateState(state.getCompletionSettings());

    state.setTemplate(templateComboBox.getItem());
    return state;
  }

  public void resetForm() {
    var state = CustomServiceSettings.getCurrentState();
    chatCompletionsForm.resetForm(state.getChatCompletionSettings());
    completionsForm.resetForm(state.getCompletionSettings());

    apiKeyField.setText(CredentialsStore.INSTANCE.getCredential(CUSTOM_SERVICE_API_KEY));
    templateComboBox.setSelectedItem(state.getTemplate());
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
}
