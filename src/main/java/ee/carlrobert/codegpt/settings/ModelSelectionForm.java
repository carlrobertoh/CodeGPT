package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.util.SwingUtils;
import ee.carlrobert.openai.client.completion.CompletionModel;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionModel;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import java.util.NoSuchElementException;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

public class ModelSelectionForm {

  private static final Logger LOG = Logger.getInstance(ModelSelectionForm.class);

  private static final String modelSelectionLabel = CodeGPTBundle.get("settingsConfigurable.section.model.selectionFieldLabel");

  private final ComboBox<CompletionModel> chatCompletionBaseModelComboBox;
  private final ComboBox<CompletionModel> textCompletionBaseModelComboBox;
  private final JBRadioButton useChatCompletionRadioButton;
  private final JBRadioButton useTextCompletionRadioButton;
  private final JPanel chatCompletionModelsPanel;
  private final JPanel textCompletionModelsPanel;

  private CompletionModel findChatCompletionModelOrGetDefault(SettingsState settings) {
    try {
      return ChatCompletionModel.findByCode(settings.chatCompletionBaseModel);
    } catch (NoSuchElementException e) {
      LOG.warn("Couldn't find completion model with code: " + settings.chatCompletionBaseModel);
      return ChatCompletionModel.GPT_3_5;
    }
  }

  private CompletionModel findTextCompletionModelOrGetDefault(SettingsState settings) {
    try {
      return TextCompletionModel.findByCode(settings.textCompletionBaseModel);
    } catch (NoSuchElementException e) {
      LOG.warn("Couldn't find completion model with code: " + settings.textCompletionBaseModel);
      return TextCompletionModel.DAVINCI;
    }
  }

  public ModelSelectionForm(SettingsState settings) {
    chatCompletionBaseModelComboBox = new BaseModelComboBox(
        new ChatCompletionModel[] {
            ChatCompletionModel.GPT_3_5,
            ChatCompletionModel.GPT_3_5_16k,
            ChatCompletionModel.GPT_4,
            ChatCompletionModel.GPT_4_32k
        },
        findChatCompletionModelOrGetDefault(settings));
    chatCompletionModelsPanel = SwingUtils.createPanel(
        chatCompletionBaseModelComboBox, modelSelectionLabel, false);
    chatCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(16));
    textCompletionBaseModelComboBox = new BaseModelComboBox(
        new TextCompletionModel[] {
            TextCompletionModel.DAVINCI,
            TextCompletionModel.CURIE,
            TextCompletionModel.BABBAGE,
            TextCompletionModel.ADA,
        },
        findTextCompletionModelOrGetDefault(settings));
    textCompletionModelsPanel = SwingUtils.createPanel(textCompletionBaseModelComboBox, modelSelectionLabel);
    textCompletionModelsPanel.setBorder(JBUI.Borders.emptyLeft(16));
    useChatCompletionRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.section.model.useChatCompletionRadioButtonLabel"),
        settings.isChatCompletionOptionSelected);
    useTextCompletionRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.section.model.useTextCompletionRadioButtonLabel"),
        settings.isTextCompletionOptionSelected);

    registerFields();
    registerRadioButtons();
  }

  public JPanel getForm() {
    var form = FormBuilder.createFormBuilder()
        .addComponent(useChatCompletionRadioButton)
        .addComponent(chatCompletionModelsPanel)
        .addComponent(useTextCompletionRadioButton)
        .addComponent(textCompletionModelsPanel)
        .getPanel();
    form.setBorder(JBUI.Borders.emptyLeft(16));
    return form;
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
    textCompletionBaseModelComboBox.setEnabled(!isChatCompletionModel);
  }
}
