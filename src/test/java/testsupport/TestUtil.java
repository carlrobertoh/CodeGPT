package testsupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import ee.carlrobert.codegpt.credentials.ApiKeyCredentials;
import ee.carlrobert.codegpt.credentials.AzureCredentials;
import ee.carlrobert.codegpt.credentials.PasswordCredentials;
import ee.carlrobert.codegpt.settings.state.util.CommonSettings;

public class TestUtil {
  public static void assertCredentials(CommonSettings<AzureCredentials> settings,
      String apiKey, String token) {
    assertCredentials(settings.getCredentials(), apiKey, token);
  }

  public static void assertCredentials(AzureCredentials credentials, String apiKey,
      String token) {
    assertCredentials(credentials, apiKey);
    assertThat(credentials.getActiveDirectoryToken()).isEqualTo(token);
  }

  public static void assertCredentials(CommonSettings<ApiKeyCredentials> settings, String apiKey) {
    assertCredentials(settings.getCredentials(), apiKey);
  }

  public static void assertCredentials(ApiKeyCredentials credentials, String apiKey) {
    assertThat(credentials.getApiKey()).isEqualTo(apiKey);
  }

  public static void assertPassword(CommonSettings<PasswordCredentials> settings, String password) {
    assertPassword(settings.getCredentials(), password);
  }

  public static void assertPassword(PasswordCredentials credentials, String password) {
    assertThat(credentials.getPassword()).isEqualTo(password);
  }
}
