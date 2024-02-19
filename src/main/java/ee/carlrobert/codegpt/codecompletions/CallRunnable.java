package ee.carlrobert.codegpt.codecompletions;

import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface CallRunnable {

  EventSource call(@Nullable BackgroundableProcessIndicator progressIndicator);
}
