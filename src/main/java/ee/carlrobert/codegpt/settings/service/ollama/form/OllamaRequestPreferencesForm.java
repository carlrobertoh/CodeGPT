package ee.carlrobert.codegpt.settings.service.ollama.form;

import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.IntegerField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettingsState;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OllamaRequestPreferencesForm {

  private final OllamaModelPreferencesForm ollamaModelPreferencesForm;
  private final JBTextField baseHostField;
  private final IntegerField topKField;
  private final JBTextField topPField;
  private final JBTextField repeatPenaltyField;

  public OllamaRequestPreferencesForm(OllamaSettingsState settings) {
    ollamaModelPreferencesForm = new OllamaModelPreferencesForm();
    baseHostField = new JBTextField(settings.getBaseHost(), 30);
    topKField = new IntegerField();
    topKField.setColumns(12);
    topKField.setValue(settings.getTopK());
    topPField = new JBTextField(12);
    topPField.setText(String.valueOf(settings.getTopP()));
    repeatPenaltyField = new JBTextField(12);
    repeatPenaltyField.setText(String.valueOf(settings.getRepeatPenalty()));
  }

  public JPanel getForm() {
    return FormBuilder.createFormBuilder()
        .addComponent(ollamaModelPreferencesForm.getForm())
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.baseHost.label"),
            baseHostField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.ollama.baseHost.comment"))
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
            CodeGPTBundle.get("settingsConfigurable.service.llama.repeatPenalty.label"),
            repeatPenaltyField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.repeatPenalty.comment"))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public void resetForm(OllamaSettingsState state) {
    ollamaModelPreferencesForm.resetForm(state);
    baseHostField.setText(state.getBaseHost());
    topKField.setValue(state.getTopK());
    topPField.setText(String.valueOf((state.getTopP())));
    repeatPenaltyField.setText(String.valueOf((state.getRepeatPenalty())));
  }

  public OllamaModelPreferencesForm getLlamaModelPreferencesForm() {
    return ollamaModelPreferencesForm;
  }

  public String getBaseHost() {
    return baseHostField.getText();
  }

  public int getTopK() {
    return topKField.getValue();
  }

  public double getTopP() {
    return Double.parseDouble(topPField.getText());
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
