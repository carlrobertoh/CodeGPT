package ee.carlrobert.codegpt.completions.you.auth;

import com.intellij.util.messages.Topic;

public interface AuthenticationNotifier {

  Topic<AuthenticationNotifier> AUTHENTICATION_TOPIC =
      Topic.create("authenticationTopic", AuthenticationNotifier.class);

  void authenticationSuccessful();
}
