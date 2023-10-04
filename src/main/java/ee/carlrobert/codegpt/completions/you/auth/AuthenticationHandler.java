package ee.carlrobert.codegpt.completions.you.auth;

import ee.carlrobert.codegpt.completions.you.auth.response.YouAuthenticationResponse;

public interface AuthenticationHandler {

  void handleAuthenticated(YouAuthenticationResponse authenticationResponse);

  void handleGenericError();

  void handleError(YouAuthenticationError authenticationError);
}
