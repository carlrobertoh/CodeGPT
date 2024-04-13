package ee.carlrobert.codegpt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

public class PlaceholderUtilTest {

  @Test
  public void testReplaceDatePlaceholder() {
    String template = "Today is {DATE_ISO_8601}";
    String expected = "Today is " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    String actual = new PlaceholderUtil().replacePlaceholder(template);
    assertEquals(expected, actual,
        "The placeholder should be replaced with the correct date");
  }

  @Test
  public void testReplaceBranchNamePlaceholder() {
    String template = "Branch name is {BRANCH_NAME}";
    String expected = "Branch name is BRANCH-UNKNOWN";
    String actual = new PlaceholderUtil().replacePlaceholder(template);
    assertEquals(expected, actual,
        "The placeholder should be replaced with the BranchName, unknown during testing");
  }
}