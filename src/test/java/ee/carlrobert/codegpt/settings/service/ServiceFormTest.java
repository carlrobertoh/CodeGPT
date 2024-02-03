package ee.carlrobert.codegpt.settings.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.credentials.LlamaCredentialsManager;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import testsupport.IntegrationTest;

public class ServiceFormTest extends IntegrationTest {

  public void testLlamaServiceForm() {
    var serviceForm = ServicesSelectionForm.createLlamaServiceSectionPanel();
    var settings = LlamaSettingsState.getInstance();

    assertThat(settings.isModified(serviceForm.getSettings())).isFalse();
    settings.reset(serviceForm);
    assertThat(settings.isModified(serviceForm.getSettings())).isFalse();

    LlamaLocalSettings otherSettings = new LlamaLocalSettings();
    otherSettings.setCredentialsManager(
        new LlamaCredentialsManager(LlamaLocalSettings.CREDENTIALS_PREFIX));
    otherSettings.setModel(HuggingFaceModel.CODE_LLAMA_34B_Q3);
    serviceForm.getServerPreferencesForm().setLocalSettings(otherSettings);
    assertThat(settings.isModified(serviceForm.getSettings())).isTrue();

    settings.apply(serviceForm.getSettings());
    assertThat(settings.isModified(serviceForm.getSettings())).isFalse();
  }

  public void testOpenAiServiceForm() {
    var serviceForm = new OpenAiServiceForm();
    serviceForm.getPanel();
    var settings = OpenAISettingsState.getInstance();

    assertThat(settings.isModified(serviceForm.getSettings())).isFalse();
    settings.reset(serviceForm);
    assertThat(settings.isModified(serviceForm.getSettings())).isFalse();

    serviceForm.setModel(OpenAIChatCompletionModel.GPT_4);
    assertThat(settings.isModified(serviceForm.getSettings())).isTrue();

    settings.apply(serviceForm.getSettings());
    assertThat(settings.isModified(serviceForm.getSettings())).isFalse();
  }

  public void testAzureServiceForm() {
    var serviceForm = new AzureServiceForm();
    serviceForm.getPanel();
    var settings = AzureSettingsState.getInstance();

    assertThat(settings.isModified(serviceForm.getSettings())).isFalse();
    settings.reset(serviceForm);
    assertThat(settings.isModified(serviceForm.getSettings())).isFalse();

    serviceForm.setModel(OpenAIChatCompletionModel.GPT_4);
    assertThat(settings.isModified(serviceForm.getSettings())).isTrue();

    settings.apply(serviceForm.getSettings());
    assertThat(settings.isModified(serviceForm.getSettings())).isFalse();
  }
}
