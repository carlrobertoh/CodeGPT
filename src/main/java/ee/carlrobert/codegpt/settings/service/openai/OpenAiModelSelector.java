package ee.carlrobert.codegpt.settings.service.openai;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EnumComboBoxModel;
import ee.carlrobert.codegpt.settings.service.util.ModelSelector;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import javax.swing.JComponent;

public class OpenAiModelSelector implements ModelSelector<OpenAIChatCompletionModel> {

  private final ComboBox<OpenAIChatCompletionModel> modelComboBox;

  public OpenAiModelSelector() {
    this.modelComboBox = new ComboBox<>(new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
  }

  @Override
  public void setSelectedModel(OpenAIChatCompletionModel model) {
    modelComboBox.setSelectedItem(model);
  }

  @Override
  public OpenAIChatCompletionModel getSelectedModel() {
    return (OpenAIChatCompletionModel) modelComboBox.getSelectedItem();
  }

  @Override
  public JComponent getComponent() {
    return modelComboBox;
  }
}
