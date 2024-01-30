package ee.carlrobert.codegpt.codecompletions;

import okhttp3.sse.EventSource;

@FunctionalInterface
public interface CallRunnable {

  EventSource call();
}
