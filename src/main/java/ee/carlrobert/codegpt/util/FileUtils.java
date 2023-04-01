package ee.carlrobert.codegpt.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

  public static String getFileExtension(String filename) {
    Pattern pattern = Pattern.compile("[^.]+$");
    Matcher matcher = pattern.matcher(filename);

    if (matcher.find()) {
      return matcher.group();
    }
    return "";
  }
}
