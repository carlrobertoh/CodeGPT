package ee.carlrobert.codegpt.completions.ollama;

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
import ee.carlrobert.codegpt.completions.ServerAgent;
import ee.carlrobert.codegpt.completions.llama.LlamaCompletionModel;
import ee.carlrobert.codegpt.completions.llama.ServerStartupParams;
import ee.carlrobert.codegpt.settings.service.util.ServerProgressPanel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service
public final class OllamaServerAgent implements ServerAgent {

  private static final Logger LOG = Logger.getInstance(OllamaServerAgent.class);

  private @Nullable OSProcessHandler pullModelProcessHandler;
  private @Nullable OSProcessHandler startServerProcessHandler;
  private @Nullable OSProcessHandler runModelProcessHandler;

  public void startAgent(
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Runnable onServerTerminated) {
    ApplicationManager.getApplication().invokeLater(() -> {
      checkOllamaModelDownloaded(params, serverProgressPanel, onSuccess, onServerTerminated);
    });
  }

  private void checkOllamaModelDownloaded(
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Runnable onServerTerminated) {
    try {
      serverProgressPanel.updateText("Pull Ollama model if needed...");
      pullModelProcessHandler = new OSProcessHandler(getOllamaPullCmdLine(params));
      pullModelProcessHandler.addProcessListener(
          getOllamaDownloadProcessListener(params, serverProgressPanel, onSuccess,
              onServerTerminated));
      pullModelProcessHandler.startNotify();
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private static GeneralCommandLine getOllamaPullCmdLine(ServerStartupParams params) {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath("ollama");
//    commandLine.withWorkDirectory(CodeGPTPlugin.getLlamaSourcePath());
    String modelId = LlamaCompletionModel.getOllamaId(params.getSelectedModel());
    commandLine.addParameters("pull", modelId);
    commandLine.setRedirectErrorStream(false);
    return commandLine;
  }

  private ProcessListener getOllamaDownloadProcessListener(
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Runnable onServerTerminated) {
    LOG.info("Downloading ollama model");

    return new ProcessAdapter() {
      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        LOG.info(event.getText());
      }

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        startUpOllamaServer(params, serverProgressPanel, onSuccess, onServerTerminated);
      }

    };
  }

  private void startUpOllamaServer(
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Runnable onServerTerminated) {
    try {
      LOG.info("Booting up ollama server");

      serverProgressPanel.updateText("Starting up ollama server");
      startServerProcessHandler = new OSProcessHandler.Silent(getServerCommandLine(params));
      startServerProcessHandler.addProcessListener(
          getStartUpListener(params, serverProgressPanel, onSuccess, onServerTerminated));
      startServerProcessHandler.startNotify();
    } catch (ExecutionException ex) {
      LOG.error("Unable to start llama server", ex);
      throw new RuntimeException(ex);
    }
  }


  private ProcessListener getStartUpListener(
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
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
            if (event.getText().contains("INFO Listening on")) {
              LOG.info("Server up and running!");
              //TODO
//              OllamaSettingsState.getInstance().setServerPort(params.getPort());
              runOllamaModel(params, serverProgressPanel, onSuccess, onServerTerminated);
              // TODO
//              onSuccess.run();
            }
          } catch (Exception ignore) {
            // ignore
          }
        }
      }
    };
  }

  private void runOllamaModel(
      ServerStartupParams params,
      ServerProgressPanel serverProgressPanel,
      Runnable onSuccess,
      Runnable onServerTerminated) {
    try {
      LOG.info("Running ollama model");
      serverProgressPanel.updateText("Running ollama model");
      runModelProcessHandler = new OSProcessHandler.Silent(getRunModelCommandLine(params));
      runModelProcessHandler.addProcessListener(
          getModelAndServerReadyListener(onSuccess, onServerTerminated));
      runModelProcessHandler.startNotify();
    } catch (ExecutionException ex) {
      LOG.error("Unable to start llama server", ex);
      throw new RuntimeException(ex);
    }
  }

  private ProcessListener getModelAndServerReadyListener(Runnable onSuccess,
      Runnable onServerTerminated) {
    return new ProcessAdapter() {
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
            // TODO validate ollama run cmd worked?
            onSuccess.run();
          } catch (Exception ignore) {
            // ignore
          }
        }
      }
    };
  }


  private GeneralCommandLine getRunModelCommandLine(ServerStartupParams params) {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath(getOllamaCmd(params.getPort()));
//    commandLine.withWorkDirectory(CodeGPTPlugin.getLlamaSourcePath());
    String modelId = LlamaCompletionModel.getOllamaId(params.getSelectedModel());
    commandLine.addParameters("run", modelId);
    commandLine.setRedirectErrorStream(false);
    return commandLine;
  }

  public void stopAgent() {
    if (startServerProcessHandler != null) {
      startServerProcessHandler.destroyProcess();
    }
    if (runModelProcessHandler != null) {
      runModelProcessHandler.destroyProcess();
    }
  }

  public boolean isServerRunning() {
    return startServerProcessHandler != null
        && startServerProcessHandler.isStartNotified()
        && !startServerProcessHandler.isProcessTerminated()
        && runModelProcessHandler != null
        && runModelProcessHandler.isStartNotified()
        && !runModelProcessHandler.isProcessTerminated();
  }


  private GeneralCommandLine getServerCommandLine(ServerStartupParams params) {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath(getOllamaCmd(params.getPort()));
//    commandLine.withWorkDirectory(CodeGPTPlugin.getLlamaSourcePath());
    commandLine.addParameters("serve");
    commandLine.addParameters(params.getAdditionalParameters());
    commandLine.setRedirectErrorStream(false);
    return commandLine;
  }

  private static String getOllamaCmd(Integer port) {
    return String.format("OLLAMA_HOST=127.0.0.1:%d ollama", port);
  }

  @Override
  public void dispose() {
    if (pullModelProcessHandler != null && !pullModelProcessHandler.isProcessTerminated()) {
      pullModelProcessHandler.destroyProcess();
    }
    if (startServerProcessHandler != null && !startServerProcessHandler.isProcessTerminated()) {
      startServerProcessHandler.destroyProcess();
    }
    if (runModelProcessHandler != null && !runModelProcessHandler.isProcessTerminated()) {
      runModelProcessHandler.destroyProcess();
    }
  }
}
