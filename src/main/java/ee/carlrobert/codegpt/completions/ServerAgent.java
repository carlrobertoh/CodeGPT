package ee.carlrobert.codegpt.completions;

import com.intellij.openapi.Disposable;
import ee.carlrobert.codegpt.completions.llama.ServerStartupParams;
import ee.carlrobert.codegpt.settings.service.util.ServerProgressPanel;

public interface ServerAgent extends Disposable {

  void startAgent(
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Runnable onServerTerminated);

   void stopAgent();

   boolean isServerRunning();

}
