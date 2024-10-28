package ee.carlrobert.codegpt.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import ee.carlrobert.codegpt.telemetry.TelemetryAction;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;

public abstract class TrackableAction extends AnAction {

  private final ActionType actionType;

  public TrackableAction(
      String text,
      String description,
      Icon icon,
      ActionType actionType) {
    super(text, description, icon);
    this.actionType = actionType;
  }

  public abstract void handleAction(@NotNull AnActionEvent e);

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    try {
      handleAction(e);
    } catch (Exception ex) {
      TelemetryAction.IDE_ACTION_ERROR
          .createActionMessage()
          .error(ex)
          .send();
      throw ex;
    } finally {
      TelemetryAction.IDE_ACTION
          .createActionMessage()
          .property("group", null)
          .property("action", actionType.name())
          .send();
    }
  }
}