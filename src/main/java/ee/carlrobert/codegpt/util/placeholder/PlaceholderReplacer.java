package ee.carlrobert.codegpt.util.placeholder;

public interface PlaceholderReplacer {
  String getPlaceholderKey();

  String getPlaceholderDescription();

  String getReplacementValue();
}