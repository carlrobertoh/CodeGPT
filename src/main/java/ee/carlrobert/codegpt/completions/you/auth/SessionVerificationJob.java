package ee.carlrobert.codegpt.completions.you.auth;

import com.intellij.openapi.diagnostic.Logger;
import java.time.LocalDateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class SessionVerificationJob implements Job {

  private static final Logger LOG = Logger.getInstance(SessionVerificationJob.class);

  @Override
  public void execute(JobExecutionContext context) {
    LOG.info("Refreshing token: " + LocalDateTime.now());
    // TODO: Not implemented
  }
}
