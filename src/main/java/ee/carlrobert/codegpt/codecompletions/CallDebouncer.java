package ee.carlrobert.codegpt.codecompletions;


import static ee.carlrobert.codegpt.settings.service.ServiceType.LLAMA;

import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.settings.state.GeneralSettingsState;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.sse.EventSource;

public class CallDebouncer {

  private final Project project;
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private final ConcurrentHashMap<Object, Future<?>> delayedMap = new ConcurrentHashMap<>();
  private final AtomicReference<EventSource> currentCall = new AtomicReference<>();

  public CallDebouncer(Project project) {
    this.project = project;
  }

  /**
   * Implements a debounce mechanism for {@code callable} with a specified {@code delay}. This means
   * the callable is set to execute after the given {@code delay} period. However, if this method is
   * invoked again with the same key before the {@code delay} elapses, the scheduled execution will
   * be aborted, and therefore the previous request will be cancelled.
   */
  public void debounce(Object key, CallRunnable runnable, long delay, TimeUnit unit) {
    Future<?> prev = delayedMap.put(key, scheduler.schedule(() -> {
      try {
        cancelPreviousCall();
        var progressIndicator = LLAMA.equals(GeneralSettingsState.getInstance().getSelectedService())
            ? createProgressIndicator()
            : null;
        currentCall.set(runnable.call(progressIndicator));
      } finally {
        delayedMap.remove(key);
      }
    }, delay, unit));

    if (prev != null) {
      prev.cancel(true);
    }
  }

  public void shutdown() {
    cancelPreviousCall();
    scheduler.shutdownNow();
  }

  public void cancelPreviousCall() {
    var call = currentCall.get();
    if (call != null) {
      call.cancel();
    }
  }

  private BackgroundableProcessIndicator createProgressIndicator() {
    return new BackgroundableProcessIndicator(project,
        CodeGPTBundle.get("codeCompletion.progress.title"), null, null, true) {
      @Override
      protected void onRunningChange() {
        if (isCanceled()) {
          cancelPreviousCall();
          CodeGPTEditorManager.getInstance().disposeAllInlays(project);
        }
      }
    };
  }
}
