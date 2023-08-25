package ee.carlrobert.codegpt.user.auth;

import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.user.UserManager;
import java.time.LocalDateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class SessionVerificationJob implements Job {

  private static final Logger LOG = Logger.getInstance(SessionVerificationJob.class);

  @Override
  public void execute(JobExecutionContext context) {
    LOG.info("Refreshing token: " + LocalDateTime.now());

    var session = UserManager.getInstance().getSession();
    if (session != null && session.isExpired()) {
      AuthenticationService.getInstance().refreshToken();
    }
  }
}
