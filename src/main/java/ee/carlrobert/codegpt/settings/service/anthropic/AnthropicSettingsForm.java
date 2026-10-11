package ee.carlrobert.codegpt.settings.service.anthropic;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.ui.UIUtil;
import java.awt.Component;
import java.util.HashMap;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jetbrains.annotations.Nullable;

public class AnthropicSettingsForm {

  private final JBPasswordField apiKeyField;
  private final JBTextField apiVersionField;
  private final JBTextField modelField;
  private final JBTextField baseHostField;
  private final ComboBox<String> reasoningEffortComboBox;
  private final JBCheckBox showReasoningSummaryCheckBox;

  public AnthropicSettingsForm(AnthropicSettingsState settings) {
    apiKeyField = new JBPasswordField();
    apiKeyField.setColumns(30);
    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      var apiKey = CredentialsStore.getCredential(
          CredentialsStore.CredentialKey.AnthropicApiKey.INSTANCE
      );
      SwingUtilities.invokeLater(() -> apiKeyField.setText(apiKey));
    });
    apiVersionField = new JBTextField(settings.getApiVersion(), 35);
    modelField = new JBTextField(settings.getModel(), 35);
    baseHostField = new JBTextField(settings.getBaseHost(), 35);
    reasoningEffortComboBox = new ComboBox<>(
        new String[]{"", "low", "medium", "high", "xhigh", "max"});
    reasoningEffortComboBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
          boolean isSelected, boolean cellHasFocus) {
        var text = value == null || value.toString().isEmpty()
            ? CodeGPTBundle.get("settingsConfigurable.service.anthropic.reasoningEffort.default")
            : value.toString();
        return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
      }
    });
    reasoningEffortComboBox.setSelectedItem(settings.getReasoningEffort());
    showReasoningSummaryCheckBox = new JBCheckBox(
        CodeGPTBundle.get("settingsConfigurable.service.anthropic.showReasoningSummary.label"),
        settings.isShowReasoningSummary());
  }

  public JPanel getForm() {
    return FormBuilder.createFormBuilder()
        .addComponent(UI.PanelFactory.grid()
            .add(UI.PanelFactory.panel(apiKeyField)
                .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
                .resizeX(false)
                .withComment(
                    CodeGPTBundle.get("settingsConfigurable.service.anthropic.apiKey.comment"))
                .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked))
            .add(UI.PanelFactory.panel(apiVersionField)
                .withLabel(CodeGPTBundle.get("shared.apiVersion"))
                .withComment(CodeGPTBundle.get(
                    "settingsConfigurable.service.anthropic.apiVersion.comment"))
                .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked)
                .resizeX(false))
            .add(UI.PanelFactory.panel(modelField)
                .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.model.label"))
                .withComment(CodeGPTBundle.get(
                    "settingsConfigurable.service.anthropic.model.comment"))
                .resizeX(false))
            .add(UI.PanelFactory.panel(baseHostField)
                .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.baseHost.label"))
                .withComment("Optional: Custom API endpoint (e.g., https://api.anthropic.com)")
                .resizeX(false))
            .add(UI.PanelFactory.panel(reasoningEffortComboBox)
                .withLabel(CodeGPTBundle.get(
                    "settingsConfigurable.service.anthropic.reasoningEffort.label"))
                .withComment(CodeGPTBundle.get(
                    "settingsConfigurable.service.anthropic.reasoningEffort.comment"))
                .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked)
                .resizeX(false))
            .add(UI.PanelFactory.panel(showReasoningSummaryCheckBox)
                .withComment(CodeGPTBundle.get(
                    "settingsConfigurable.service.anthropic.showReasoningSummary.comment"))
                .resizeX(false))
            .createPanel())
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public AnthropicSettingsState getCurrentState() {
    var state = new AnthropicSettingsState();
    state.setModel(modelField.getText());
    state.setApiVersion(apiVersionField.getText());
    state.setBaseHost(baseHostField.getText());
    state.setReasoningEffort((String) reasoningEffortComboBox.getSelectedItem());
    state.setShowReasoningSummary(showReasoningSummaryCheckBox.isSelected());
    state.setFeatureReasoningEfforts(
        new HashMap<>(AnthropicSettings.getCurrentState().getFeatureReasoningEfforts()));
    return state;
  }

  public void resetForm() {
    var state = AnthropicSettings.getCurrentState();
    apiKeyField.setText(
        CredentialsStore.getCredential(CredentialsStore.CredentialKey.AnthropicApiKey.INSTANCE)
    );
    apiVersionField.setText(state.getApiVersion());
    modelField.setText(state.getModel());
    baseHostField.setText(state.getBaseHost());
    reasoningEffortComboBox.setSelectedItem(state.getReasoningEffort());
    showReasoningSummaryCheckBox.setSelected(state.isShowReasoningSummary());
  }

  public @Nullable String getApiKey() {
    var apiKey = new String(apiKeyField.getPassword());
    return apiKey.isEmpty() ? null : apiKey;
  }
}
