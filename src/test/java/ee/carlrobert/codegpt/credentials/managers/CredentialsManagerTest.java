package ee.carlrobert.codegpt.credentials.managers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import ee.carlrobert.codegpt.TestPasswordSafe;
import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
import ee.carlrobert.codegpt.credentials.AzureCredentials;
import ee.carlrobert.codegpt.credentials.CredentialsService;
import ee.carlrobert.codegpt.credentials.PasswordCredentials;
import org.junit.Before;
import org.junit.Test;

public class CredentialsManagerTest {

  private TestPasswordSafe passwordSafe;
  private CredentialsService credentialsService;

  @Before
  public void setUp() {
    passwordSafe = new TestPasswordSafe();
    credentialsService = new CredentialsService(passwordSafe);
  }

  @Test
  public void testLlamaInitialCredentials() {
    CredentialAttributes llamaApiKeyAttributes = credentialsService.createCredentialAttributes(
        "LLAMA_API_KEY");
    passwordSafe.set(llamaApiKeyAttributes, new Credentials("foo", "apikey"));

    LlamaCredentialsManager llamaLocalCredentialsManager = new LlamaCredentialsManager(
        credentialsService);

    assertThat(llamaLocalCredentialsManager.getCredentials().getApiKey())
        .isEqualTo("apikey");
  }

  @Test
  public void testLlamaApplyCredentials() {
    LlamaCredentialsManager credentialsManager = new LlamaCredentialsManager(credentialsService);

    credentialsManager.apply(new ApiKeyCredentials("apikey"));

    CredentialAttributes apiKeyAttributes = credentialsService.createCredentialAttributes(
        "LLAMA_API_KEY");
    assertThat(passwordSafe.get(apiKeyAttributes).getPasswordAsString())
        .isEqualTo("apikey");
  }

  @Test
  public void testOpenAIInitialCredentials() {
    CredentialAttributes apiKeyAttributes = credentialsService.createCredentialAttributes(
        "OPENAI_API_KEY");
    passwordSafe.set(apiKeyAttributes, new Credentials("foo", "apikey"));

    OpenAICredentialsManager credentialsManager = new OpenAICredentialsManager(credentialsService);

    assertThat(credentialsManager.getCredentials().getApiKey())
        .isEqualTo("apikey");
  }

  @Test
  public void testOpenAIApplyCredentials() {
    OpenAICredentialsManager credentialsManager = new OpenAICredentialsManager(credentialsService);

    credentialsManager.apply(new ApiKeyCredentials("apikey"));

    CredentialAttributes apiKeyAttributes = credentialsService.createCredentialAttributes(
        "OPENAI_API_KEY");
    assertThat(passwordSafe.get(apiKeyAttributes).getPasswordAsString())
        .isEqualTo("apikey");
  }

  @Test
  public void testAzureInitialCredentials() {
    CredentialAttributes apiKeyAttributes = credentialsService.createCredentialAttributes(
        "AZURE_OPENAI_API_KEY");
    CredentialAttributes activeDirectoryTokenAttributes =
        credentialsService.createCredentialAttributes("AZURE_ACTIVE_DIRECTORY_TOKEN");
    passwordSafe.set(apiKeyAttributes, new Credentials("foo", "apikey"));
    passwordSafe.set(activeDirectoryTokenAttributes, new Credentials("foo", "token"));

    AzureCredentialsManager credentialsManager = new AzureCredentialsManager(credentialsService);

    AzureCredentials credentials = credentialsManager.getCredentials();
    assertThat(credentials.getApiKey()).isEqualTo("apikey");
    assertThat(credentials.getActiveDirectoryToken()).isEqualTo("token");
  }

  @Test
  public void testAzureApplyCredentials() {
    AzureCredentialsManager credentialsManager = new AzureCredentialsManager(credentialsService);

    credentialsManager.apply(new AzureCredentials("apikey", "token"));

    CredentialAttributes apiKeyAttributes = credentialsService.createCredentialAttributes(
        "AZURE_OPENAI_API_KEY");
    CredentialAttributes activeDirectoryTokenAttributes =
        credentialsService.createCredentialAttributes("AZURE_ACTIVE_DIRECTORY_TOKEN");
    assertThat(passwordSafe.get(apiKeyAttributes).getPasswordAsString()).isEqualTo("apikey");
    assertThat(passwordSafe.get(activeDirectoryTokenAttributes).getPasswordAsString()).isEqualTo(
        "token");
  }


  @Test
  public void testYouInitialCredentials() {
    CredentialAttributes passwordAttributes =
        credentialsService.createCredentialAttributes("YOU_ACCOUNT_PASSWORD");
    passwordSafe.set(passwordAttributes, new Credentials("foo", "password"));

    YouCredentialsManager credentialsManager = new YouCredentialsManager(credentialsService);

    assertThat(credentialsManager.getCredentials().getPassword()).isEqualTo("password");
  }

  @Test
  public void testYouApplyCredentials() {
    YouCredentialsManager credentialsManager = new YouCredentialsManager(credentialsService);

    credentialsManager.apply(new PasswordCredentials("password"));

    CredentialAttributes passwordAttributes = credentialsService.createCredentialAttributes(
        "YOU_ACCOUNT_PASSWORD");
    assertThat(passwordSafe.get(passwordAttributes).getPasswordAsString()).isEqualTo("password");
  }


}
