package testsupport.mixin;

import ee.carlrobert.codegpt.credentials.AzureCredentialManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.AzureSettings;
import ee.carlrobert.codegpt.settings.state.GeneralSettings;
import ee.carlrobert.codegpt.settings.state.LlamaSettings;
import ee.carlrobert.codegpt.settings.state.OpenAISettings;

public interface ShortcutsTestMixin {

  default void useOpenAIService() {
    GeneralSettings.getCurrentState().setSelectedService(ServiceType.OPENAI);
    OpenAICredentialManager.getInstance().setCredential("TEST_API_KEY");
    OpenAISettings.getCurrentState().setModel("gpt-4");
    OpenAISettings.getCurrentState().setBaseHost(null);
  }

  default void useAzureService() {
    GeneralSettings.getCurrentState().setSelectedService(ServiceType.AZURE);
    AzureCredentialManager.getInstance().setApiKey("TEST_API_KEY");
    var azureSettings = AzureSettings.getCurrentState();
    azureSettings.setBaseHost(null);
    azureSettings.setResourceName("TEST_RESOURCE_NAME");
    azureSettings.setApiVersion("TEST_API_VERSION");
    azureSettings.setDeploymentId("TEST_DEPLOYMENT_ID");
  }

  default void useYouService() {
    GeneralSettings.getCurrentState().setSelectedService(ServiceType.YOU);
  }

  default void useLlamaService() {
    GeneralSettings.getCurrentState().setSelectedService(ServiceType.LLAMA_CPP);
    LlamaSettings.getCurrentState().setServerPort(null);
  }
}
