package ee.carlrobert.codegpt.settings.state;

import static ee.carlrobert.codegpt.CodeGPTPlugin.CODEGPT_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static testsupport.TestUtil.assertCredentials;
import static testsupport.TestUtil.assertPassword;

import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.plugins.PluginSetBuilder;
import com.intellij.ide.plugins.RawPluginDescriptor;
import ee.carlrobert.codegpt.TestPasswordSafe;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.credentials.CredentialsService;
import ee.carlrobert.codegpt.credentials.PasswordCredentials;
import ee.carlrobert.codegpt.credentials.manager.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.LlamaCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.OpenAICredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.YouCredentialsManager;
import ee.carlrobert.codegpt.settings.state.azure.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.openai.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.you.YouSettingsState;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.nio.file.Path;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ServiceSettingsTest {

  private TestPasswordSafe passwordSafe;
  private CredentialsService credentialsService;

  @Before
  public void setUp() {
    passwordSafe = new TestPasswordSafe();
    credentialsService = new CredentialsService(passwordSafe);
  }

  @Test
  public void testLlamaSettings() {
    mockCodeGptPluginPath();
    LlamaLocalCredentialsManager localCredentialsManager = new LlamaLocalCredentialsManager(
        credentialsService);
    LlamaCredentialsManager remoteCredentialsManager = new LlamaCredentialsManager(
        credentialsService);
    LlamaSettings settings = new LlamaSettings(localCredentialsManager,
        remoteCredentialsManager);

    assertCredentials(settings.getState().getLocalSettings(), null);
    assertCredentials(localCredentialsManager.getCredentials(), null);
    assertCredentials(remoteCredentialsManager.getCredentials(), null);

    LlamaSettingsState expectedSettings = new LlamaSettingsState();
    // Port is always initialized randomly, therefore reset
    expectedSettings.getLocalSettings()
        .setServerPort(settings.getState().getLocalSettings().getServerPort());
    assertThat(settings.getState().isModified(expectedSettings)).isFalse();

    LlamaLocalSettings expectedLocalSettings = expectedSettings.getLocalSettings();
    HuggingFaceModel expectedModel = HuggingFaceModel.WIZARD_CODER_PYTHON_7B_Q3;
    expectedLocalSettings.setModel(expectedModel);
    String expectedApiKey = "apikey";
    expectedLocalSettings.getCredentials().setApiKey(expectedApiKey);
    assertThat(settings.getState().isModified(expectedSettings)).isTrue();

    settings.apply(expectedSettings);

    assertThat(settings.getState().isModified(expectedSettings)).isFalse();
    assertThat(settings.getState().getLocalSettings().getModel()).isEqualTo(expectedModel);
    assertCredentials(settings.getState().getLocalSettings(), expectedApiKey);
    assertCredentials(localCredentialsManager.getCredentials(), expectedApiKey);
    assertCredentials(remoteCredentialsManager.getCredentials(), null);

    expectedSettings = new LlamaSettingsState();
    // Port is always initialized randomly, therefore reset
    expectedSettings.getLocalSettings()
        .setServerPort(settings.getState().getLocalSettings().getServerPort());
    expectedSettings.getRemoteSettings().getCredentials().setApiKey(expectedApiKey);

    settings.apply(expectedSettings);

    assertThat(settings.getState().isModified(expectedSettings)).isFalse();
    assertCredentials(settings.getState().getRemoteSettings(), expectedApiKey);
    assertCredentials(remoteCredentialsManager.getCredentials(), expectedApiKey);
  }

  @Test
  public void testOpenAiSettings() {
    OpenAICredentialsManager credentialsManager = new OpenAICredentialsManager(credentialsService);
    OpenAISettings settings = new OpenAISettings(credentialsManager);

    assertCredentials(settings.getState(), null);
    assertCredentials(credentialsManager.getCredentials(), null);

    OpenAISettingsState expectedSettings = new OpenAISettingsState();
    assertThat(settings.getState().isModified(expectedSettings)).isFalse();
    OpenAIChatCompletionModel expectedModel = OpenAIChatCompletionModel.GPT_4;
    expectedSettings.setModel(expectedModel);
    String expectedApiKey = "apikey";
    expectedSettings.getCredentials().setApiKey(expectedApiKey);
    assertThat(settings.getState().isModified(expectedSettings)).isTrue();

    settings.apply(expectedSettings);

    assertThat(settings.getState().isModified(expectedSettings)).isFalse();
    assertThat(settings.getState().getModel()).isEqualTo(expectedModel);
    assertCredentials(settings.getState(), expectedApiKey);
    assertCredentials(credentialsManager.getCredentials(), expectedApiKey);
  }

  @Test
  public void testAzureSettings() {
    AzureCredentialsManager credentialsManager = new AzureCredentialsManager(credentialsService);
    AzureSettings settings = new AzureSettings(credentialsManager);

    assertCredentials(settings.getState(), null, null);
    assertCredentials(credentialsManager.getCredentials(), null, null);

    AzureSettingsState expectedSettings = new AzureSettingsState();
    assertThat(settings.getState().isModified(expectedSettings)).isFalse();
    String expectedApiKey = "apikey";
    String expectedToken = "token";
    expectedSettings.getCredentials().setApiKey(expectedApiKey);
    expectedSettings.getCredentials().setActiveDirectoryToken(expectedToken);
    assertThat(settings.getState().isModified(expectedSettings)).isTrue();

    settings.apply(expectedSettings);

    assertThat(settings.getState().isModified(expectedSettings)).isFalse();
    assertCredentials(settings.getState(), expectedApiKey, expectedToken);
    assertCredentials(credentialsManager.getCredentials(), expectedApiKey, expectedToken);
  }

  @Test
  public void testYouSettings() {
    YouCredentialsManager credentialsManager = new YouCredentialsManager(credentialsService);
    YouSettings settings = new YouSettings(credentialsManager);

    assertPassword(settings.getState(), null);
    assertPassword(credentialsManager.getCredentials(), null);

    YouSettingsState expectedSettings = new YouSettingsState();
    String expectedPassword = "password";
    expectedSettings.getCredentials().setPassword(expectedPassword);

    settings.apply(new PasswordCredentials(expectedPassword));

    assertPassword(settings.getState(), expectedPassword);
    assertPassword(credentialsManager.getCredentials(), expectedPassword);
  }



  private static void mockCodeGptPluginPath() {
    RawPluginDescriptor raw = new RawPluginDescriptor();
    raw.id = CODEGPT_ID.getIdString();
    IdeaPluginDescriptorImpl pluginDescriptor = new IdeaPluginDescriptorImpl(raw, Path.of(""),
        false, CODEGPT_ID, null, false);
    pluginDescriptor.setEnabled(true);
    PluginManagerCore.setPluginSet(
        new PluginSetBuilder(List.of(pluginDescriptor)).createPluginSetWithEnabledModulesMap());
  }

}