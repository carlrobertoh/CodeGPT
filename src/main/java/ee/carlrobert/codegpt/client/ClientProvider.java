package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.state.settings.SettingsState;
import ee.carlrobert.codegpt.state.settings.advanced.AdvancedSettingsState;
import ee.carlrobert.openai.client.OpenAIClient;
import ee.carlrobert.openai.client.ProxyAuthenticator;
import ee.carlrobert.openai.client.billing.BillingClient;
import ee.carlrobert.openai.client.completion.chat.ChatCompletionClient;
import ee.carlrobert.openai.client.completion.text.TextCompletionClient;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class ClientProvider {

  public static BillingClient getBillingClient() {
    return getClientBuilder().buildBillingClient();
  }

  public static ChatCompletionClient getChatCompletionClient() {
    return getClientBuilder().buildChatCompletionClient();
  }

  public static TextCompletionClient getTextCompletionClient() {
    return getClientBuilder().buildTextCompletionClient();
  }

  private static OpenAIClient.Builder getClientBuilder() {
    var builder = new OpenAIClient.Builder(SettingsState.getInstance().apiKey) // TODO: ENV var?
        .setConnectTimeout(60L, TimeUnit.SECONDS)
        .setReadTimeout(30L, TimeUnit.SECONDS);

    var settings = AdvancedSettingsState.getInstance();
    var proxyHost = settings.proxyHost;
    var proxyPort = settings.proxyPort;
    if (!proxyHost.isEmpty() && proxyPort != 0) {
      builder.setProxy(new Proxy(settings.proxyType, new InetSocketAddress(proxyHost, proxyPort)));
      if (settings.isProxyAuthSelected) {
        builder.setProxyAuthenticator(new ProxyAuthenticator(settings.proxyUsername, settings.proxyPassword));
      }
    }

    return builder;
  }
}


