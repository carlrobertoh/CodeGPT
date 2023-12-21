package testsupport.mixin;

import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;

public interface ShortcutsTestMixin {

  default void useOpenAIService() {
    SettingsState.getInstance().setSelectedService(ServiceType.OPENAI);
    OpenAICredentialsManager.getInstance().setApiKey("TEST_API_KEY");
    OpenAISettingsState.getInstance().setModel("gpt-4");
    OpenAISettingsState.getInstance().setBaseHost(null);
  }

  default void useAzureService() {
    SettingsState.getInstance().setSelectedService(ServiceType.AZURE);
    AzureCredentialsManager.getInstance().setApiKey("TEST_API_KEY");
    var azureSettings = AzureSettingsState.getInstance();
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
