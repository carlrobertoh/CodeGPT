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
    GeneralSettings.getCurrentState().getSelectedPersona().setServiceType(ServiceType.OPENAI);
    OpenAICredentialManager.getInstance().setCredential("TEST_API_KEY");
    OpenAISettings.getCurrentState().setModel("gpt-4");
  }

  default void useAzureService() {
    GeneralSettings.getCurrentState().getSelectedPersona().setServiceType(ServiceType.AZURE);
    AzureCredentialsManager.getInstance().setApiKey("TEST_API_KEY");
    var azureSettings = AzureSettings.getCurrentState();
    azureSettings.setResourceName("TEST_RESOURCE_NAME");
    azureSettings.setApiVersion("TEST_API_VERSION");
    azureSettings.setDeploymentId("TEST_DEPLOYMENT_ID");
  }

  default void useYouService() {
    GeneralSettings.getCurrentState().getSelectedPersona().setServiceType(ServiceType.YOU);
  }

  default void useLlamaService() {
    GeneralSettings.getCurrentState().getSelectedPersona().setServiceType(ServiceType.LLAMA_CPP);
    LlamaSettings.getCurrentState().setServerPort(null);
  }
}
