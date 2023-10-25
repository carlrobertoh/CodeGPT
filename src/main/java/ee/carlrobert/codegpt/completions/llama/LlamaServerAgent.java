package ee.carlrobert.codegpt.completions.llama;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.process.ProcessOutputType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.JBUI;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import java.nio.charset.StandardCharsets;
import javax.swing.JPanel;
import org.jetbrains.annotations.NotNull;

public class LlamaServerAgent {

  private static final Logger LOG = Logger.getInstance(LlamaServerAgent.class);

  private final String customModelPath;

  public LlamaServerAgent(String customModelPath) {
    this.customModelPath = customModelPath;
  }

  public void startAgent(String modelPath, Runnable onSuccess, JPanel startServerLinkWrapper) {
    ApplicationManager.getApplication().invokeLater(() -> {
      try {
        startServerLinkWrapper.removeAll();
        startServerLinkWrapper.add(JBUI.Panels.simplePanel(4, 0)
            .addToLeft(new JBLabel("Building llama.cpp..."))
            .addToRight(new AsyncProcessIcon("sign_in_spinner")));
        startServerLinkWrapper.repaint();
        startServerLinkWrapper.revalidate();

        var process = new OSProcessHandler(getMakeCommandLinde());
        process.addProcessListener(
            getMakeProcessListener(modelPath, onSuccess, startServerLinkWrapper));
        process.startNotify();
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private ProcessListener getMakeProcessListener(
      String modelPath,
      Runnable onSuccess,
      JPanel startServerLinkWrapper) {
    return new ProcessAdapter() {
      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        LOG.info(event.getText());
      }

      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        ProcessHandler processHandler;
        try {
          startServerLinkWrapper.removeAll();
          startServerLinkWrapper.add(JBUI.Panels.simplePanel(4, 0)
              .addToLeft(new JBLabel("Booting up server..."))
              .addToRight(new AsyncProcessIcon("sign_in_spinner")));
          startServerLinkWrapper.repaint();
          startServerLinkWrapper.revalidate();

          processHandler = new OSProcessHandler(getServerCommandLine(modelPath));
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        }
        processHandler.addProcessListener(getProcessListener(onSuccess));
        processHandler.startNotify();
      }
    };
  }

  private ProcessListener getProcessListener(Runnable onSuccess) {
    return new ProcessAdapter() {
      private final ObjectMapper objectMapper = new ObjectMapper();

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

  private GeneralCommandLine getServerCommandLine(String fileName) {
    GeneralCommandLine commandLine = new GeneralCommandLine().withCharset(StandardCharsets.UTF_8);
    commandLine.setExePath("./server");
    commandLine.withWorkDirectory(CodeGPTPlugin.getLlamaSourcePath());

    var modelPath = StringUtil.isEmpty(customModelPath) ? "models/" + fileName : customModelPath;
    commandLine.addParameters("-m", modelPath, "-c", "2048");
    commandLine.setRedirectErrorStream(false);
    return commandLine;
  }
}
