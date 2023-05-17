package ee.carlrobert.codegpt.client;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
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
    return getOpenAIClientBuilder(false).buildDashboardClient();
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
    return getOpenAIClientBuilder(true);
  }

  private static OpenAIClient.Builder getOpenAIClientBuilder(boolean isCustomServiceEnabled) {
    var settings = SettingsState.getInstance();
    var builder = new OpenAIClient.Builder(settings.getApiKey());

    if (settings.useOpenAIService) {
      builder.setOrganization(settings.organization);
    }

    return (OpenAIClient.Builder) addDefaultClientParams(builder, isCustomServiceEnabled);
  }

  private static AzureClient.Builder getAzureClientBuilder() {
    var settings = SettingsState.getInstance();
    var params = new AzureClientRequestParams(
        settings.resourceName, settings.deploymentId, settings.apiVersion);
    var builder = new AzureClient.Builder(settings.getApiKey(), params)
        .setActiveDirectoryAuthentication(settings.useActiveDirectoryAuthentication);
    return (AzureClient.Builder) addDefaultClientParams(builder, true);
  }

  private static Client.Builder addDefaultClientParams(
      Client.Builder builder, boolean isCustomServiceEnabled) {
    var settings = SettingsState.getInstance();
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

    if (isUnitTestingMode() || (isCustomServiceEnabled && settings.useCustomService)) {
      builder.setHost(settings.customHost);
    }

    return builder
        .setConnectTimeout(120L, TimeUnit.SECONDS)
        .setReadTimeout(60L, TimeUnit.SECONDS);
  }

  private static boolean isUnitTestingMode() {
    Application app = ApplicationManager.getApplication();
    return app != null && app.isUnitTestMode();
  }
}


