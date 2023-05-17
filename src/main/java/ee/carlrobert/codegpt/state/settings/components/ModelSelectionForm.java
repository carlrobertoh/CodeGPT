package ee.carlrobert.codegpt.state.settings.components;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.state.settings.BaseModelComboBox;
import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.util.SwingUtils;
import ee.carlrobert.openai.client.completion.CompletionModel;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

public class ModelSelectionForm {

  private final ComboBox<CompletionModel> chatCompletionBaseModelComboBox;
  private final ComboBox<CompletionModel> textCompletionBaseModelComboBox;
  private final JBTextField customChatCompletionModelField;
  private final JBTextField customTextCompletionModelField;
  private final JBRadioButton useChatCompletionRadioButton;
  private final JBRadioButton useTextCompletionRadioButton;
  private final JPanel chatCompletionModelsPanel;
  private final JPanel customChatCompletionModelPanel;
  private final JPanel customTextCompletionModelPanel;
  private final JPanel textCompletionModelsPanel;

  public ModelSelectionForm(SettingsState settings) {
    chatCompletionBaseModelComboBox = new BaseModelComboBox(
        new ChatCompletionModel[]{
            ChatCompletionModel.GPT_3_5,
            ChatCompletionModel.GPT_3_5_SNAPSHOT,
            ChatCompletionModel.GPT_4,
            ChatCompletionModel.GPT_4_32k
        },
        ChatCompletionModel.findByCode(settings.chatCompletionBaseModel));
    chatCompletionModelsPanel = SwingUtils.createPanel(
        chatCompletionBaseModelComboBox, "Model:", false);
    chatCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(16));
    textCompletionBaseModelComboBox = new BaseModelComboBox(
        new TextCompletionModel[]{
            TextCompletionModel.DAVINCI,
            TextCompletionModel.CURIE,
            TextCompletionModel.BABBAGE,
            TextCompletionModel.ADA,
        },
        TextCompletionModel.findByCode(settings.textCompletionBaseModel));
    customChatCompletionModelField = new JBTextField(settings.customChatCompletionModel, 20);
    customChatCompletionModelPanel = SwingUtils.createPanel(
        customChatCompletionModelField, "Model:", false);
    customChatCompletionModelPanel.setBorder(JBUI.Borders.emptyLeft(16));
    customTextCompletionModelField = new JBTextField(settings.customTextCompletionModel, 20);
    customTextCompletionModelPanel = SwingUtils.createPanel(
        customTextCompletionModelField, "Model:", false);
    customTextCompletionModelPanel.setBorder(JBUI.Borders.emptyLeft(16));
    textCompletionModelsPanel = SwingUtils.createPanel(
        textCompletionBaseModelComboBox, "Model:", false);
    textCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(16));
    useChatCompletionRadioButton = new JBRadioButton("Use chat completion",
        settings.isChatCompletionOptionSelected);
    useTextCompletionRadioButton = new JBRadioButton("Use text completion",
        settings.isTextCompletionOptionSelected);

    registerFields();
    registerRadioButtons();
  }

  public JPanel getForm() {
    var panel = FormBuilder.createFormBuilder()
        .addComponent(useChatCompletionRadioButton)
        .addComponent(customChatCompletionModelPanel)
        .addComponent(chatCompletionModelsPanel)
        .addComponent(useTextCompletionRadioButton)
        .addComponent(customTextCompletionModelPanel)
        .addComponent(textCompletionModelsPanel)
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }

  public void changeModelPanelsVisibility(boolean isCustomModelsVisible) {
    chatCompletionModelsPanel.setVisible(!isCustomModelsVisible);
    textCompletionModelsPanel.setVisible(!isCustomModelsVisible);
    customChatCompletionModelPanel.setVisible(isCustomModelsVisible);
    customTextCompletionModelPanel.setVisible(isCustomModelsVisible);
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

  public String getCustomChatCompletionModel() {
    return customChatCompletionModelField.getText();
  }

  public void setCustomChatCompletionModel(String model) {
    customChatCompletionModelField.setText(model);
  }

  public String getCustomTextCompletionModel() {
    return customTextCompletionModelField.getText();
  }

  public void setCustomTextCompletionModel(String model) {
    customTextCompletionModelField.setText(model);
  }

  private void registerRadioButtons() {
    var completionButtonGroup = new ButtonGroup();
    completionButtonGroup.add(useChatCompletionRadioButton);
    completionButtonGroup.add(useTextCompletionRadioButton);
    useChatCompletionRadioButton.addActionListener(e -> enableModelFields(true));
    useTextCompletionRadioButton.addActionListener(e -> enableModelFields(false));
  }

  private void registerFields() {
    enableModelFields(useChatCompletionRadioButton.isSelected());
  }

  private void enableModelFields(boolean isChatCompletionModel) {
    chatCompletionBaseModelComboBox.setEnabled(isChatCompletionModel);
    customChatCompletionModelField.setEnabled(isChatCompletionModel);
    textCompletionBaseModelComboBox.setEnabled(!isChatCompletionModel);
    customTextCompletionModelField.setEnabled(!isChatCompletionModel);
  }
}
