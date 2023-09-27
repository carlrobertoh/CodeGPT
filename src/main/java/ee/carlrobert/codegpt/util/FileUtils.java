package ee.carlrobert.codegpt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtilRt;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

  private static final Logger LOG = Logger.getInstance(FileUtils.class);

  public static File createFile(String directoryPath, String fileName, String fileContent) {
    try {
      tryCreateDirectory(directoryPath);
      return Files.writeString(Path.of(directoryPath, fileName), fileContent, StandardOpenOption.CREATE).toFile();
    } catch (IOException e) {
      throw new RuntimeException("Failed to create file", e);
    }
  }

  public static void tryCreateDirectory(String directoryPath) {
    try {
      if (!FileUtil.exists(directoryPath)) {
        if (!FileUtil.createDirectory(Path.of(directoryPath).toFile())) {
          throw new IOException("Failed to create directory: " + directoryPath);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to create directory", e);
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

  public static Map.Entry<String, String> findFileNameExtensionMapping(String language) {
    var defaultValue = Map.entry("Text", ".txt");
    var mapper = new ObjectMapper();

    List<FileExtensionLanguageDetails> fileExtensionLanguageMappings;
    List<LanguageFileExtensionDetails> languageFileExtensionMappings;
    try {
      fileExtensionLanguageMappings = mapper.readValue(getResourceContent("/fileExtensionLanguageMappings.json"), new TypeReference<>() {});
      languageFileExtensionMappings = mapper.readValue(getResourceContent("/languageFileExtensionMappings.json"), new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      LOG.error("Unable to extract file extension", e);
      return defaultValue;
    }

    return findFirstExtension(languageFileExtensionMappings, language)
        .orElseGet(() -> fileExtensionLanguageMappings.stream()
            .filter(it -> it.getExtension().equalsIgnoreCase(language))
            .findFirst()
            .map(it -> findFirstExtension(languageFileExtensionMappings, it.getValue()).orElse(defaultValue))
            .orElse(defaultValue));
  }

  public static boolean isUtf8File(String filePath) {
    var path = Paths.get(filePath);
    try (var reader = Files.newBufferedReader(path)) {
      int c = reader.read();
      if (c >= 0) {
        reader.transferTo(Writer.nullWriter());
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static String getResourceContent(String name) {
    try (var stream = Objects.requireNonNull(FileUtils.class.getResourceAsStream(name))) {
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Unable to read resource", e);
    }
  }

  private static Optional<Map.Entry<String, String>> findFirstExtension(
      List<LanguageFileExtensionDetails> languageFileExtensionMappings,
      String language) {
    return languageFileExtensionMappings.stream()
        .filter(item -> language.equalsIgnoreCase(item.getName()))
        .findFirst()
        .map(it -> Map.entry(it.getName(), it.getExtensions().get(0)));
  }

  public static CharSequence getName(@NotNull String fileName, @Nullable String defaultValue) {
    int index = StringUtilRt.lastIndexOf(fileName, '.', 0, fileName.length());
    if (index < 0) {
        return defaultValue;
    }
    return fileName.subSequence(0, index);
  }

  public static void replaceContentInFile(String from, String to, String input, String replace, int startLine, int endLine) {
    if (input.indexOf("\r\n") > 0) {
      input = input.replaceAll("\r\n", System.lineSeparator());
    } else if (input.indexOf("\n\r") > 0) {
      input = input.replaceAll("\n\r", System.lineSeparator());
    } else if (input.indexOf("\n") > 0) {
      input = input.replaceAll("\n", System.lineSeparator());
    } else if (input.indexOf("\r") > 0) {
      input = input.replaceAll("\r", System.lineSeparator());
    }
    if (replace.indexOf("\r\n") > 0) {
        replace = replace.replaceAll("\r\n", System.lineSeparator());
    } else if (replace.indexOf("\n\r") > 0) {
        replace = replace.replaceAll("\n\r", System.lineSeparator());
    } else if (replace.indexOf("\n") > 0) {
        replace = replace.replaceAll("\n", System.lineSeparator());
    } else if (replace.indexOf("\r") > 0) {
        replace = replace.replaceAll("\r", System.lineSeparator());
    }
    try (FileReader fr = new FileReader(from, StandardCharsets.UTF_8);
         BufferedReader br = new BufferedReader(fr);
         FileWriter fw = new FileWriter(to, StandardCharsets.UTF_8)
    ) {
        StringBuilder totalStr = new StringBuilder();
        StringBuilder toReplace = new StringBuilder();
        int lineNumber = 0;
        startLine++;
        endLine++;
        String s;
        while ((s = br.readLine()) != null || lineNumber < endLine) {
            s = s == null ? "" : s;
            lineNumber++;
            if (lineNumber >= startLine && lineNumber < endLine) {
                toReplace.append(s).append(System.lineSeparator());
                continue;
            }
            if (lineNumber == endLine) {
                toReplace.append(s).append(System.lineSeparator());
                if(StringUtils.isNoneBlank(input)) {
                    replace = toReplace.toString().replace(input, replace);
                }
                totalStr.append(replace);
                continue;
            }
            totalStr.append(s).append(System.lineSeparator());
        }
        fw.write(totalStr.toString());
    } catch (Exception e) {
        throw new RuntimeException("replace content failed!");
    }
  }

  public static String addWriteSpace(String text, String selectedText) {
    boolean flag = true;
    for (String str : text.split("\n")) {
        if (StringUtils.isBlank(str)) {
            continue;
        }
        if (str.startsWith(" ")) {
            flag = false;
        }
        break;
    }
    if (!flag){
      return text;
    }
    int leadingSpaceCount = 0;
    String newText = text;
    for (String str : selectedText.split("\n")) {
        if (StringUtils.isBlank(str)) {
            continue;
        }
        if (!str.startsWith(" ")) {
            break;
        }
        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) {
                leadingSpaceCount++;
            } else {
                break;
            }
            break;
        }
    }
    if (leadingSpaceCount > 0) {
        StringBuilder spaces = new StringBuilder();
        spaces.append(" ".repeat(leadingSpaceCount));
        newText = spaces + newText.replaceAll("\n", "\n" + spaces);
    }
    return newText;
  }
}
