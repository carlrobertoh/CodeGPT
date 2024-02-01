package ee.carlrobert.codegpt.credentials;

public final class LlamaCredentialsManager extends ApiKeyCredentialsManager {

  public LlamaCredentialsManager(String suffix) {
    super(CredentialsUtil.createCredentialAttributes("LLAMA_API_KEY" + suffix));
  }

}
