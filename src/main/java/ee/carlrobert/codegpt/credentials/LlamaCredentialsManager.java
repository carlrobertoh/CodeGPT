package ee.carlrobert.codegpt.credentials;

public class LlamaCredentialsManager extends ApiKeyCredentialsManager {
  public LlamaCredentialsManager(String suffix) {
    super(CredentialsUtil.createCredentialAttributes("LLAMA_API_KEY" + suffix));
  }

}
