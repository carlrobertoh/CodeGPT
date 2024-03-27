package testsupport.mixin;

import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.AZURE_OPENAI_API_KEY;
import static ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey.OPENAI_API_KEY;

import ee.carlrobert.codegpt.credentials.CredentialsStore;
import ee.carlrobert.codegpt.settings.GeneralSettings;
import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;

public interface ShortcutsTestMixin {

  default void useOpenAIService() {
    GeneralSettings.getCurrentState().setSelectedService(ServiceType.OPENAI);
    CredentialsStore.INSTANCE.setCredential(OPENAI_API_KEY, "TEST_API_KEY");
    OpenAISettings.getCurrentState().setModel("gpt-4");
  }

  default void useAzureService() {
    GeneralSettings.getCurrentState().setSelectedService(ServiceType.AZURE);
    CredentialsStore.INSTANCE.setCredential(AZURE_OPENAI_API_KEY, "TEST_API_KEY");
    var azureSettings = AzureSettings.getCurrentState();
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
