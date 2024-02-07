package testsupport.mixin;

import ee.carlrobert.codegpt.credentials.AzureCredentialManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.AzureSettings;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;

public interface ShortcutsTestMixin {

  default void useOpenAIService() {
    SettingsState.getInstance().setSelectedService(ServiceType.OPENAI);
    OpenAICredentialManager.getInstance().setCredential("TEST_API_KEY");
    OpenAISettingsState.getInstance().setModel("gpt-4");
    OpenAISettingsState.getInstance().setBaseHost(null);
  }

  default void useAzureService() {
    SettingsState.getInstance().setSelectedService(ServiceType.AZURE);
    AzureCredentialManager.getInstance().setApiKey("TEST_API_KEY");
    var azureSettings = AzureSettings.getInstance().getState();
    azureSettings.setBaseHost(null);
    azureSettings.setResourceName("TEST_RESOURCE_NAME");
    azureSettings.setApiVersion("TEST_API_VERSION");
    azureSettings.setDeploymentId("TEST_DEPLOYMENT_ID");
  }

  default void useYouService() {
    SettingsState.getInstance().setSelectedService(ServiceType.YOU);
  }

  default void useLlamaService() {
    SettingsState.getInstance().setSelectedService(ServiceType.LLAMA_CPP);
    LlamaSettingsState.getInstance().setServerPort(null);
  }
}
