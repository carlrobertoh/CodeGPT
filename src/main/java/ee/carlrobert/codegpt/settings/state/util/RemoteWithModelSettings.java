package ee.carlrobert.codegpt.settings.state.util;

import static ee.carlrobert.codegpt.util.Utils.areValuesDifferent;

import ee.carlrobert.codegpt.credentials.CredentialsManager;
import ee.carlrobert.llm.completion.CompletionModel;

/**
 * Settings for using a remote service with model selection
 */
public class RemoteWithModelSettings<T extends CredentialsManager, M extends CompletionModel> extends
    RemoteSettings<T> {

  protected M model;

  public RemoteWithModelSettings() {
  }

  public RemoteWithModelSettings(String baseHost, String path, M model, T credentialsManager) {
    this.baseHost = baseHost;
    this.path = path;
    this.model = model;
    this.credentialsManager = credentialsManager;
  }

  public M getModel() {
    return model;
  }

  public void setModel(M model) {
    this.model = model;
  }

  public boolean isModified(RemoteWithModelSettings<T, M> remoteSettings) {
    return super.isModified(remoteSettings)
        || areValuesDifferent(remoteSettings.getModel(), this.getModel());
  }

}
