package ee.carlrobert.codegpt.util;

import java.util.Objects;
import org.jetbrains.deft.Obj;

public class Utils {

  public static boolean areValuesDifferent(String s1, String s2) {
    return (s1 != null && !s1.isEmpty()) ? !s1.equals(s2) : (s2 != null && !s2.isEmpty());
  }

  public static boolean areValuesDifferent(Object s1, Object s2) {
    return !Objects.equals(s1, s2);
  }
}
