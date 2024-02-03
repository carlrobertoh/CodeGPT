package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.completions.llama.LlamaServerAgent;
import ee.carlrobert.codegpt.completions.you.auth.AuthenticationNotifier;
import ee.carlrobert.codegpt.settings.service.llama.LlamaLocalOrRemoteServiceForm;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.YouSettingsState;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ServicesSelectionForm {

  private static final Logger LOG = Logger.getInstance(ServicesSelectionForm.class);

  private final Disposable parentDisposable;

  private final OpenAiServiceForm openAIServiceSectionPanel;
  private final AzureServiceForm azureServiceSectionPanel;

  private final JPanel youServiceSectionPanel;
  private final JBCheckBox displayWebSearchResultsCheckBox;

  private final LlamaServiceForm llamaServiceSectionPanel;

  public ServicesSelectionForm(Disposable parentDisposable) {
    this.parentDisposable = parentDisposable;

    displayWebSearchResultsCheckBox = new JBCheckBox(
        CodeGPTBundle.get("settingsConfigurable.service.you.displayResults.label"),
        YouSettingsState.getInstance().isDisplayWebSearchResults());

    openAIServiceSectionPanel = new OpenAiServiceForm();
    azureServiceSectionPanel = new AzureServiceForm();
    youServiceSectionPanel = createYouServiceSectionPanel();
    llamaServiceSectionPanel = createLlamaServiceSectionPanel();

    ApplicationManager.getApplication()
        .getMessageBus()
        .connect()
        .subscribe(AuthenticationNotifier.AUTHENTICATION_TOPIC,
            (AuthenticationNotifier) () -> displayWebSearchResultsCheckBox.setEnabled(true));
  }

  private LlamaServiceForm createLlamaServiceSectionPanel() {
    var settings = LlamaSettingsState.getInstance();
    return new LlamaServiceForm(
        new LlamaLocalOrRemoteServiceForm(settings,
            ApplicationManager.getApplication().getService(LlamaServerAgent.class),
            ServiceType.LLAMA_CPP) {
          @Override
          public boolean isModelExists(HuggingFaceModel model) {
            return FileUtil.exists(
                CodeGPTPlugin.getLlamaModelsPath() + File.separator + model.getModelFileName());
          }
        }, settings.getRequestSettings());
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

  public LlamaServiceForm getLlamaServiceSectionPanel() {
    return llamaServiceSectionPanel;
  }

}
