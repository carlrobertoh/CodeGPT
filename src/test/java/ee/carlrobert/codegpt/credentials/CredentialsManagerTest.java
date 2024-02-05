package ee.carlrobert.codegpt.credentials;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static testsupport.TestUtil.assertCredentials;
import static testsupport.TestUtil.assertPassword;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.mock.MockApplication;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import ee.carlrobert.codegpt.TestPasswordSafe;
import ee.carlrobert.codegpt.credentials.manager.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.LlamaLocalCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.LlamaRemoteCredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.OpenAICredentialsManager;
import ee.carlrobert.codegpt.credentials.manager.YouCredentialsManager;
import org.junit.BeforeClass;
import org.junit.Test;

public class CredentialsManagerTest {

  private static TestPasswordSafe passwordSafe = new TestPasswordSafe();

  @BeforeClass
  public static void setUp() {
    Disposable disposable = Disposer.newDisposable();
    final var application = MockApplication.setUp(disposable);
    ApplicationManager.setApplication(application, disposable);
    application.registerService(PasswordSafe.class, passwordSafe);
  }

  @Test
  public void testLlamaCredentialsManager() {
    String expectedApiKey = "apikey";
    CredentialAttributes llamaLocalApiKey = CredentialsUtil.createCredentialAttributes(
        "LLAMA_LOCAL_API_KEY");
    passwordSafe.set(llamaLocalApiKey, new Credentials("foo", expectedApiKey));
    LlamaLocalCredentialsManager localCredentialsManager = new LlamaLocalCredentialsManager();
    assertCredentials(localCredentialsManager.getCredentials(), expectedApiKey);

    CredentialAttributes llamaRemoteApiKey = CredentialsUtil.createCredentialAttributes(
        "LLAMA_REMOTE_API_KEY");
    expectedApiKey = "test";
    passwordSafe.set(llamaRemoteApiKey, new Credentials("foo", expectedApiKey));
    LlamaRemoteCredentialsManager remoteCredentialsManager = new LlamaRemoteCredentialsManager();
    assertCredentials(remoteCredentialsManager.getCredentials(), expectedApiKey);

    expectedApiKey = "bar";
    remoteCredentialsManager.apply(new ApiKeyCredentials(expectedApiKey));
    assertCredentials(remoteCredentialsManager.getCredentials(), expectedApiKey);
    assertThat(passwordSafe.get(llamaRemoteApiKey).getPasswordAsString()).isEqualTo(expectedApiKey);
  }

  @Test
  public void testOpenAiCredentialsManager() {
    String expectedApiKey = "apikey";
    CredentialAttributes apiKey = CredentialsUtil.createCredentialAttributes(
        "OPENAI_API_KEY");
    passwordSafe.set(apiKey, new Credentials("foo", expectedApiKey));
    OpenAICredentialsManager credentialsManager = new OpenAICredentialsManager();
    assertCredentials(credentialsManager.getCredentials(), expectedApiKey);

    expectedApiKey = "test";
    credentialsManager.apply(new ApiKeyCredentials(expectedApiKey));
    assertCredentials(credentialsManager.getCredentials(), expectedApiKey);
    assertThat(passwordSafe.get(apiKey).getPasswordAsString()).isEqualTo(expectedApiKey);
  }

  @Test
  public void testAzureCredentialsManager() {
    String expectedApiKey = "apikey";
    String expectedToken = "token";
    CredentialAttributes apiKey = CredentialsUtil.createCredentialAttributes(
        "AZURE_OPENAI_API_KEY");
    CredentialAttributes tokenKey = CredentialsUtil.createCredentialAttributes(
        "AZURE_ACTIVE_DIRECTORY_TOKEN");
    passwordSafe.set(apiKey, new Credentials("foo", expectedApiKey));
    passwordSafe.set(tokenKey, new Credentials("foo", expectedToken));
    AzureCredentialsManager credentialsManager = new AzureCredentialsManager();
    assertCredentials(credentialsManager.getCredentials(), expectedApiKey, expectedToken);

    expectedApiKey = "test";
    expectedToken = "tokentest";
    credentialsManager.apply(new AzureCredentials(expectedApiKey, expectedToken));
    assertCredentials(credentialsManager.getCredentials(), expectedApiKey, expectedToken);
    assertThat(passwordSafe.get(apiKey).getPasswordAsString()).isEqualTo(expectedApiKey);
    assertThat(passwordSafe.get(tokenKey).getPasswordAsString()).isEqualTo(expectedToken);
  }

  @Test
  public void testOpenYouCredentialsManager() {
    String expectedPw = "password";
    CredentialAttributes passwordKey = CredentialsUtil.createCredentialAttributes(
        "YOU_ACCOUNT_PASSWORD");
    passwordSafe.set(passwordKey, new Credentials("foo", expectedPw));
    YouCredentialsManager credentialsManager = new YouCredentialsManager();
    assertPassword(credentialsManager.getCredentials(), expectedPw);

    expectedPw = "test";
    credentialsManager.apply(new PasswordCredentials(expectedPw));
    assertPassword(credentialsManager.getCredentials(), expectedPw);
    assertThat(passwordSafe.get(passwordKey).getPasswordAsString()).isEqualTo(expectedPw);
  }

}
