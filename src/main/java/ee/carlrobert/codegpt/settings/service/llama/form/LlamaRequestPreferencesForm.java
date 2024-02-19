package ee.carlrobert.codegpt.settings.service.llama.form;

import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettingsState;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LlamaRequestPreferencesForm {

  private final IntegerField topKField;
  private final JBTextField topPField;
  private final JBTextField minPField;
  private final JBTextField repeatPenaltyField;

  public LlamaRequestPreferencesForm(LlamaSettingsState llamaSettings) {
    topKField = new IntegerField();
    topKField.setColumns(12);
    topKField.setValue(llamaSettings.getTopK());
    topPField = new JBTextField(12);
    topPField.setText(String.valueOf(llamaSettings.getTopP()));
    minPField = new JBTextField(12);
    minPField.setText(String.valueOf(llamaSettings.getMinP()));
    repeatPenaltyField = new JBTextField(12);
    repeatPenaltyField.setText(String.valueOf(llamaSettings.getRepeatPenalty()));
  }

  public JPanel getForm() {
    return FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.topK.label"),
            topKField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.topK.comment"))
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.topP.label"),
            topPField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.topP.comment"))
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.minP.label"),
            minPField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.minP.comment"))
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.repeatPenalty.label"),
            repeatPenaltyField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.repeatPenalty.comment"))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public void resetForm(LlamaSettingsState state) {
    topKField.setValue(state.getTopK());
    topPField.setText(String.valueOf((state.getTopP())));
    minPField.setText(String.valueOf((state.getMinP())));
    repeatPenaltyField.setText(String.valueOf((state.getRepeatPenalty())));
  }

  public int getTopK() {
    return topKField.getValue();
  }

  public double getTopP() {
    return Double.parseDouble(topPField.getText());
  }

  public double getMinP() {
    return Double.parseDouble(minPField.getText());
  }

  public double getRepeatPenalty() {
    return Double.parseDouble(repeatPenaltyField.getText());
  }

  private JLabel createComment(String messageKey) {
    var comment = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get(messageKey), true);
    comment.setBorder(JBUI.Borders.empty(0, 4));
    return comment;
  }
}
