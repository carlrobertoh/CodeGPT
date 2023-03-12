package ee.carlrobert.codegpt.ide.settings;

import com.intellij.openapi.ui.ComboBox;
import ee.carlrobert.codegpt.client.BaseModel;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class BaseModelComboBox extends ComboBox<BaseModel> {

  public BaseModelComboBox(BaseModel[] options, BaseModel selectedModel) {
    super(options);
    setSelectedItem(selectedModel);
    setRenderer(getBasicComboBoxRenderer());
  }

  private BasicComboBoxRenderer getBasicComboBoxRenderer() {
    return new BasicComboBoxRenderer() {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value != null) {
          BaseModel model = (BaseModel) value;
          setText(model.getDescription());
        }
        return this;
      }
    };
  }
}
