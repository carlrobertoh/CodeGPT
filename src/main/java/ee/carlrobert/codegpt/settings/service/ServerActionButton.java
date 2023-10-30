package ee.carlrobert.codegpt.settings.service;

import com.intellij.icons.AllIcons.Actions;
import javax.swing.JButton;

public class ServerActionButton extends JButton {

  private boolean serverRunning;

  public ServerActionButton(Runnable onStart, Runnable onStop, boolean serverRunning) {
    this.serverRunning = serverRunning;
    addActionListener(e -> {
      if (this.serverRunning) {
        handleStop(onStop);
        return;
      }

      handleStart(onStart);
    });
    updateComponent();
  }

  private void handleStop(Runnable onStop) {
    serverRunning = false;
    updateComponent();
    onStop.run();
  }

  private void handleStart(Runnable onStart) {
    serverRunning = true;
    updateComponent();
    onStart.run();
  }

  private void updateComponent() {
    setText(serverRunning ? "Stop Server" : "Start Server");
    setIcon(serverRunning ? Actions.Suspend : Actions.Execute);
    setEnabled(true);
  }
}
