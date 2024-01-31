package ee.carlrobert.codegpt.credentials;

import static ee.carlrobert.codegpt.util.Utils.areStringsDifferentIgnoringEmptyOrNull;

import org.jetbrains.annotations.Nullable;

public abstract class ServiceCredentialsManager {

  private final String suffix;

  public ServiceCredentialsManager() {
    this.suffix = "";
  }

  public ServiceCredentialsManager(String suffix) {
    this.suffix = suffix;
  }

  public boolean providesApiKey() {
    return true;
  }

  @Nullable
  public abstract String getApiKey();

  public abstract void setApiKey(String apiKey);

  public void apply(String apiKey){
    if(providesApiKey()){
      setApiKey(apiKey);
    }
  }

  public boolean isApiKeySet() {
    return getApiKey() != null && !getApiKey().isEmpty();
  }

  public boolean isModified(String apiKey) {
    if (!providesApiKey()) {
      return false;
    }
    return areStringsDifferentIgnoringEmptyOrNull(getApiKey(), apiKey);
  }



  public String getSuffix() {
    return suffix;
  }
}
