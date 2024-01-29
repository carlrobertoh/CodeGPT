package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.PromptTemplate;
import ee.carlrobert.codegpt.settings.state.CommonSettings;
import ee.carlrobert.codegpt.ui.PromptTemplateWrapper;
import javax.swing.JPanel;

public class LlamaRemoteModelPreferencesForm {

  private final PromptTemplateWrapper promptTemplateWrapper;
  private final CommonSettings settings;

  public LlamaRemoteModelPreferencesForm(CommonSettings settings) {
    this.settings = settings;
    promptTemplateWrapper = new PromptTemplateWrapper(settings.getPromptTemplate(), true);
  }

  public JPanel getForm() {
    var customModelHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get("settingsConfigurable.service.llama.customModelPath.comment"),
        true);
    customModelHelpText.setBorder(JBUI.Borders.empty(0, 4));

    return FormBuilder.createFormBuilder()
        .addLabeledComponent(CodeGPTBundle.get("shared.promptTemplate"), promptTemplateWrapper)
        .addComponentToRightColumn(promptTemplateWrapper.getPromptTemplateHelpText())
        .addVerticalGap(4)
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public void enableFields(boolean enabled) {
    promptTemplateWrapper.setEnabled(enabled);
  }

  public void setPromptTemplate(PromptTemplate promptTemplate) {
    promptTemplateWrapper.setPromptTemplate(promptTemplate);
  }

  public PromptTemplate getPromptTemplate() {
    return promptTemplateWrapper.getPrompTemplate();
  }

}
