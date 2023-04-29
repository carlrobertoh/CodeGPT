package ee.carlrobert.codegpt.state.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.util.SwingUtils;
import ee.carlrobert.openai.client.completion.CompletionModel;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import java.awt.Desktop;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

public class SettingsComponent {

  private final JPanel mainPanel;
  private final JBTextField resourceNameField;
  private final JBTextField deploymentIdField;
  private final JBTextField apiVersionField;
  private final JBTextField apiKeyField;
  private final JBTextField organizationField;
  private final JBTextField displayNameField;
  private final JBCheckBox useOpenAIAccountNameCheckBox;
  private final JBCheckBox useAzureCheckbox;
  private final ComboBox<CompletionModel> chatCompletionBaseModelComboBox;
  private final ComboBox<CompletionModel> textCompletionBaseModelComboBox;
  private final JBRadioButton useChatCompletionRadioButton;
  private final JBRadioButton useTextCompletionRadioButton;

  public SettingsComponent(SettingsState settings) {
    apiKeyField = new JBTextField(settings.apiKey, 40);
    useAzureCheckbox = new JBCheckBox("Use Azure OpenAI service API", false);
    resourceNameField = new JBTextField(settings.resourceName, 40);
    deploymentIdField = new JBTextField(settings.resourceName, 40);
    apiVersionField = new JBTextField(settings.resourceName, 40);
    organizationField = new JBTextField(settings.organization, 40);
    displayNameField = new JBTextField(settings.displayName, 20);
    useOpenAIAccountNameCheckBox = new JBCheckBox("Use OpenAI account name", true);
    chatCompletionBaseModelComboBox = new BaseModelComboBox(
        new ChatCompletionModel[]{
            ChatCompletionModel.GPT_3_5,
            ChatCompletionModel.GPT_3_5_SNAPSHOT,
            ChatCompletionModel.GPT_4,
            ChatCompletionModel.GPT_4_32k
        },
        ChatCompletionModel.findByCode(settings.chatCompletionBaseModel));
    textCompletionBaseModelComboBox = new BaseModelComboBox(
        new TextCompletionModel[]{
            TextCompletionModel.DAVINCI,
            TextCompletionModel.CURIE,
            TextCompletionModel.BABBAGE,
            TextCompletionModel.ADA,
        },
        TextCompletionModel.findByCode(settings.textCompletionBaseModel));
    useChatCompletionRadioButton = new JBRadioButton("Use chat completion",
        settings.isChatCompletionOptionSelected);
    useTextCompletionRadioButton = new JBRadioButton("Use text completion",
        settings.isTextCompletionOptionSelected);
    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator("Integration Preference"))
        .addVerticalGap(8)
        .addComponent(createMainSelectionForm())
        .addVerticalGap(8)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();

    registerButtons();
    registerFields();
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public JComponent getPreferredFocusedComponent() {
    return apiKeyField;
  }

  public String getApiKey() {
    return apiKeyField.getText();
  }

  public void setApiKey(String apiKey) {
    apiKeyField.setText(apiKey);
  }

  public void setUseAzureCheckbox(boolean selected) {
    useAzureCheckbox.setSelected(selected);
  }

  public boolean isUseAzure() {
    return useAzureCheckbox.isSelected();
  }

  public String getResourceName() {
    return resourceNameField.getText();
  }

  public void setResourceName(String resourceName) {
    resourceNameField.setText(resourceName);
  }

  public String getDeploymentId() {
    return deploymentIdField.getText();
  }

  public void setDeploymentId(String deploymentId) {
    deploymentIdField.setText(deploymentId);
  }
  public String getApiVersion() {
    return apiVersionField.getText();
  }

  public void setApiVersionField(String apiVersion) {
    apiVersionField.setText(apiVersion);
  }

  public String getOrganization() {
    return organizationField.getText();
  }

  public void setOrganization(String organization) {
    organizationField.setText(organization);
  }

  public String getDisplayName() {
    return displayNameField.getText();
  }

  public void setDisplayName(String displayName) {
    displayNameField.setText(displayName);
  }

  public void setUseOpenAIAccountNameCheckBox(boolean selected) {
    useOpenAIAccountNameCheckBox.setSelected(selected);
  }

  public boolean isUseOpenAIAccountName() {
    return useOpenAIAccountNameCheckBox.isSelected();
  }

  public boolean isChatCompletionOptionSelected() {
    return useChatCompletionRadioButton.isSelected();
  }

  public void setUseChatCompletionSelected(boolean isSelected) {
    useChatCompletionRadioButton.setSelected(isSelected);
  }

  public boolean isTextCompletionOptionSelected() {
    return useTextCompletionRadioButton.isSelected();
  }

  public void setUseTextCompletionSelected(boolean isSelected) {
    useTextCompletionRadioButton.setSelected(isSelected);
  }

  public TextCompletionModel getTextCompletionBaseModel() {
    return (TextCompletionModel) textCompletionBaseModelComboBox.getSelectedItem();
  }

  public void setTextCompletionBaseModel(String modelCode) {
    textCompletionBaseModelComboBox.setSelectedItem(TextCompletionModel.findByCode(modelCode));
  }

  public ChatCompletionModel getChatCompletionBaseModel() {
    return (ChatCompletionModel) chatCompletionBaseModelComboBox.getSelectedItem();
  }

  public void setChatCompletionBaseModel(String modelCode) {
    chatCompletionBaseModelComboBox.setSelectedItem(ChatCompletionModel.findByCode(modelCode));
  }

  private JPanel createMainSelectionForm() {
    var apiKeyFieldPanel = UI.PanelFactory.panel(apiKeyField)
        .withLabel("API key:")
        .resizeX(false)
        .withComment(
            "You can find your Secret API key in your <a href=\"https://platform.openai.com/account/api-keys\">User settings</a>.")
        .withCommentHyperlinkListener(this::handleHyperlinkClicked)
        .createPanel();
    var organizationFieldPanel = UI.PanelFactory.panel(organizationField)
        .withLabel("Organization:")
        .resizeX(false)
        .withComment("Useful when you are part of multiple organizations")
        .createPanel();
    var displayNameFieldPanel = SwingUtils.createPanel(displayNameField, "Display name:", false);

    SwingUtils.setEqualLabelWidths(organizationFieldPanel, displayNameFieldPanel);
    SwingUtils.setEqualLabelWidths(apiKeyFieldPanel, displayNameFieldPanel);

    var chatCompletionModelsPanel = SwingUtils.createPanel(
        chatCompletionBaseModelComboBox, "Model:", false);
    chatCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(16));

    var textCompletionModelsPanel = SwingUtils.createPanel(
        textCompletionBaseModelComboBox, "Model:", false);
    textCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(16));

    var panel = FormBuilder.createFormBuilder()
        .addComponent(FormBuilder.createFormBuilder()
            .addComponent(apiKeyFieldPanel)
            .addComponent(useAzureCheckbox)
            .addComponent(createAzureServicePanel())
            .addComponent(organizationFieldPanel)
            .addVerticalGap(8)
            .addComponent(displayNameFieldPanel)
            .addComponent(useOpenAIAccountNameCheckBox)
            .addVerticalGap(16)
            .addComponent(UI.PanelFactory.panel(useChatCompletionRadioButton)
                .withComment("OpenAI’s most advanced language model")
                .createPanel())
            .addComponent(chatCompletionModelsPanel)
            .addVerticalGap(8)
            .addComponent(UI.PanelFactory.panel(useTextCompletionRadioButton)
                .withComment("Best for high-quality texts")
                .createPanel())
            .addComponent(textCompletionModelsPanel)
            .getPanel())
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }

  private JPanel createAzureServicePanel() {
    JPanel azureRelatedFieldsPanel = new JPanel();
    var resourceNameFieldPanel = UI.PanelFactory.panel(resourceNameField)
            .withLabel("Resource name:")
            .resizeX(false)
            .withComment(
                    "Azure OpenAI Service resource name")
            .createPanel();
    var deploymentIdFieldPanel = UI.PanelFactory.panel(deploymentIdField)
            .withLabel("Deployment ID:")
            .resizeX(false)
            .withComment(
                    "Azure OpenAI Service deployment ID")
            .createPanel();
    var apiVersionFieldPanel = UI.PanelFactory.panel(apiVersionField)
            .withLabel("API version:")
            .resizeX(false)
            .withComment(
                    "API version to be used for Azure OpenAI Service")
            .createPanel();
    azureRelatedFieldsPanel.setLayout(new BoxLayout(azureRelatedFieldsPanel, BoxLayout.Y_AXIS));

    azureRelatedFieldsPanel.add(resourceNameFieldPanel);
    azureRelatedFieldsPanel.add(deploymentIdFieldPanel);
    azureRelatedFieldsPanel.add(apiVersionFieldPanel);
    SwingUtils.setEqualLabelWidths(deploymentIdFieldPanel, resourceNameFieldPanel);
    SwingUtils.setEqualLabelWidths(apiVersionFieldPanel, resourceNameFieldPanel);

    azureRelatedFieldsPanel.setVisible(false);

    useAzureCheckbox.addItemListener(e -> azureRelatedFieldsPanel.setVisible(e.getStateChange() == ItemEvent.SELECTED));

    return azureRelatedFieldsPanel;
  }

  private void registerButtons() {
    ButtonGroup completionButtonGroup = new ButtonGroup();
    completionButtonGroup.add(useChatCompletionRadioButton);
    completionButtonGroup.add(useTextCompletionRadioButton);
    useChatCompletionRadioButton.addActionListener(e -> {
      chatCompletionBaseModelComboBox.setEnabled(true);
      textCompletionBaseModelComboBox.setEnabled(false);
    });
    useTextCompletionRadioButton.addActionListener(e -> {
      chatCompletionBaseModelComboBox.setEnabled(false);
      textCompletionBaseModelComboBox.setEnabled(true);
    });
  }

  private void registerFields() {
    chatCompletionBaseModelComboBox.setEnabled(useChatCompletionRadioButton.isSelected());
    textCompletionBaseModelComboBox.setEnabled(useTextCompletionRadioButton.isSelected());
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
