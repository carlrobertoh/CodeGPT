package ee.carlrobert.codegpt.util;

public class Utils {
  public static boolean areStringsDifferentIgnoringEmptyOrNull(String s1, String s2){
    return (s1 != null && !s1.isEmpty()) ? !s1.equals(s2) : (s2 != null && !s2.isEmpty());
  }
}
