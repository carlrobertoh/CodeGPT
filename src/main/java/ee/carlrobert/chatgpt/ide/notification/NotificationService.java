package ee.carlrobert.chatgpt.ide.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import ee.carlrobert.chatgpt.ide.settings.SettingsConfigurable;
import org.jetbrains.annotations.NotNull;

public class NotificationService {

  private Notification notification;

  public void createAndNotify(@NotNull Project project) {
    createNotification();
    notify(project);
  }

  public void expire() {
    if (notification != null) {
      notification.expire();
    }
  }

  private void notify(@NotNull Project project) {
    this.notification
        .addAction(new AnAction("Open Settings") {
          @Override
          public void actionPerformed(@NotNull AnActionEvent event) {
            DataContext dataContext = event.getDataContext();
            Project project = PlatformDataKeys.PROJECT.getData(dataContext);
            ShowSettingsUtil.getInstance().showSettingsDialog(project, SettingsConfigurable.class);
          }
        })
        .notify(project);
  }

  private void createNotification() {
    this.notification = NotificationGroupManager.getInstance()
        .getNotificationGroup("ChatGPT-Empty-API-Key")
        .createNotification("ChatGPT API key not set", NotificationType.WARNING);
  }
}
