package ee.carlrobert.codegpt.util.placeholder;

public class DateReplacer implements PlaceholderReplacer {
  @Override
  public String getPlaceholderKey() {
    return "DATE_ISO_8601";
  }

  @Override
  public String getPlaceholderDescription() {
    return "Current date in ISO 8601 format, e.g. 2021-01-01.";
  }

  @Override
  public String getReplacementValue() {
    return java.time.LocalDate.now().toString();
  }
}