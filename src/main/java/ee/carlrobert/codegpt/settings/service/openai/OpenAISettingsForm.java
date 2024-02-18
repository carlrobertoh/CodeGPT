package ee.carlrobert.codegpt.settings.service.openai;

import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.ui.UIUtil;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import org.jetbrains.annotations.NotNull;

public class OpenAISettingsForm {

  private final JBPasswordField openAIApiKeyField;
  private final JBTextField openAICustomModelField;
  private final JBTextField openAIBaseHostField;
  private final JBTextField openAIPathField;
  private final JBTextField openAIOrganizationField;
  private final ComboBox<OpenAIChatCompletionModel> openAICompletionModelComboBox;

  public OpenAISettingsForm(OpenAISettingsState settings) {
    openAIApiKeyField = new JBPasswordField();
    openAIApiKeyField.setColumns(30);
    openAIApiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
    openAIBaseHostField = new JBTextField(settings.getBaseHost(), 30);
    openAIPathField = new JBTextField(settings.getPath(), 30);
    openAIOrganizationField = new JBTextField(settings.getOrganization(), 30);
    openAICompletionModelComboBox = new ComboBox<>(
        new EnumComboBoxModel<>(OpenAIChatCompletionModel.class));
    openAICompletionModelComboBox.setEnabled(settings.getCustomModel().isEmpty());
    openAICompletionModelComboBox.setSelectedItem(
        OpenAIChatCompletionModel.findByCode(settings.getModel()));
    openAICustomModelField = new JBTextField(settings.getCustomModel(), 20);
    openAICustomModelField.getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent e) {
        openAICompletionModelComboBox.setEnabled(openAICustomModelField.getText().isEmpty());
      }
    });
  }

  public JPanel getForm() {
    var requestConfigurationPanel = UI.PanelFactory.grid()
        .add(UI.PanelFactory.panel(openAICompletionModelComboBox)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.model.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(openAICustomModelField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.openai.customModel.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(openAIOrganizationField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.service.openai.organization.label"))
            .resizeX(false)
            .withComment(CodeGPTBundle.get(
                "settingsConfigurable.section.openai.organization.comment")))
        .add(UI.PanelFactory.panel(openAIBaseHostField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.baseHost.label"))
            .resizeX(false))
        .add(UI.PanelFactory.panel(openAIPathField)
            .withLabel(CodeGPTBundle.get(
                "settingsConfigurable.shared.path.label"))
            .resizeX(false))
        .createPanel();

    var apiKeyFieldPanel = UI.PanelFactory.panel(openAIApiKeyField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
        .resizeX(false)
        .withComment(
            CodeGPTBundle.get("settingsConfigurable.service.openai.apiKey.comment"))
        .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked)
        .createPanel();

    return FormBuilder.createFormBuilder()
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")))
        .addComponent(withEmptyLeftBorder(apiKeyFieldPanel))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.requestConfiguration.title")))
        .addComponent(withEmptyLeftBorder(requestConfigurationPanel))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  public @Nullable String getApiKey() {
    var apiKey = new String(openAIApiKeyField.getPassword());
    return apiKey.isEmpty() ? null : apiKey;
  }

  public String getModel() {
    return ((OpenAIChatCompletionModel) (openAICompletionModelComboBox.getModel()
        .getSelectedItem()))
        .getCode();
  }

  public OpenAISettingsState getCurrentState() {
    var state = new OpenAISettingsState();
    state.setOrganization(openAIOrganizationField.getText());
    state.setBaseHost(openAIBaseHostField.getText());
    state.setPath(openAIPathField.getText());
    state.setCustomModel(openAICustomModelField.getText());
    state.setModel(getModel());
    return state;
  }

  public void resetForm() {
    var state = OpenAISettings.getCurrentState();
    openAIApiKeyField.setText(OpenAICredentialManager.getInstance().getCredential());
    openAIOrganizationField.setText(state.getOrganization());
    openAICustomModelField.setText(state.getCustomModel());
    openAIBaseHostField.setText(state.getBaseHost());
    openAIPathField.setText(state.getPath());
    openAICompletionModelComboBox.setSelectedItem(
        OpenAIChatCompletionModel.findByCode(state.getModel()));
  }
}
