package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.credentials.AzureCredentialsManager;
import ee.carlrobert.codegpt.credentials.OpenAICredentialsManager;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.settings.advanced.AdvancedSettingsState;
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

public class CompletionClientProvider {

  public static EmbeddingsClient getEmbeddingsClient() {
    var settings = SettingsState.getInstance();

    if (settings.useOpenAIService) {
      var baseHost = SettingsState.getInstance().openAIBaseHost;
      return ((OpenAIClient.Builder) getOpenAIClientBuilder().setHost(baseHost)).buildEmbeddingsClient();
    }

    return null; // TODO
  }

  public static CompletionClient getChatCompletionClient(SettingsState settings) {
    return getClientBuilder(settings).buildChatCompletionClient();
  }

  @Deprecated
  public static CompletionClient getTextCompletionClient(SettingsState settings) {
    return getClientBuilder(settings).buildTextCompletionClient();
  }

  public static Client.Builder getClientBuilder(SettingsState settings) {
    return settings.useAzureService ?
        getAzureClientBuilder().setHost(settings.azureBaseHost) :
        getOpenAIClientBuilder().setHost(settings.openAIBaseHost);
  }

  private static OpenAIClient.Builder getOpenAIClientBuilder() {
    var settings = SettingsState.getInstance();
    var builder = new OpenAIClient.Builder(OpenAICredentialsManager.getInstance().getApiKey());

    if (settings.useOpenAIService) {
      builder.setOrganization(settings.openAIOrganization);
    }

    return (OpenAIClient.Builder) addDefaultClientParams(builder);
  }

  private static AzureClient.Builder getAzureClientBuilder() {
    var settings = SettingsState.getInstance();
    var params = new AzureClientRequestParams(settings.azureResourceName, settings.azureDeploymentId, settings.azureApiVersion);
    var azureCredentials = AzureCredentialsManager.getInstance();
    var secret = settings.useAzureActiveDirectoryAuthentication ?
        azureCredentials.getAzureActiveDirectoryToken() :
        azureCredentials.getAzureOpenAIApiKey();
    var builder = new AzureClient.Builder(secret, params).setActiveDirectoryAuthentication(settings.useAzureActiveDirectoryAuthentication);
    return (AzureClient.Builder) addDefaultClientParams(builder);
  }

  private static Client.Builder addDefaultClientParams(Client.Builder builder) {
    var advancedSettings = AdvancedSettingsState.getInstance();
    var proxyHost = advancedSettings.proxyHost;
    var proxyPort = advancedSettings.proxyPort;
    if (!proxyHost.isEmpty() && proxyPort != 0) {
      builder.setProxy(
          new Proxy(advancedSettings.proxyType, new InetSocketAddress(proxyHost, proxyPort)));
      if (advancedSettings.isProxyAuthSelected) {
        builder.setProxyAuthenticator(
            new ProxyAuthenticator(advancedSettings.proxyUsername, advancedSettings.proxyPassword));
      }
    }

    return builder
        .setConnectTimeout((long) advancedSettings.connectTimeout, TimeUnit.SECONDS)
        .setReadTimeout((long) advancedSettings.readTimeout, TimeUnit.SECONDS)
        .setRetryOnReadTimeout(true);
  }
}


