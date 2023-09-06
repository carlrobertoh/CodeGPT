package ee.carlrobert.codegpt.user.auth;

import ee.carlrobert.codegpt.user.auth.response.AuthenticationResponse;

public interface AuthenticationHandler {

  void handleAuthenticated(AuthenticationResponse authenticationResponse);

  void handleGenericError();

  void handleError(AuthenticationError authenticationError);
}
