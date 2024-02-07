package ee.carlrobert.codegpt.settings.service.llama.form;

import com.intellij.icons.AllIcons.General;
import com.intellij.ide.HelpTooltip;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.text.StringEscapeUtils;

public abstract class BasePromptTemplatePanel<T extends Enum<T>> extends JPanel {

  private final JBLabel promptTemplateHelpIcon;
  protected final ComboBox<T> promptTemplateComboBox;
  private final JLabel promptTemplateHelpText;
  private final Class<T> enumClass;

  public BasePromptTemplatePanel(
      Class<T> enumClass,
      T initiallySelectedTemplate,
      boolean enabled,
      String helpTextKey) {
    super(new FlowLayout(FlowLayout.LEADING, 0, 0));
    this.enumClass = enumClass;
    promptTemplateComboBox = new ComboBox<>(new EnumComboBoxModel<>(enumClass));
    promptTemplateComboBox.setSelectedItem(initiallySelectedTemplate);
    promptTemplateComboBox.setEnabled(enabled);
    promptTemplateComboBox.addItemListener(
        item -> updatePromptTemplateHelpTooltip(enumClass.cast(item.getItem())));
    add(promptTemplateComboBox);
    add(Box.createHorizontalStrut(8));

    promptTemplateHelpIcon = new JBLabel(General.ContextHelp);
    add(promptTemplateHelpIcon);

    promptTemplateHelpText = ComponentPanelBuilder.createCommentComponent(
        CodeGPTBundle.get(helpTextKey),
        true);
    promptTemplateHelpText.setBorder(JBUI.Borders.empty(0, 4));
    updatePromptTemplateHelpTooltip(initiallySelectedTemplate);
  }

  public void setPromptTemplate(T promptTemplate) {
    promptTemplateComboBox.setSelectedItem(promptTemplate);
  }

  public T getPromptTemplate() {
    Object selectedItem = promptTemplateComboBox.getSelectedItem();
    if (!enumClass.isInstance(selectedItem)) {
      throw new IllegalStateException("Selected item is not an instance of the expected type.");
    }
    return enumClass.cast(selectedItem);
  }

  public JLabel getPromptTemplateHelpText() {
    return promptTemplateHelpText;
  }

  protected abstract String buildPromptDescription(T template);

  private void updatePromptTemplateHelpTooltip(T template) {
    promptTemplateHelpIcon.setToolTipText(null);

    var htmlDescription = Arrays.stream(buildPromptDescription(template).split("\n"))
        .map(StringEscapeUtils::escapeHtml4)
        .collect(Collectors.joining("<br>"));

    new HelpTooltip()
        .setTitle(template.toString())
        .setDescription("<html><p>" + htmlDescription + "</p></html>")
        .installOn(promptTemplateHelpIcon);
  }
}
