package ee.carlrobert.codegpt.credentials;

public class OpenAICredentialsManager extends ApiKeyCredentialsManager {

  public OpenAICredentialsManager() {
    super(CredentialsUtil.createCredentialAttributes("OPENAI_API_KEY"));
  }

}
