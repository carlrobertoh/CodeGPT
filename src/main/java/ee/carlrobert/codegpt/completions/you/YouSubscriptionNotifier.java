package ee.carlrobert.codegpt.completions.you;

import com.intellij.util.messages.Topic;

public interface YouSubscriptionNotifier {

  Topic<YouSubscriptionNotifier> SUBSCRIPTION_TOPIC =
      Topic.create("subscriptionTopic", YouSubscriptionNotifier.class);

  void subscribed();
}