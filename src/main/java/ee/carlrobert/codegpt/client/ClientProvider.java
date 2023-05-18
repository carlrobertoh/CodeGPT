package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.state.settings.advanced.AdvancedSettingsState;
import ee.carlrobert.openai.client.AzureClient;
import ee.carlrobert.openai.client.Client;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.ProxyAuthenticator;
import ee.carlrobert.openai.client.azure.AzureClientRequestParams;
import ee.carlrobert.openai.client.completion.CompletionClient;
import ee.carlrobert.openai.client.dashboard.DashboardClient;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class ClientProvider {

  public static DashboardClient getDashboardClient() {
    return getOpenAIClientBuilder().buildDashboardClient();
  }

  public static CompletionClient getChatCompletionClient(SettingsState settings) {
    return getClientBuilder(settings).buildChatCompletionClient();
  }

  public static CompletionClient getTextCompletionClient(SettingsState settings) {
    return getClientBuilder(settings).buildTextCompletionClient();
  }

  private static Client.Builder getClientBuilder(SettingsState settings) {
    return settings.useAzureService ? getAzureClientBuilder() : getOpenAIClientBuilder();
  }

  private static OpenAIClient.Builder getOpenAIClientBuilder() {
    var settings = SettingsState.getInstance();
    var builder = new OpenAIClient.Builder(settings.apiKey);

    if (!settings.apiHost.isEmpty()){
      builder.setHost(settings.apiHost);
    }

    if (settings.useOpenAIService) {
      builder.setOrganization(settings.organization);
    }
    return (OpenAIClient.Builder) addDefaultClientParams(builder);
  }

  private static AzureClient.Builder getAzureClientBuilder() {
    var settings = SettingsState.getInstance();
    var params = new AzureClientRequestParams(
        settings.resourceName, settings.deploymentId, settings.apiVersion);
    var builder = new AzureClient.Builder(settings.apiKey, params)
        .setActiveDirectoryAuthentication(settings.useActiveDirectoryAuthentication);
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

    if (!advancedSettings.host.isEmpty()) {
      builder.setHost(advancedSettings.host);
    }

    return builder
        .setConnectTimeout(60L, TimeUnit.SECONDS)
        .setReadTimeout(30L, TimeUnit.SECONDS);
  }
}


