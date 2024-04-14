package ee.carlrobert.codegpt.completions;

import static ee.carlrobert.codegpt.credentials.Credential.getCredential;

import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.you.YouUserManager;
import ee.carlrobert.codegpt.credentials.CredentialsStore.CredentialKey;
import ee.carlrobert.codegpt.settings.advanced.AdvancedSettings;
import ee.carlrobert.codegpt.settings.service.anthropic.AnthropicSettings;
import ee.carlrobert.codegpt.settings.service.azure.AzureSettings;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.openai.OpenAISettings;
import ee.carlrobert.llm.client.anthropic.ClaudeClient;
import ee.carlrobert.llm.client.azure.AzureClient;
import ee.carlrobert.llm.client.azure.AzureCompletionRequestParams;
import ee.carlrobert.llm.client.llama.LlamaClient;
import ee.carlrobert.llm.client.openai.OpenAIClient;
import ee.carlrobert.llm.client.you.UTMParameters;
import ee.carlrobert.llm.client.you.YouClient;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;

public class CompletionClientProvider {

  private CompletionClientProvider() {
  }

  public static OpenAIClient getOpenAIClient() {
    return new OpenAIClient.Builder(getCredential(CredentialKey.OPENAI_API_KEY))
        .setOrganization(OpenAISettings.getCurrentState().getOrganization())
        .build(getDefaultClientBuilder());
  }

  public static ClaudeClient getClaudeClient() {
    return new ClaudeClient(
        getCredential(CredentialKey.ANTHROPIC_API_KEY),
        AnthropicSettings.getCurrentState().getApiVersion(),
        getDefaultClientBuilder());
  }

  public static AzureClient getAzureClient() {
    var settings = AzureSettings.getCurrentState();
    var params = new AzureCompletionRequestParams(
        settings.getResourceName(),
        settings.getDeploymentId(),
        settings.getApiVersion());
    var useAzureActiveDirectoryAuthentication = settings.isUseAzureActiveDirectoryAuthentication();
    var credential = useAzureActiveDirectoryAuthentication
        ? getCredential(CredentialKey.AZURE_ACTIVE_DIRECTORY_TOKEN)
        : getCredential(CredentialKey.AZURE_OPENAI_API_KEY);
    return new AzureClient.Builder(credential, params)
        .setActiveDirectoryAuthentication(useAzureActiveDirectoryAuthentication)
        .build(getDefaultClientBuilder());
  }

  public static YouClient getYouClient() {
    var utmParameters = new UTMParameters();
    utmParameters.setSource("ide");
    utmParameters.setMedium("jetbrains");
    utmParameters.setCampaign(CodeGPTPlugin.getVersion());
    utmParameters.setContent("CodeGPT");

    var sessionId = "";
    var accessToken = "";
    var youUserManager = YouUserManager.getInstance();
    if (youUserManager.isAuthenticated()) {
      var authenticationResponse = youUserManager.getAuthenticationResponse().data();
      sessionId = authenticationResponse.session().sessionId();
      accessToken = authenticationResponse.sessionJwt();
    }

    return new YouClient.Builder(sessionId, accessToken)
        .setUTMParameters(utmParameters)
        .build();
  }

  public static LlamaClient getLlamaClient() {
    var llamaSettings = LlamaSettings.getCurrentState();
    var builder = new LlamaClient.Builder()
        .setPort(llamaSettings.getServerPort());
    if (!llamaSettings.isRunLocalServer()) {
      builder.setHost(llamaSettings.getBaseHost());
      String apiKey = getCredential(CredentialKey.LLAMA_API_KEY);
      if (apiKey != null && !apiKey.isBlank()) {
        builder.setApiKey(apiKey);
      }
    }
    return builder.build(getDefaultClientBuilder());
  }

  public static OkHttpClient.Builder getDefaultClientBuilder() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    var advancedSettings = AdvancedSettings.getCurrentState();
    var proxyHost = advancedSettings.getProxyHost();
    var proxyPort = advancedSettings.getProxyPort();
    if (!proxyHost.isEmpty() && proxyPort != 0) {
      builder.proxy(
          new Proxy(advancedSettings.getProxyType(), new InetSocketAddress(proxyHost, proxyPort)));
      if (advancedSettings.isProxyAuthSelected()) {
        builder.proxyAuthenticator((route, response) ->
            response.request()
                .newBuilder()
                .header("Proxy-Authorization", Credentials.basic(
                    advancedSettings.getProxyUsername(),
                    advancedSettings.getProxyPassword()))
                .build());
      }
    }

    return builder
        .connectTimeout(advancedSettings.getConnectTimeout(), TimeUnit.SECONDS)
        .readTimeout(advancedSettings.getReadTimeout(), TimeUnit.SECONDS);
  }
}
