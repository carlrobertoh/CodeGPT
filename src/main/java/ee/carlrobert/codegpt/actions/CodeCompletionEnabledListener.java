package ee.carlrobert.codegpt.actions;

import com.intellij.util.messages.Topic;
import com.intellij.util.messages.Topic.BroadcastDirection;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;
import java.util.EventListener;

/**
 * {@link EventListener} for changes of {@link ConfigurationState#isCodeCompletionsEnabled()}.
 *
 * @see EnableCompletionsAction
 * @see DisableCompletionsAction
 */
public interface CodeCompletionEnabledListener extends EventListener {

  /**
   * Topic for subscribing to {@link ConfigurationState#isCodeCompletionsEnabled()} changes.<br/>
   * Broadcasts from Application-Level to all projects.
   */
  @Topic.AppLevel
  Topic<CodeCompletionEnabledListener> TOPIC = new Topic<>(CodeCompletionEnabledListener.class,
      BroadcastDirection.TO_DIRECT_CHILDREN);

  void onCodeCompletionsEnabledChange(boolean codeCompletionsEnabled);
}

