package ee.carlrobert.codegpt.completions.llama;

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
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import java.nio.charset.StandardCharsets;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service
public final class LlamaServerAgent {

  private static final Logger LOG = Logger.getInstance(LlamaServerAgent.class);

  private static @Nullable OSProcessHandler makeProcessHandler;
  private static @Nullable OSProcessHandler startServerProcessHandler;

  public void startAgent(
      String modelPath,
      Runnable onSuccess,
      Runnable onTerminated,
      JPanel startServerLinkWrapper) {
    ApplicationManager.getApplication().invokeLater(() -> {
      try {
        startServerLinkWrapper.removeAll();
        startServerLinkWrapper.add(JBUI.Panels.simplePanel(4, 0)
            .addToLeft(new JBLabel("Building llama.cpp..."))
            .addToRight(new AsyncProcessIcon("sign_in_spinner")));
        startServerLinkWrapper.repaint();
        startServerLinkWrapper.revalidate();

        makeProcessHandler = new OSProcessHandler(getMakeCommandLinde());
        makeProcessHandler.addProcessListener(
            getMakeProcessListener(modelPath, onSuccess, onTerminated, startServerLinkWrapper));
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
    return startServerProcessHandler != null &&
        startServerProcessHandler.isStartNotified() &&
        !startServerProcessHandler.isProcessTerminated();
  }

  private ProcessListener getMakeProcessListener(
      String modelPath,
      Runnable onSuccess,
      Runnable onTerminated,
      JPanel startServerLinkWrapper) {
    return new ProcessAdapter() {
      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        LOG.info(event.getText());
      }

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        try {
          startServerLinkWrapper.removeAll();
          startServerLinkWrapper.add(JBUI.Panels.simplePanel(4, 0)
              .addToLeft(new JBLabel("Booting up server..."))
              .addToRight(new AsyncProcessIcon("sign_in_spinner")));
          startServerLinkWrapper.repaint();
          startServerLinkWrapper.revalidate();

          startServerProcessHandler = new OSProcessHandler(getServerCommandLine(modelPath));
          startServerProcessHandler.addProcessListener(getProcessListener(onSuccess, onTerminated));
          startServerProcessHandler.startNotify();
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  private ProcessListener getProcessListener(Runnable onSuccess, Runnable onTerminated) {
    return new ProcessAdapter() {
      private final ObjectMapper objectMapper = new ObjectMapper();

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        onTerminated.run();
      }

      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        System.out.println(event.getText());

        if (outputType == ProcessOutputType.STDOUT) {
          try {
            var serverMessage = objectMapper.readValue(event.getText(), LlamaServerMessage.class);
            if ("HTTP server listening".equals(serverMessage.getMessage())) {
              onSuccess.run();
            }
          } catch (Exception ignore) {
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

  private GeneralCommandLine getServerCommandLine(String modelPath) {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath("./server");
    commandLine.withWorkDirectory(CodeGPTPlugin.getLlamaSourcePath());
    commandLine.addParameters("-m", modelPath, "-c", "2048");
    commandLine.setRedirectErrorStream(false);
    return commandLine;
  }
}
