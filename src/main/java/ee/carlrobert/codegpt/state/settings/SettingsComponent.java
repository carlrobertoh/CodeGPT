package ee.carlrobert.codegpt.state.settings;

import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.state.settings.components.ModelSelectionForm;
import ee.carlrobert.codegpt.state.settings.components.ServiceSelectionForm;
import ee.carlrobert.codegpt.util.SwingUtils;
import java.awt.Desktop;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;

public class SettingsComponent {

  private final JPanel mainPanel;
  private final JBTextField apiKeyField;
  private final JBTextField displayNameField;
  private final JBCheckBox useApiKeyFromEnvVarCheckBox;
  private final JBCheckBox useOpenAIAccountNameCheckBox;
  private final ServiceSelectionForm serviceSelectionForm;
  private final ModelSelectionForm modelSelectionForm;

  public SettingsComponent(SettingsState settings) {
    modelSelectionForm = new ModelSelectionForm(settings);
    serviceSelectionForm = new ServiceSelectionForm(settings, modelSelectionForm);

    apiKeyField = new JBTextField(settings.apiKey, 40);
    displayNameField = new JBTextField(settings.displayName, 20);
    useApiKeyFromEnvVarCheckBox = new JBCheckBox(
        "Use API Key from Environment Variable (recommended)", settings.useApiKeyFromEnvVar);
    useOpenAIAccountNameCheckBox = new JBCheckBox(
        "Use OpenAI account name", settings.useOpenAIAccountName);

    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Integration Preference"))
        .addVerticalGap(8)
        .addComponent(createMainSelectionForm())
        .addVerticalGap(8)
        .addComponent(new TitledSeparator("Service Preference"))
        .addVerticalGap(8)
        .addComponent(serviceSelectionForm.getForm())
        .addVerticalGap(8)
        .addComponent(new TitledSeparator("Model Preference"))
        .addVerticalGap(8)
        .addComponent(modelSelectionForm.getForm())
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();

    registerFields();
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return apiKeyField;
  }

  public ServiceSelectionForm getServiceSelectionForm() {
    return serviceSelectionForm;
  }

  public ModelSelectionForm getModelSelectionForm() {
    return modelSelectionForm;
  }

  public String getApiKey() {
    return apiKeyField.getText();
  }

  public void setApiKey(String apiKey) {
    apiKeyField.setText(apiKey);
  }

  public String getDisplayName() {
    return displayNameField.getText();
  }

  public void setDisplayName(String displayName) {
    displayNameField.setText(displayName);
  }

  public void setUseApiKeyFromEnvVarCheckBox(boolean selected) {
    useApiKeyFromEnvVarCheckBox.setSelected(selected);
  }

  public boolean isUseApiKeyFromEnvVar() {
    return useApiKeyFromEnvVarCheckBox.isSelected();
  }

  public void setUseOpenAIAccountNameCheckBox(boolean selected) {
    useOpenAIAccountNameCheckBox.setSelected(selected);
  }

  public boolean isUseOpenAIAccountName() {
    return useOpenAIAccountNameCheckBox.isSelected();
  }

  private JPanel createMainSelectionForm() {
    var apiKeyFieldPanel = UI.PanelFactory.panel(apiKeyField)
        .withLabel("API key:")
        .resizeX(false)
        .withComment(
            "You can find your Secret API key in your <a href=\"https://platform.openai.com/account/api-keys\">User settings</a>.")
        .withCommentHyperlinkListener(this::handleHyperlinkClicked)
        .createPanel();
    var displayNameFieldPanel = SwingUtils.createPanel(displayNameField, "Display name:", false);
    var apiKeyEnvVarFromCheckboxFieldPanel = UI.PanelFactory.panel(useApiKeyFromEnvVarCheckBox)
        .resizeX(false)
        .withComment(
            "Set your OpenAI API key as OPENAI_API_KEY in <a href=\"https://help.openai.com/en/articles/5112595-best-practices-for-api-key-safety#h_a1ab3ba7b2\">environment variables</a>.")
        .withCommentHyperlinkListener(this::handleHyperlinkClicked)
        .createPanel();

    SwingUtils.setEqualLabelWidths(apiKeyFieldPanel, displayNameFieldPanel);

    useApiKeyFromEnvVarCheckBox.addItemListener(event ->
        apiKeyField.setEnabled(event.getStateChange() != ItemEvent.SELECTED));

    var panel = FormBuilder.createFormBuilder()
        .addComponent(FormBuilder.createFormBuilder()
            .addComponent(apiKeyEnvVarFromCheckboxFieldPanel)
            .addComponent(apiKeyFieldPanel)
            .addVerticalGap(8)
            .addComponent(displayNameFieldPanel)
            .addComponent(useOpenAIAccountNameCheckBox)
            .getPanel())
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }

  private void registerFields() {
    apiKeyField.setEnabled(!useApiKeyFromEnvVarCheckBox.isSelected());
  }

  private void handleHyperlinkClicked(HyperlinkEvent event) {
    if (HyperlinkEvent.EventType.ACTIVATED.equals(event.getEventType())) {
      if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        try {
          Desktop.getDesktop().browse(event.getURL().toURI());
        } catch (IOException | URISyntaxException e) {
          throw new RuntimeException("Couldn't open the browser.", e);
        }
      }
    }
  }
}
