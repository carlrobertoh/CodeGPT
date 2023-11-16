package ee.carlrobert.codegpt.completions.llama;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessOutputType;
import com.intellij.icons.AllIcons.Actions;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.ui.components.JBLabel;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.settings.service.ServerProgressPanel;
import ee.carlrobert.codegpt.settings.state.LlamaSettingsState;
import java.nio.charset.StandardCharsets;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service
public final class LlamaServerAgent implements Disposable {

  private static final Logger LOG = Logger.getInstance(LlamaServerAgent.class);

  private static @Nullable OSProcessHandler makeProcessHandler;
  private static @Nullable OSProcessHandler startServerProcessHandler;

  public void startAgent(
      String modelPath,
      int contextLength,
      int port,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess) {
    ApplicationManager.getApplication().invokeLater(() -> {
      try {
        serverProgressPanel.updateText("Building llama.cpp...");
        makeProcessHandler = new OSProcessHandler(getMakeCommandLinde());
        makeProcessHandler.addProcessListener(
            getMakeProcessListener(modelPath, contextLength, port, serverProgressPanel, onSuccess));
        makeProcessHandler.startNotify();
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void stopAgent() {
    if (startServerProcessHandler != null) {
      startServerProcessHandler.destroyProcess();
    }
  }

  public boolean isServerRunning() {
    return startServerProcessHandler != null
        && startServerProcessHandler.isStartNotified()
        && !startServerProcessHandler.isProcessTerminated();
  }

  private ProcessListener getMakeProcessListener(
      String modelPath,
      int contextLength,
      int port,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess) {
    return new ProcessAdapter() {
      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        LOG.info(event.getText());
      }

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        try {
          serverProgressPanel.updateText("Booting up server...");
          startServerProcessHandler = new OSProcessHandler(
              getServerCommandLine(modelPath, contextLength, port));
          startServerProcessHandler.addProcessListener(
              getProcessListener(port, serverProgressPanel, onSuccess));
          startServerProcessHandler.startNotify();
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  private ProcessListener getProcessListener(
      int port,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess) {
    return new ProcessAdapter() {
      private final ObjectMapper objectMapper = new ObjectMapper();

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        serverProgressPanel.displayComponent(new JBLabel(
            "Server terminated",
            Actions.Cancel,
            SwingConstants.LEADING));
      }

      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        LOG.debug(event.getText());

        if (outputType == ProcessOutputType.STDOUT) {
          try {
            var serverMessage = objectMapper.readValue(event.getText(), LlamaServerMessage.class);
            if ("HTTP server listening".equals(serverMessage.getMessage())) {
              LlamaSettingsState.getInstance().setServerPort(port);
              onSuccess.run();
            }
          } catch (Exception ignore) {
            // ignore
          }
        }
      }
    };
  }

  private static GeneralCommandLine getMakeCommandLinde() {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath("make");
    commandLine.withWorkDirectory(CodeGPTPlugin.getLlamaSourcePath());
    commandLine.addParameters("-j");
    commandLine.setRedirectErrorStream(false);
    return commandLine;
  }

  private GeneralCommandLine getServerCommandLine(String modelPath, int contextLength, int port) {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath("./server");
    commandLine.withWorkDirectory(CodeGPTPlugin.getLlamaSourcePath());
    commandLine.addParameters(
        "-m", modelPath,
        "-c", String.valueOf(contextLength),
        "--port", String.valueOf(port));
    commandLine.setRedirectErrorStream(false);
    return commandLine;
  }

  @Override
  public void dispose() {
    if (makeProcessHandler != null && !makeProcessHandler.isProcessTerminated()) {
      makeProcessHandler.destroyProcess();
    }
    if (startServerProcessHandler != null && !startServerProcessHandler.isProcessTerminated()) {
      startServerProcessHandler.destroyProcess();
    }
  }
}
