package ee.carlrobert.chatgpt;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import ee.carlrobert.chatgpt.settings.SettingsConfigurable;
import ee.carlrobert.chatgpt.settings.SettingsState;
import org.jetbrains.annotations.NotNull;

public class ShowNotificationActivity implements StartupActivity {

  @Override
  public void runActivity(@NotNull Project project) {
    var secretKey = SettingsState.getInstance().secretKey;
    if (secretKey == null || secretKey.isEmpty()) {
      NotificationGroupManager.getInstance()
          .getNotificationGroup("ChatGPT-Empty-API-Key")
          .createNotification("ChatGPT API key not set", NotificationType.WARNING)
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
  }
}
