package ee.carlrobert.codegpt.settings.service.ollama.form.model;

import static java.lang.String.format;

import com.intellij.openapi.ui.ComboBox;
import java.awt.Component;
import javax.swing.ComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.jetbrains.annotations.NotNull;

public class OllamaModelQuantizationComboBox extends ComboBox<OllamaChildModel> {

  public OllamaModelQuantizationComboBox(
      @NotNull ComboBoxModel<OllamaChildModel> model) {
    super(model);
  }

  @Override
  public ListCellRenderer<? super OllamaChildModel> getRenderer() {
    return new ListCellRenderer<OllamaChildModel>() {
      @Override
      public Component getListCellRendererComponent(JList<? extends OllamaChildModel> list,
          OllamaChildModel value, int index, boolean isSelected, boolean cellHasFocus) {
        return new JLabel(
            format("%d-bit precision", value.getQuantization()));
      }
    };
  }
}
