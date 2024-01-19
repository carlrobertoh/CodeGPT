package ee.carlrobert.codegpt.codecompletions;

public class InfillRequestDetails {

  private final String prefix;
  private final String suffix;

  public InfillRequestDetails(String prefix, String suffix) {
    this.prefix = prefix;
    this.suffix = suffix;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getSuffix() {
    return suffix;
  }
}
