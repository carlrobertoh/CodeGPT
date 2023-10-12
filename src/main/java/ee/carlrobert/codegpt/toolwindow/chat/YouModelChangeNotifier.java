package ee.carlrobert.codegpt.toolwindow.chat;

import com.intellij.util.messages.Topic;

public interface YouModelChangeNotifier {

  Topic<YouModelChangeNotifier> YOU_MODEL_CHANGE_NOTIFIER_TOPIC =
      Topic.create("youModelChangeTopic", YouModelChangeNotifier.class);

  void modelChanged(boolean selected);
}
