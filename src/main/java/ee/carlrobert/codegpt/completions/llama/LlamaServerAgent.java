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
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Key;
import ee.carlrobert.codegpt.CodeGPTBundle;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import ee.carlrobert.codegpt.settings.service.llama.LlamaSettings;
import ee.carlrobert.codegpt.settings.service.llama.form.ServerProgressPanel;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service
public final class LlamaServerAgent implements Disposable {

  private static final Logger LOG = Logger.getInstance(LlamaServerAgent.class);

  private @Nullable OSProcessHandler makeProcessHandler;
  private @Nullable OSProcessHandler startServerProcessHandler;
  private ServerProgressPanel activeServerProgressPanel;
  private boolean stoppedByUser;

  public void startAgent(
      LlamaServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Consumer<ServerProgressPanel> onServerStopped) {
    this.activeServerProgressPanel = serverProgressPanel;
    ApplicationManager.getApplication().invokeLater(() -> {
      try {
        stoppedByUser = false;
        serverProgressPanel.displayText(
            CodeGPTBundle.get("llamaServerAgent.buildingProject.description"));
        makeProcessHandler = new OSProcessHandler(
            getMakeCommandLine(params));
        makeProcessHandler.addProcessListener(
            getMakeProcessListener(params, onSuccess, onServerStopped));
        makeProcessHandler.startNotify();
      } catch (ExecutionException e) {
        showServerError(e.getMessage(), onServerStopped);
      }
    });
  }

  public void stopAgent() {
    stoppedByUser = true;
    if (makeProcessHandler != null) {
      makeProcessHandler.destroyProcess();
    }
    if (startServerProcessHandler != null) {
      startServerProcessHandler.destroyProcess();
    }
  }

  public boolean isServerRunning() {
    return (makeProcessHandler != null
        && makeProcessHandler.isStartNotified()
        && !makeProcessHandler.isProcessTerminated())
        || (startServerProcessHandler != null
        && startServerProcessHandler.isStartNotified()
        && !startServerProcessHandler.isProcessTerminated());
  }

  private ProcessListener getMakeProcessListener(
      LlamaServerStartupParams params,
      Runnable onSuccess,
      Consumer<ServerProgressPanel> onServerStopped) {
    LOG.info("Building llama project");

    return new ProcessAdapter() {

      private final List<String> errorLines = new CopyOnWriteArrayList<>();

      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        if (ProcessOutputType.isStderr(outputType)) {
          errorLines.add(event.getText());
          return;
        }
        LOG.info(event.getText());
      }

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        int exitCode = event.getExitCode();
        LOG.info(format("Server build exited with code %d", exitCode));
        if (stoppedByUser) {
          onServerStopped.accept(activeServerProgressPanel);
          return;
        }
        if (exitCode != 0) {
          showServerError(String.join(",", errorLines), onServerStopped);
          return;
        }

        try {
          LOG.info("Booting up llama server");

          activeServerProgressPanel.displayText(
              CodeGPTBundle.get("llamaServerAgent.serverBootup.description"));
          startServerProcessHandler = new OSProcessHandler.Silent(getServerCommandLine(params));
          startServerProcessHandler.addProcessListener(
              getProcessListener(params.port(), onSuccess, onServerStopped));
          startServerProcessHandler.startNotify();
        } catch (ExecutionException ex) {
          showServerError(ex.getMessage(), onServerStopped);
        }
      }
    };
  }

  private ProcessListener getProcessListener(
      int port,
      Runnable onSuccess,
      Consumer<ServerProgressPanel> onServerStopped) {
    return new ProcessAdapter() {
      private final ObjectMapper objectMapper = new ObjectMapper();
      private final List<String> errorLines = new CopyOnWriteArrayList<>();

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        LOG.info(format("Server stopped with code %d", event.getExitCode()));
        if (stoppedByUser) {
          onServerStopped.accept(activeServerProgressPanel);
        } else {
          showServerError(String.join(",", errorLines), onServerStopped);
        }
      }

      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        if (ProcessOutputType.isStderr(outputType)) {
          errorLines.add(event.getText());
        }

        if (ProcessOutputType.isStdout(outputType)) {
          LOG.info(event.getText());

          try {
            var serverMessage = objectMapper.readValue(event.getText(), LlamaServerMessage.class);
            // hack
            if ("HTTP server listening".equals(serverMessage.msg())) {
              LOG.info("Server up and running!");

              LlamaSettings.getCurrentState().setServerPort(port);
              onSuccess.run();
            }
          } catch (Exception ignore) {
            // ignore
          }
        }
      }
    };
  }

  private void showServerError(String errorText, Consumer<ServerProgressPanel> onServerStopped) {
    onServerStopped.accept(activeServerProgressPanel);
    LOG.info("Unable to start llama server:\n" + errorText);
    OverlayUtil.showClosableBalloon(errorText, MessageType.ERROR, activeServerProgressPanel);
  }

  private static GeneralCommandLine getMakeCommandLine(LlamaServerStartupParams params) {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath("make");
    commandLine.withWorkDirectory(CodeGPTPlugin.getLlamaSourcePath());
    commandLine.addParameters("-j");
    commandLine.addParameters(params.additionalBuildParameters());
    commandLine.withEnvironment(params.additionalEnvironmentVariables());
    commandLine.setRedirectErrorStream(false);
    return commandLine;
  }

  private GeneralCommandLine getServerCommandLine(LlamaServerStartupParams params) {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath("./server");
    commandLine.withWorkDirectory(CodeGPTPlugin.getLlamaSourcePath());
    commandLine.addParameters(
        "-m", params.modelPath(),
        "-c", String.valueOf(params.contextLength()),
        "--port", String.valueOf(params.port()),
        "-t", String.valueOf(params.threads()));
    commandLine.addParameters(params.additionalRunParameters());
    commandLine.withEnvironment(params.additionalEnvironmentVariables());
    commandLine.setRedirectErrorStream(false);
    return commandLine;
  }

  public void setActiveServerProgressPanel(
      ServerProgressPanel activeServerProgressPanel) {
    this.activeServerProgressPanel = activeServerProgressPanel;
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
