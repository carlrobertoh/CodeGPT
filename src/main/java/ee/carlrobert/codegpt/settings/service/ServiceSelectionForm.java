package ee.carlrobert.codegpt.settings.service;

import com.intellij.openapi.Disposable;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettingsForm;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.llama.form.LlamaSettingsForm;
import ee.carlrobert.codegpt.settings.service.ollama.OllamaSettings;
import ee.carlrobert.codegpt.settings.service.ollama.form.OllamaSettingsForm;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettingsForm;
import ee.carlrobert.codegpt.settings.service.you.YouSettings;
import ee.carlrobert.codegpt.settings.service.you.YouSettingsForm;

public class ServiceSelectionForm {

  private final OpenAISettingsForm openAISettingsForm;
  private final AzureSettingsForm azureSettingsForm;
  private final LlamaSettingsForm llamaSettingsForm;
  private final OllamaSettingsForm ollamaSettingsForm;
  private final YouSettingsForm youSettingsForm;

  public ServiceSelectionForm(Disposable parentDisposable) {
    openAISettingsForm = new OpenAISettingsForm(OpenAISettings.getCurrentState());
    azureSettingsForm = new AzureSettingsForm(AzureSettings.getCurrentState());
    llamaSettingsForm = new LlamaSettingsForm(LlamaSettings.getCurrentState());
    ollamaSettingsForm = new OllamaSettingsForm(OllamaSettings.getCurrentState());
    youSettingsForm = new YouSettingsForm(YouSettings.getCurrentState(), parentDisposable);
  }

  public OpenAISettingsForm getOpenAISettingsForm() {
    return openAISettingsForm;
  }

  public AzureSettingsForm getAzureSettingsForm() {
    return azureSettingsForm;
  }

  public LlamaSettingsForm getLlamaSettingsForm() {
    return llamaSettingsForm;
  }

  public OllamaSettingsForm getOllamaSettingsForm() {
    return ollamaSettingsForm;
  }

  public YouSettingsForm getYouSettingsForm() {
    return youSettingsForm;
  }
}
