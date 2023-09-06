package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.state.ModelSettingsState;
import ee.carlrobert.codegpt.util.SwingUtils;
import ee.carlrobert.llm.client.openai.completion.chat.OpenAIChatCompletionModel;
import ee.carlrobert.llm.client.openai.completion.text.OpenAITextCompletionModel;
import ee.carlrobert.llm.completion.CompletionModel;
import java.util.NoSuchElementException;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
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

  public ModelSelectionForm() {
    var settings = ModelSettingsState.getInstance();
    chatCompletionBaseModelComboBox = new ModelComboBox(
        new OpenAIChatCompletionModel[] {
            OpenAIChatCompletionModel.GPT_3_5,
            OpenAIChatCompletionModel.GPT_3_5_16k,
            OpenAIChatCompletionModel.GPT_4,
            OpenAIChatCompletionModel.GPT_4_32k
        },
        findChatCompletionModelOrGetDefault(settings));
    chatCompletionModelsPanel = SwingUtils.createPanel(
        chatCompletionBaseModelComboBox, modelSelectionLabel, false);
    textCompletionBaseModelComboBox = new ModelComboBox(
        new OpenAITextCompletionModel[] {
            OpenAITextCompletionModel.DAVINCI,
            OpenAITextCompletionModel.CURIE,
            OpenAITextCompletionModel.BABBAGE,
            OpenAITextCompletionModel.ADA,
        },
        findTextCompletionModelOrGetDefault(settings));
    textCompletionModelsPanel = SwingUtils.createPanel(textCompletionBaseModelComboBox, modelSelectionLabel);
    useChatCompletionRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.section.model.useChatCompletionRadioButtonLabel"),
        settings.isUseChatCompletion());
    useTextCompletionRadioButton = new JBRadioButton(
        CodeGPTBundle.get("settingsConfigurable.section.model.useTextCompletionRadioButtonLabel"),
        settings.isUseTextCompletion());

    enableModelFields(useChatCompletionRadioButton.isSelected());
    registerRadioButtons();
  }

  public JPanel getForm() {
    return FormBuilder.createFormBuilder()
        .addComponent(useChatCompletionRadioButton)
        .addComponent(withEmptyLeftBorder(chatCompletionModelsPanel))
        .addComponent(useTextCompletionRadioButton)
        .addComponent(withEmptyLeftBorder(textCompletionModelsPanel))
        .getPanel();
  }

  private void registerRadioButtons() {
    var completionButtonGroup = new ButtonGroup();
    completionButtonGroup.add(useChatCompletionRadioButton);
    completionButtonGroup.add(useTextCompletionRadioButton);
    useChatCompletionRadioButton.addActionListener(e -> enableModelFields(true));
    useTextCompletionRadioButton.addActionListener(e -> enableModelFields(false));
  }

  private void enableModelFields(boolean isChatCompletionModel) {
    chatCompletionBaseModelComboBox.setEnabled(isChatCompletionModel);
    textCompletionBaseModelComboBox.setEnabled(!isChatCompletionModel);
  }

  private CompletionModel findChatCompletionModelOrGetDefault(ModelSettingsState settings) {
    try {
      return OpenAIChatCompletionModel.findByCode(settings.getChatCompletionModel());
    } catch (NoSuchElementException e) {
      LOG.warn("Couldn't find completion model with code: " + settings.getChatCompletionModel());
      return OpenAIChatCompletionModel.GPT_3_5;
    }
  }

  private CompletionModel findTextCompletionModelOrGetDefault(ModelSettingsState settings) {
    try {
      return OpenAITextCompletionModel.findByCode(settings.getTextCompletionModel());
    } catch (NoSuchElementException e) {
      LOG.warn("Couldn't find completion model with code: " + settings.getTextCompletionModel());
      return OpenAITextCompletionModel.DAVINCI;
    }
  }

  private JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
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

  public OpenAITextCompletionModel getTextCompletionBaseModel() {
    return (OpenAITextCompletionModel) textCompletionBaseModelComboBox.getSelectedItem();
  }

  public void setTextCompletionBaseModel(String modelCode) {
    textCompletionBaseModelComboBox.setSelectedItem(OpenAITextCompletionModel.findByCode(modelCode));
  }

  public OpenAIChatCompletionModel getChatCompletionBaseModel() {
    return (OpenAIChatCompletionModel) chatCompletionBaseModelComboBox.getSelectedItem();
  }

  public void setChatCompletionBaseModel(String modelCode) {
    chatCompletionBaseModelComboBox.setSelectedItem(OpenAIChatCompletionModel.findByCode(modelCode));
  }
}
