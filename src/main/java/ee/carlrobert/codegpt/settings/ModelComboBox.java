package ee.carlrobert.codegpt.settings;

import com.intellij.openapi.ui.ComboBox;
import ee.carlrobert.llm.completion.CompletionModel;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class ModelComboBox extends ComboBox<CompletionModel> {

  public ModelComboBox(CompletionModel[] options, CompletionModel selectedModel) {
    super(options);
    setSelectedItem(selectedModel);
    setRenderer(getBasicComboBoxRenderer());
  }

  private BasicComboBoxRenderer getBasicComboBoxRenderer() {
    return new BasicComboBoxRenderer() {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null) {
          CompletionModel model = (CompletionModel) value;
          setText(model.getDescription());
        }
        return this;
      }
    };
  }
}
