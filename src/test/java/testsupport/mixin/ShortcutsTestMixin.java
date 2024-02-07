package testsupport.mixin;

import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialManager;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;

public interface ShortcutsTestMixin {

  default void useOpenAIService() {
    GeneralSettings.getCurrentState().setSelectedService(ServiceType.OPENAI);
    OpenAICredentialManager.getInstance().setCredential("TEST_API_KEY");
    OpenAISettings.getCurrentState().setModel("gpt-4");
    OpenAISettings.getCurrentState().setBaseHost(null);
  }

  default void useAzureService() {
    GeneralSettings.getCurrentState().setSelectedService(ServiceType.AZURE);
    AzureCredentialsManager.getInstance().setApiKey("TEST_API_KEY");
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
