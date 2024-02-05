package ee.carlrobert.codegpt.settings.state;

import static ee.carlrobert.codegpt.CodeGPTPlugin.CODEGPT_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.plugins.IdeaPluginDescriptorImpl;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.plugins.PluginSetBuilder;
import com.intellij.ide.plugins.RawPluginDescriptor;
import com.intellij.mock.MockApplication;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.completions.llama.HuggingFaceModel;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
import ee.carlrobert.codegpt.credentials.AzureCredentials;
import ee.carlrobert.codegpt.credentials.PasswordCredentials;
import ee.carlrobert.codegpt.credentials.manager.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.LlamaLocalCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.LlamaRemoteCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.OpenAICredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.YouCredentialsManager;
import ee.carlrobert.codegpt.settings.state.azure.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.llama.LlamaLocalSettings;
import ee.carlrobert.codegpt.settings.state.llama.LlamaSettingsState;
import ee.carlrobert.codegpt.settings.state.openai.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.util.CommonSettings;
import ee.carlrobert.codegpt.settings.state.you.YouSettingsState;
import ee.carlrobert.llm.client.openai.completion.OpenAIChatCompletionModel;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.concurrency.Promise;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServiceSettingsTest {

  @BeforeClass
  public static void setUp() {
    Disposable disposable = Disposer.newDisposable();
    final var application = MockApplication.setUp(disposable);
    ApplicationManager.setApplication(application, disposable);
    mockCodeGptPluginPath();
    application.registerService(PasswordSafe.class, new TestPasswordSafe());
    application.registerService(YouCredentialsManager.class, new YouCredentialsManager());
    application.registerService(OpenAICredentialsManager.class, new OpenAICredentialsManager());
    application.registerService(LlamaLocalCredentialsManager.class,
        new LlamaLocalCredentialsManager());
    application.registerService(LlamaRemoteCredentialsManager.class,
        new LlamaRemoteCredentialsManager());
  }

  @Test
  public void testLlamaSettings() {
    LlamaSettings settings = new LlamaSettings();
    LlamaRemoteCredentialsManager remoteCredentialsManager =
        LlamaRemoteCredentialsManager.getInstance();
    LlamaLocalCredentialsManager localCredentialsManager =
        LlamaLocalCredentialsManager.getInstance();

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
    OpenAISettings settings = new OpenAISettings();
    OpenAICredentialsManager credentialsManager = OpenAICredentialsManager.getInstance();

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
    AzureCredentialsManager credentialsManager = AzureCredentialsManager.getInstance();
    AzureSettings settings = new AzureSettings();

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
    YouCredentialsManager credentialsManager = YouCredentialsManager.getInstance();
    YouSettings settings = new YouSettings();

    assertPassword(settings.getState(), null);
    assertPassword(credentialsManager.getCredentials(), null);

    YouSettingsState expectedSettings = new YouSettingsState();
    String expectedPassword = "password";
    expectedSettings.getCredentials().setPassword(expectedPassword);

    settings.apply(new PasswordCredentials(expectedPassword));

    assertPassword(settings.getState(), expectedPassword);
    assertPassword(credentialsManager.getCredentials(), expectedPassword);
  }

  private void assertCredentials(CommonSettings<AzureCredentials> settings,
      String apiKey, String token) {
    assertCredentials(settings.getCredentials(), apiKey, token);
  }

  private void assertCredentials(AzureCredentials credentials, String apiKey,
      String token) {
    assertCredentials(credentials, apiKey);
    assertThat(credentials.getActiveDirectoryToken()).isEqualTo(token);
  }

  private void assertCredentials(CommonSettings<ApiKeyCredentials> settings, String apiKey) {
    assertCredentials(settings.getCredentials(), apiKey);
  }

  private void assertCredentials(ApiKeyCredentials credentials, String apiKey) {
    assertThat(credentials.getApiKey()).isEqualTo(apiKey);
  }

  private void assertPassword(CommonSettings<PasswordCredentials> settings, String password) {
    assertPassword(settings.getCredentials(), password);
  }

  private void assertPassword(PasswordCredentials credentials, String password) {
    assertThat(credentials.getPassword()).isEqualTo(password);
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

  /**
   * Dummy {@link PasswordSafe} implementation using a {@link HashMap}.
   */
  static class TestPasswordSafe extends PasswordSafe {

    Map<CredentialAttributes, Credentials> credentials = new HashMap<>();

    @Override
    public boolean isMemoryOnly() {
      return true;
    }

    @Override
    public boolean isRememberPasswordByDefault() {
      return false;
    }

    @Override
    public void setRememberPasswordByDefault(boolean b) {

    }

    @NotNull
    @Override
    public Promise<Credentials> getAsync(@NotNull CredentialAttributes credentialAttributes) {
      return null;
    }

    @Override
    public boolean isPasswordStoredOnlyInMemory(@NotNull CredentialAttributes credentialAttributes,
        @NotNull Credentials credentials) {
      return false;
    }

    @Override
    public void set(@NotNull CredentialAttributes credentialAttributes,
        @Nullable Credentials credentials, boolean b) {
      this.credentials.put(credentialAttributes, credentials);
    }

    @Override
    public void set(@NotNull CredentialAttributes attributes, @Nullable Credentials credentials) {
      set(attributes, credentials, false);
    }

    @Override
    public @Nullable Credentials get(@NotNull CredentialAttributes attributes) {
      return this.credentials.get(attributes);
    }
  }

}