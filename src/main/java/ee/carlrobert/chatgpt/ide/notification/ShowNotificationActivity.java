package ee.carlrobert.chatgpt.ide.notification;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;
import org.jetbrains.annotations.NotNull;

public class ShowNotificationActivity implements StartupActivity {

  @Override
  public void runActivity(@NotNull Project project) {
    var notificationService = ApplicationManager.getApplication().getService(NotificationService.class);
    var apiKey = SettingsState.getInstance().apiKey;
    if (apiKey == null || apiKey.isEmpty()) {
      notificationService.createAndNotify(project);
    }
  }
}
