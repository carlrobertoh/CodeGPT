package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.advanced.AdvancedSettingsState;
import ee.carlrobert.codegpt.settings.state.AzureSettingsState;
import ee.carlrobert.codegpt.settings.state.CustomSettingsState;
import ee.carlrobert.codegpt.settings.state.OpenAISettingsState;
import ee.carlrobert.codegpt.settings.state.SettingsState;
import ee.carlrobert.openai.client.AzureClient;
import ee.carlrobert.openai.client.Client;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.ProxyAuthenticator;
import ee.carlrobert.openai.client.azure.AzureClientRequestParams;
import ee.carlrobert.openai.client.completion.CompletionClient;
import ee.carlrobert.openai.client.embeddings.EmbeddingsClient;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSources;

public class CompletionClientProvider {

  public static EmbeddingsClient getEmbeddingsClient() {
    if (SettingsState.getInstance().isUseOpenAIService()) {
      return getOpenAIClientBuilder().buildEmbeddingsClient();
    }
    // TODO
    return null;
  }

  public static OkHttpClient getCustomChatCompletionClient() {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

    return addDefaultClientParams(new Client.Builder(""))
        .setHost(CustomSettingsState.getInstance().getUrl())
        .setInterceptor(loggingInterceptor)
        .buildHttpClient();
  }

  public static CompletionClient getChatCompletionClient() {
    return getClientBuilder().buildChatCompletionClient();
  }

  @Deprecated
  public static CompletionClient getTextCompletionClient() {
    return getClientBuilder().buildTextCompletionClient();
  }

  public static Client.Builder getClientBuilder() {
    return SettingsState.getInstance().isUseAzureService() ? getAzureClientBuilder() : getOpenAIClientBuilder();
  }

  private static OpenAIClient.Builder getOpenAIClientBuilder() {
    var settings = OpenAISettingsState.getInstance();
    var builder = new OpenAIClient
        .Builder(OpenAICredentialsManager.getInstance().getApiKey())
        .setOrganization(settings.getOrganization());
    return (OpenAIClient.Builder) addDefaultClientParams(builder).setHost(settings.getBaseHost());
  }

  private static AzureClient.Builder getAzureClientBuilder() {
    var settings = AzureSettingsState.getInstance();
    var params = new AzureClientRequestParams(settings.getResourceName(), settings.getDeploymentId(), settings.getApiVersion());
    var builder = new AzureClient.Builder(AzureCredentialsManager.getInstance().getSecret(), params)
        .setActiveDirectoryAuthentication(settings.isUseAzureActiveDirectoryAuthentication());
    return (AzureClient.Builder) addDefaultClientParams(builder).setHost(settings.getBaseHost());
  }

  private static Client.Builder addDefaultClientParams(Client.Builder builder) {
    var advancedSettings = AdvancedSettingsState.getInstance();
    var proxyHost = advancedSettings.getProxyHost();
    var proxyPort = advancedSettings.getProxyPort();
    if (!proxyHost.isEmpty() && proxyPort != 0) {
      builder.setProxy(
          new Proxy(advancedSettings.getProxyType(), new InetSocketAddress(proxyHost, proxyPort)));
      if (advancedSettings.isProxyAuthSelected()) {
        builder.setProxyAuthenticator(
            new ProxyAuthenticator(advancedSettings.getProxyUsername(), advancedSettings.getProxyPassword()));
      }
    }

    return builder
        .setConnectTimeout((long) advancedSettings.getConnectTimeout(), TimeUnit.SECONDS)
        .setReadTimeout((long) advancedSettings.getReadTimeout(), TimeUnit.SECONDS)
        .setRetryOnReadTimeout(true);
  }
}


