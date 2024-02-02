package testsupport.mixin;

import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.GeneralSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;

public interface ShortcutsTestMixin {

  default void useOpenAIService() {
    GeneralSettingsState.getInstance().setSelectedService(ServiceType.OPENAI);
    OpenAISettingsState openAISettings = OpenAISettingsState.getInstance();
    openAISettings.getCredentialsManager().setApiKey("TEST_API_KEY");
    openAISettings.setModel("gpt-4");
    openAISettings.setBaseHost(null);
  }

  default void useAzureService() {
    GeneralSettingsState.getInstance().setSelectedService(ServiceType.AZURE);
    var azureSettings = AzureSettingsState.getInstance();
    azureSettings.getCredentialsManager().setApiKey("TEST_API_KEY");
    azureSettings.setBaseHost(null);
    azureSettings.setResourceName("TEST_RESOURCE_NAME");
    azureSettings.setApiVersion("TEST_API_VERSION");
    azureSettings.setDeploymentId("TEST_DEPLOYMENT_ID");
  }

  default void useYouService() {
    GeneralSettingsState.getInstance().setSelectedService(ServiceType.YOU);
  }

  default void useLlamaService() {
    GeneralSettingsState.getInstance().setSelectedService(ServiceType.LLAMA_CPP);
    LlamaSettingsState llamaSettingsState = LlamaSettingsState.getInstance();
    llamaSettingsState.setRunLocalServer(true);
    var localSettings = llamaSettingsState.getLocalSettings();
    localSettings.setServerPort(null);
  }
}
