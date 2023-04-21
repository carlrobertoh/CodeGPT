package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.state.settings.advanced.AdvancedSettingsState;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.ProxyAuthenticator;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionClient;
import ee.carlrobert.openai.client.completion.text.TextCompletionClient;
import ee.carlrobert.openai.client.dashboard.DashboardClient;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class ClientProvider {

  public static DashboardClient getDashboardClient() {
    return getClientBuilder().buildDashboardClient();
  }

  public static ChatCompletionClient getChatCompletionClient() {
    return getClientBuilder().buildChatCompletionClient();
  }

  public static TextCompletionClient getTextCompletionClient() {
    return getClientBuilder().buildTextCompletionClient();
  }

  private static OpenAIClient.Builder getClientBuilder() {
    var settings = SettingsState.getInstance();
    var builder = new OpenAIClient.Builder(settings.apiKey) // TODO: ENV var?
        .setOrganization(settings.organization)
        .setConnectTimeout(60L, TimeUnit.SECONDS)
        .setReadTimeout(30L, TimeUnit.SECONDS);

    var advancedSettings = AdvancedSettingsState.getInstance();
    var proxyHost = advancedSettings.proxyHost;
    var proxyPort = advancedSettings.proxyPort;
    if (!proxyHost.isEmpty() && proxyPort != 0) {
      builder.setProxy(new Proxy(advancedSettings.proxyType, new InetSocketAddress(proxyHost, proxyPort)));
      if (advancedSettings.isProxyAuthSelected) {
        builder.setProxyAuthenticator(new ProxyAuthenticator(advancedSettings.proxyUsername, advancedSettings.proxyPassword));
      }
    }

    return builder;
  }
}


