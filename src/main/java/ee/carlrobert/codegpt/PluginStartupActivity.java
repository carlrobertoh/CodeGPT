package ee.carlrobert.codegpt;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.startup.StartupActivity;
import ee.carlrobert.codegpt.actions.editor.EditorActionsUtil;
import ee.carlrobert.codegpt.credentials.UserCredentialsManager;
import ee.carlrobert.codegpt.settings.SettingsState;
import ee.carlrobert.codegpt.user.UserManager;
import ee.carlrobert.codegpt.user.auth.AuthenticationHandler;
import ee.carlrobert.codegpt.user.auth.AuthenticationService;
import ee.carlrobert.codegpt.user.auth.SessionVerificationJob;
import ee.carlrobert.codegpt.util.OverlayUtils;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class PluginStartupActivity implements StartupActivity {

  private static final Logger LOG = Logger.getInstance(PluginStartupActivity.class);
  private Scheduler scheduler;

  @Override
  public void runActivity(@NotNull Project project) {
    EditorActionsUtil.refreshActions();

    var userManager = UserManager.getInstance();
    var session = userManager.getSession();
    if (session == null || session.isExpired()) {
      handleAuthentication(project);
    } else {
      startSessionVerificationJob();
    }

    ProjectManager.getInstance().addProjectManagerListener(project, new ProjectManagerListener() {
      @Override
      public void projectClosing(@NotNull Project project) {
        if (scheduler != null) {
          try {
            scheduler.shutdown();
          } catch (SchedulerException e) {
            LOG.error("Could not shut down scheduler.", e);
          }
        }
      }
    });
  }

  private void startSessionVerificationJob() {
    try {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.start();

      var job = JobBuilder.newJob(SessionVerificationJob.class)
          .withIdentity("authenticationVerifierJob", "authenticationVerifier")
          .build();
      var trigger = TriggerBuilder.newTrigger()
          .forJob(job)
          .withIdentity("authenticationVerifierTrigger", "authenticationVerifier")
          .withSchedule(SimpleScheduleBuilder.simpleSchedule()
              .withIntervalInMinutes(30)
              .repeatForever())
          .startNow()
          .build();

      scheduler.scheduleJob(job, trigger);
    } catch (SchedulerException e) {
      LOG.error("Couldn't start authentication verifier job", e);
    }
  }

  private void handleAuthentication(Project project) {
    var settings = SettingsState.getInstance();
    if (settings == null || !settings.previouslySignedIn) {
      return;
    }

    var password = UserCredentialsManager.getInstance().getAccountPassword();
    if (!settings.email.isEmpty() && password != null && !password.isEmpty()) {
      AuthenticationService.getInstance()
          .signInAsync(settings.email, password, new AuthenticationHandler() {
            @Override
            public void handleAuthenticated() {
              OverlayUtils.showNotification("Authentication successful.", NotificationType.INFORMATION);
              startSessionVerificationJob();
            }

            @Override
            public void handleInvalidCredentials() {
              OverlayUtils.showNotification("Authentication unsuccessful. Invalid credentials provided.", NotificationType.ERROR);
            }

            @Override
            public void handleGenericError() {
              OverlayUtils.showNotification("Something went wrong while trying to authenticate.", NotificationType.ERROR);
            }
          });
    }
  }
}
