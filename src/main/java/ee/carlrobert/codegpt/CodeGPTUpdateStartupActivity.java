package ee.carlrobert.codegpt;

import static ee.carlrobert.codegpt.CodeGPTPlugin.CODEGPT_ID;

import com.intellij.ide.plugins.InstalledPluginsState;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.updateSettings.impl.UpdateChecker;
import com.intellij.openapi.updateSettings.impl.UpdateSettings;
import com.intellij.util.concurrency.AppExecutorUtil;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings;
import ee.carlrobert.codegpt.ui.OverlayUtil;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class CodeGPTUpdateStartupActivity implements StartupActivity.Background {

  @Override
  public void runActivity(@NotNull Project project) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      return;
    }

    schedulePluginUpdateChecks(project);
  }

  private void schedulePluginUpdateChecks(Project project) {
    AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(() -> {
      if (project != null && ConfigurationSettings.getCurrentState().isCheckForPluginUpdates()) {
        new CheckForUpdatesTask(project).queue();
      }

    }, 0, 4L, TimeUnit.HOURS);
  }

  private static class CheckForUpdatesTask extends Task.Backgroundable {

    public CheckForUpdatesTask(@NotNull Project project) {
      super(project, CodeGPTBundle.get("checkForUpdatesTask.title"), true);
    }

    private static void installCodeGPTUpdate(Project project) {
      var settingsCopy = new UpdateSettings();
      var settingsState = settingsCopy.getState();
      settingsState.copyFrom(UpdateSettings.getInstance().getState());
      settingsState.setCheckNeeded(true);
      settingsState.setPluginsCheckNeeded(true);
      settingsState.setShowWhatsNewEditor(true);
      settingsState.setThirdPartyPluginsAllowed(true);
      UpdateChecker.updateAndShowResult(project, settingsCopy);
    }

    public void run(@NotNull ProgressIndicator indicator) {
      if (!myProject.isDisposed()) {
        if (InstalledPluginsState.getInstance().hasNewerVersion(CODEGPT_ID)) {
          OverlayUtil.getDefaultNotification(
                  CodeGPTBundle.get("checkForUpdatesTask.notification.message"),
                  NotificationType.IDE_UPDATE)
              .addAction(NotificationAction.createSimpleExpiring(
                  CodeGPTBundle.get("checkForUpdatesTask.notification.installButton"),
                  () -> ApplicationManager.getApplication()
                      .executeOnPooledThread(() -> installCodeGPTUpdate(myProject))))
              .addAction(NotificationAction.createSimpleExpiring(
                  CodeGPTBundle.get("checkForUpdatesTask.notification.hideButton"),
                  () -> ConfigurationSettings.getCurrentState().setCheckForPluginUpdates(false)))
              .notify(myProject);
        }
      }
    }
  }
}
