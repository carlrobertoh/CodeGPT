package ee.carlrobert.codegpt.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
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
import org.jetbrains.annotations.NotNull;

public class FileUtils {

  private static final Logger LOG = Logger.getInstance(FileUtils.class);

  public static File createFile(String directoryPath, String fileName, String fileContent) {
    try {
      tryCreateDirectory(directoryPath);
      return Files.writeString(
          Path.of(directoryPath, fileName),
          fileContent,
          StandardOpenOption.CREATE).toFile();
    } catch (IOException e) {
      throw new RuntimeException("Failed to create file", e);
    }
  }

  public static VirtualFile getEditorFile(@NotNull Editor editor) {
    return FileDocumentManager.getInstance().getFile(editor.getDocument());
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
      fileExtensionLanguageMappings = mapper.readValue(
          getResourceContent("/fileExtensionLanguageMappings.json"), new TypeReference<>() {
          });
      languageFileExtensionMappings = mapper.readValue(
          getResourceContent("/languageFileExtensionMappings.json"), new TypeReference<>() {
          });
    } catch (JsonProcessingException e) {
      LOG.error("Unable to extract file extension", e);
      return defaultValue;
    }

    return findFirstExtension(languageFileExtensionMappings, language)
        .orElseGet(() -> fileExtensionLanguageMappings.stream()
            .filter(it -> it.getExtension().equalsIgnoreCase(language))
            .findFirst()
            .map(it -> findFirstExtension(languageFileExtensionMappings, it.getValue())
                .orElse(defaultValue))
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
}
