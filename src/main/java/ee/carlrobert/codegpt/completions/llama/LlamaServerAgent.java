package ee.carlrobert.codegpt.completions.llama;

import static java.lang.String.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessOutputType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.completions.ServerAgent;
import ee.carlrobert.codegpt.settings.service.util.ServerProgressPanel;
import ee.carlrobert.codegpt.settings.state.LlamaSettings;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service
public final class LlamaServerAgent implements ServerAgent {

  private static final Logger LOG = Logger.getInstance(LlamaServerAgent.class);

  private @Nullable OSProcessHandler makeProcessHandler;
  private @Nullable OSProcessHandler startServerProcessHandler;

  public void startAgent(
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Runnable onServerTerminated) {
    ApplicationManager.getApplication().invokeLater(() -> {
      if (!params.isUseCustomServer()) {
        try {
          serverProgressPanel.updateText(
              CodeGPTBundle.get("llamaServerAgent.buildingProject.description"));
          makeProcessHandler = new OSProcessHandler(getMakeCommandLinde());
          makeProcessHandler.addProcessListener(
              getMakeProcessListener(params, serverProgressPanel, onSuccess, onServerTerminated));
          makeProcessHandler.startNotify();
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        }
      } else {
        startServer(params, serverProgressPanel, onSuccess, onServerTerminated);
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
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Runnable onServerTerminated) {
    LOG.info("Building llama project");

    return new ProcessAdapter() {
      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        LOG.info(event.getText());
      }

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        startServer(params, serverProgressPanel, onSuccess, onServerTerminated);
      }
    };
  }

  private void startServer(
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Runnable onServerTerminated) {
    try {
      LOG.info("Booting up llama server");

      serverProgressPanel.updateText(
          CodeGPTBundle.get("llamaServerAgent.serverBootup.description"));
      startServerProcessHandler = new OSProcessHandler.Silent(getServerCommandLine(params));
      startServerProcessHandler.addProcessListener(
          getProcessListener(params.getPort(), onSuccess, onServerTerminated));
      startServerProcessHandler.startNotify();
    } catch (ExecutionException ex) {
      LOG.error("Unable to start llama server", ex);
      throw new RuntimeException(ex);
    }
  }

  private ProcessListener getProcessListener(
      int port,
      Runnable onSuccess,
      Runnable onServerTerminated) {
    return new ProcessAdapter() {
      private final ObjectMapper objectMapper = new ObjectMapper();
      private final List<String> errorLines = new CopyOnWriteArrayList<>();

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        if (errorLines.isEmpty()) {
          LOG.info(format("Server terminated with code %d", event.getExitCode()));
        } else {
          LOG.info(String.join("", errorLines));
        }

        onServerTerminated.run();
      }

      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        if (ProcessOutputType.isStderr(outputType)) {
          errorLines.add(event.getText());
          return;
        }

        if (ProcessOutputType.isStdout(outputType)) {
          LOG.info(event.getText());

          try {
            var serverMessage = objectMapper.readValue(event.getText(), LlamaServerMessage.class);
            if ("HTTP server listening".equals(serverMessage.getMessage())) {
              LOG.info("Server up and running!");

              LlamaSettings.getInstance().getState().getLocalSettings().setServerPort(port);
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

  private GeneralCommandLine getServerCommandLine(ServerStartupParams params) {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath("./" + params.getServerFileName());
    commandLine.withWorkDirectory(params.getServerDirectory());
    String modelFileName = LlamaCompletionModel.getModelPath(params.getSelectedModel());
    commandLine.addParameters(
        "-m", modelFileName,
        "-c", String.valueOf(params.getContextLength()),
        "--port", String.valueOf(params.getPort()),
        "-t", String.valueOf(params.getThreads()));
    commandLine.addParameters(params.getAdditionalParameters());
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
