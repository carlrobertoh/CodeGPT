package ee.carlrobert.codegpt.completions.you.auth;

import com.intellij.util.messages.Topic;

public interface SignedOutNotifier {

  Topic<SignedOutNotifier> SIGNED_OUT_TOPIC =
      Topic.create("signedOutTopic", SignedOutNotifier.class);

  void signedOut();
}