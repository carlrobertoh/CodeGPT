package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.state.settings.advanced.AdvancedSettingsState;
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
    return getClientBuilder().buildDashboardClient();
  }
  public static CompletionClient getChatCompletionClient(SettingsState settings) {
    if (settings.useAzureService) {
      return getClientBuilder().buildAzureChatCompletionClient(getAzureRequestParams());
    }
    return getClientBuilder().buildChatCompletionClient();
  }

  public static CompletionClient getTextCompletionClient(SettingsState settings) {
    if (settings.useAzureService) {
      return getClientBuilder().buildAzureTextCompletionClient(getAzureRequestParams());
    }
    return getClientBuilder().buildTextCompletionClient();
  }

  private static AzureClientRequestParams getAzureRequestParams() {
    var settings = SettingsState.getInstance();
    return new AzureClientRequestParams(settings.resourceName, settings.deploymentId, settings.apiVersion);
  }

  private static OpenAIClient.Builder getClientBuilder() {
    var settings = SettingsState.getInstance();
    var builder = new OpenAIClient.Builder(settings.apiKey) // TODO: ENV var?
        .setConnectTimeout(60L, TimeUnit.SECONDS)
        .setReadTimeout(30L, TimeUnit.SECONDS);

    if (settings.useOpenAIService) {
      builder.setOrganization(settings.organization);
    }

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

    return builder;
  }
}


