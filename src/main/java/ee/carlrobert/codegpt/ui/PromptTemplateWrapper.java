package ee.carlrobert.codegpt.ui;

import com.intellij.icons.AllIcons.General;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.llama.PromptTemplate;
import ee.carlrobert.codegpt.conversations.message.Message;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.text.StringEscapeUtils;

public class PromptTemplateWrapper extends JPanel {

  private final JBLabel promptTemplateHelpIcon;
  private final ComboBox<PromptTemplate> promptTemplateComboBox;
  private final JLabel promptTemplateHelpText;

  public PromptTemplateWrapper(PromptTemplate initiallySelectedTemplate, boolean enabled) {
    super(new FlowLayout(FlowLayout.LEADING, 0, 0));
    promptTemplateComboBox = new ComboBox<>(new EnumComboBoxModel<>(PromptTemplate.class));
    promptTemplateComboBox.setSelectedItem(initiallySelectedTemplate);
    promptTemplateComboBox.setEnabled(enabled);
    promptTemplateComboBox.addItemListener(item -> {
      var template = (PromptTemplate) item.getItem();
      updatePromptTemplateHelpTooltip(template);
    });
    add(promptTemplateComboBox);
    add(Box.createHorizontalStrut(8));

    promptTemplateHelpIcon = new JBLabel(General.ContextHelp);
    add(promptTemplateHelpIcon);

    promptTemplateHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get("settingsConfigurable.service.llama.promptTemplate.comment"),
        true);
    promptTemplateHelpText.setBorder(JBUI.Borders.empty(0, 4));
    updatePromptTemplateHelpTooltip(initiallySelectedTemplate);
  }

  public void setPromptTemplate(PromptTemplate promptTemplate) {
    promptTemplateComboBox.setSelectedItem(promptTemplate);
  }

  public PromptTemplate getPrompTemplate() {
    return (PromptTemplate) promptTemplateComboBox.getSelectedItem();
  }

  public JLabel getPromptTemplateHelpText() {
    return promptTemplateHelpText;
  }

  private void updatePromptTemplateHelpTooltip(PromptTemplate template) {
    promptTemplateHelpIcon.setToolTipText(null);

    var prompt = template.buildPrompt(
        "SYSTEM_PROMPT",
        "USER_PROMPT",
        List.of(new Message("PREV_PROMPT", "PREV_RESPONSE")));
    var htmlDescription = Arrays.stream(prompt.split("\n"))
        .map(StringEscapeUtils::escapeHtml4)
        .collect(Collectors.joining("<br>"));

    new HelpTooltip()
        .setTitle(template.toString())
        .setDescription("<html><p>" + htmlDescription + "</p></html>")
        .installOn(promptTemplateHelpIcon);
  }
}
