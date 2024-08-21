package ee.carlrobert.codegpt.actions;

import com.intellij.util.messages.Topic;
import com.intellij.util.messages.Topic.BroadcastDirection;
import java.util.EventListener;

public interface CodeCompletionEnabledListener extends EventListener {

  @Topic.AppLevel
  Topic<CodeCompletionEnabledListener> TOPIC = new Topic<>(CodeCompletionEnabledListener.class,
      BroadcastDirection.TO_DIRECT_CHILDREN);

  void onCodeCompletionsEnabledChange(boolean codeCompletionsEnabled);
}

