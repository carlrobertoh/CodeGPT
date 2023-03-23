package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
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
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;

public class SettingsComponent {

  private final JPanel mainPanel;
  private final JBTextField apiKeyField;
  private final ComboBox<CompletionModel> chatCompletionBaseModelComboBox;
  private final ComboBox<CompletionModel> textCompletionBaseModelComboBox;
  private final JBRadioButton useChatCompletionRadioButton;
  private final JBRadioButton useTextCompletionRadioButton;

  public SettingsComponent(SettingsState settings) {
    apiKeyField = new JBTextField(settings.apiKey, 1);
    chatCompletionBaseModelComboBox = new BaseModelComboBox(
        new ChatCompletionModel[] {
            ChatCompletionModel.GPT_3_5,
            ChatCompletionModel.GPT_3_5_SNAPSHOT,
            ChatCompletionModel.GPT_4
        },
        ChatCompletionModel.findByCode(settings.chatCompletionBaseModel));
    textCompletionBaseModelComboBox = new BaseModelComboBox(
        new TextCompletionModel[] {
            TextCompletionModel.DAVINCI,
            TextCompletionModel.CURIE,
            TextCompletionModel.BABBAGE,
            TextCompletionModel.ADA,
        },
        TextCompletionModel.findByCode(settings.textCompletionBaseModel));
    useChatCompletionRadioButton = new JBRadioButton("Use chat completion", settings.isChatCompletionOptionSelected);
    useTextCompletionRadioButton = new JBRadioButton("Use text completion", settings.isTextCompletionOptionSelected);
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
        .withComment("You can find your Secret API key in your <a href=\"https://platform.openai.com/account/api-keys\">User settings</a>.")
        .withCommentHyperlinkListener(this::handleHyperlinkClicked)
        .createPanel();
    apiKeyFieldPanel.setBorder(JBUI.Borders.emptyLeft(8));

    var chatCompletionModelsPanel = SwingUtils.createPanel(chatCompletionBaseModelComboBox, "Model:", false);
    chatCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(24));

    var textCompletionModelsPanel = SwingUtils.createPanel(textCompletionBaseModelComboBox, "Model:", false);
    textCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(24));

    var panel = FormBuilder.createFormBuilder()
        .addComponent(FormBuilder.createFormBuilder()
            .addComponent(apiKeyFieldPanel)
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
