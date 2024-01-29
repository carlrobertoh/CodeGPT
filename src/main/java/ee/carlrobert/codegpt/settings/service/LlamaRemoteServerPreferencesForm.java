package ee.carlrobert.codegpt.settings.service;

import static ee.carlrobert.codegpt.ui.UIUtil.createComment;
import static ee.carlrobert.codegpt.ui.UIUtil.withEmptyLeftBorder;

import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.RemoteSettings;
import ee.carlrobert.codegpt.ui.UIUtil;
import javax.swing.JComponent;

public class LlamaRemoteServerPreferencesForm {

  private final JBTextField baseHostField;
  private final LlamaRemoteModelPreferencesForm llamaRemoteModelPreferencesForm;
  private final JBPasswordField apiKeyField;

  public LlamaRemoteServerPreferencesForm() {
    var remoteSettings = LlamaSettingsState.getInstance().getRemoteSettings();
    apiKeyField = new JBPasswordField();
    apiKeyField.setColumns(30);
    apiKeyField.setText(LlamaCredentialsManager.getInstance().getApiKey());

    baseHostField = new JBTextField(remoteSettings.getBaseHost(), 30);

    llamaRemoteModelPreferencesForm = new LlamaRemoteModelPreferencesForm(remoteSettings);
  }

  public JComponent getForm() {
    var apiKeyFieldPanel = UI.PanelFactory.panel(apiKeyField)
        .withLabel(CodeGPTBundle.get("settingsConfigurable.shared.apiKey.label"))
        .resizeX(false)
        .withComment(
            CodeGPTBundle.get("settingsConfigurable.service.llama.apiKey.comment"))
        .withCommentHyperlinkListener(UIUtil::handleHyperlinkClicked)
        .createPanel();
    return withEmptyLeftBorder(FormBuilder.createFormBuilder()
        .addLabeledComponent(
            CodeGPTBundle.get("settingsConfigurable.service.llama.baseHost.label"),
            baseHostField)
        .addComponentToRightColumn(
            createComment("settingsConfigurable.service.llama.baseHost.comment"))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.llama.modelPreferences.title")))
        .addComponent(withEmptyLeftBorder(llamaRemoteModelPreferencesForm.getForm()))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.shared.authentication.title")))
        .addComponent(withEmptyLeftBorder(apiKeyFieldPanel))
        .getPanel());
  }

  public void setFormEnabled(boolean enabled) {
    llamaRemoteModelPreferencesForm.enableFields(enabled);
    baseHostField.setEnabled(enabled);
    apiKeyField.setEnabled(enabled);
  }

  public void setRemoteSettings(RemoteSettings remoteSettings){
    llamaRemoteModelPreferencesForm.setPromptTemplate(remoteSettings.getPromptTemplate());
    baseHostField.setText(remoteSettings.getBaseHost());
    apiKeyField.setText(LlamaCredentialsManager.getInstance().getApiKey());
  }

  public RemoteSettings getRemoteSettings(){
    return new RemoteSettings(
        llamaRemoteModelPreferencesForm.getPromptTemplate(),
        baseHostField.getText()
    );
  }


}
