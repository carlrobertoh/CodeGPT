package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationNotifier;
import ee.carlrobert.codegpt.settings.service.llama.LlamaCppServiceForm;
import ee.carlrobert.codegpt.settings.service.llama.ollama.OllamaServiceForm;
import ee.carlrobert.codegpt.settings.state.LlamaCppSettingsState;
import ee.carlrobert.codegpt.settings.state.OllamaSettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ServicesSelectionForm {

  private static final Logger LOG = Logger.getInstance(ServicesSelectionForm.class);

  private final Disposable parentDisposable;

  private final OpenAiServiceForm openAIServiceSectionPanel;
  private final AzureServiceForm azureServiceSectionPanel;

  private final JPanel youServiceSectionPanel;
  private final JBCheckBox displayWebSearchResultsCheckBox;

  private final LlamaCppServiceForm llamaCppServiceSectionPanel;
  private final OllamaServiceForm ollamaServiceSectionPanel;

  public ServicesSelectionForm(Disposable parentDisposable) {
    this.parentDisposable = parentDisposable;

    displayWebSearchResultsCheckBox = new JBCheckBox(
        CodeGPTBundle.get("settingsConfigurable.service.you.displayResults.label"),
        YouSettingsState.getInstance().isDisplayWebSearchResults());

    openAIServiceSectionPanel = new OpenAiServiceForm();
    azureServiceSectionPanel = new AzureServiceForm();
    youServiceSectionPanel = createYouServiceSectionPanel();
    llamaCppServiceSectionPanel = createLlamaServiceSectionPanel();
    ollamaServiceSectionPanel = createOllamaServiceSectionPanel();

    ApplicationManager.getApplication()
        .getMessageBus()
        .connect()
        .subscribe(AuthenticationNotifier.AUTHENTICATION_TOPIC,
            (AuthenticationNotifier) () -> displayWebSearchResultsCheckBox.setEnabled(true));
  }

  static LlamaCppServiceForm createLlamaServiceSectionPanel() {
    var settings = LlamaCppSettingsState.getInstance();
    return new LlamaCppServiceForm(settings);
  }

  static OllamaServiceForm createOllamaServiceSectionPanel() {
    return new OllamaServiceForm(OllamaSettingsState.getInstance());
  }

  private JPanel createYouServiceSectionPanel() {
    return FormBuilder.createFormBuilder()
        .addComponent(new YouServiceSelectionForm(parentDisposable))
        .addComponent(new TitledSeparator(
            CodeGPTBundle.get("settingsConfigurable.service.you.chatPreferences.title")))
        .addComponent(withEmptyLeftBorder(displayWebSearchResultsCheckBox))
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private JComponent withEmptyLeftBorder(JComponent component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
  }

  public void setDisplayWebSearchResults(boolean displayWebSearchResults) {
    displayWebSearchResultsCheckBox.setSelected(displayWebSearchResults);
  }

  public boolean isDisplayWebSearchResults() {
    return displayWebSearchResultsCheckBox.isSelected();
  }

  public OpenAiServiceForm getOpenAIServiceSectionPanel() {
    return openAIServiceSectionPanel;
  }

  public AzureServiceForm getAzureServiceSectionPanel() {
    return azureServiceSectionPanel;
  }

  public JPanel getYouServiceSectionPanel() {
    return youServiceSectionPanel;
  }

  public LlamaCppServiceForm getLlamaCppServiceSectionPanel() {
    return llamaCppServiceSectionPanel;
  }

  public OllamaServiceForm getOllamaServiceSectionPanel() {
    return ollamaServiceSectionPanel;
  }


}
