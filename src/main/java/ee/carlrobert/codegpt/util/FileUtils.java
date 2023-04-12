package ee.carlrobert.codegpt.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

  public static String getResource(String name) {
    try (var stream = Objects.requireNonNull(FileUtils.class.getResourceAsStream(name))) {
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Unable to read resource", e);
    }
  }

  public static String getFileExtension(String filename) {
    Pattern pattern = Pattern.compile("[^.]+$");
    Matcher matcher = pattern.matcher(filename);

    if (matcher.find()) {
      return matcher.group();
    }
    return "";
  }
}
