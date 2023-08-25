package ee.carlrobert.codegpt.user.auth;

public interface AuthenticationHandler {

  void handleAuthenticated();

  void handleInvalidCredentials();

  void handleGenericError();
}
