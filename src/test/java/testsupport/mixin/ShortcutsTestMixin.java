package testsupport.mixin;

import ee.carlrobert.codegpt.settings.service.ServiceType;
import ee.carlrobert.codegpt.settings.state.AzureSettings;
import ee.carlrobert.codegpt.settings.state.GeneralSettingsState;
import ee.carlrobert.codegpt.settings.state.LlamaSettings;
import ee.carlrobert.codegpt.settings.state.OpenAISettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.openai.OpenAISettingsState;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;

public interface ShortcutsTestMixin {

  default void useOpenAIService() {
    GeneralSettingsState.getInstance().setSelectedService(ServiceType.OPENAI);
    OpenAISettingsState openAISettings = OpenAISettings.getInstance().getState();
    openAISettings.getCredentials().setApiKey("TEST_API_KEY");
    openAISettings.setModel(OpenAIChatCompletionModel.GPT_4);
    openAISettings.setBaseHost(null);
  }

  default void useAzureService() {
    GeneralSettingsState.getInstance().setSelectedService(ServiceType.AZURE);
    var azureSettings = AzureSettings.getInstance().getState();
    azureSettings.getCredentials().setApiKey("TEST_API_KEY");
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
    LlamaSettingsState llamaSettingsState = LlamaSettings.getInstance().getState();
    llamaSettingsState.setRunLocalServer(true);
    var localSettings = llamaSettingsState.getLocalSettings();
    localSettings.setServerPort(null);
  }
}
