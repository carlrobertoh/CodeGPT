package ee.carlrobert.codegpt.util.file;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import ee.carlrobert.codegpt.CodeGPTPlugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
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

  public static void copyFileWithProgress(
      String fileName,
      URL url,
      long[] bytesRead,
      long fileSize,
      ProgressIndicator indicator) throws IOException {
    FileUtils.tryCreateDirectory(CodeGPTPlugin.getLlamaModelsPath());

    try (
        var readableByteChannel = Channels.newChannel(url.openStream());
        var fileOutputStream = new FileOutputStream(
            CodeGPTPlugin.getLlamaModelsPath() + File.separator + fileName)) {
      var buffer = ByteBuffer.allocateDirect(1024 * 10);

      while (readableByteChannel.read(buffer) != -1) {
        if (indicator.isCanceled()) {
          readableByteChannel.close();
          break;
        }
        buffer.flip();
        bytesRead[0] += fileOutputStream.getChannel().write(buffer);
        buffer.clear();
        indicator.setFraction((double) bytesRead[0] / fileSize);
      }
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

  public static Map.Entry<String, String> findLanguageExtensionMapping(String language) {
    var defaultValue = Map.entry("Text", ".txt");
    var mapper = new ObjectMapper();

    List<FileExtensionLanguageDetails> extensionToLanguageMappings;
    List<LanguageFileExtensionDetails> languageToExtensionMappings;
    try {
      extensionToLanguageMappings = mapper.readValue(
          getResourceContent("/fileExtensionLanguageMappings.json"), new TypeReference<>() {
          });
      languageToExtensionMappings = mapper.readValue(
          getResourceContent("/languageFileExtensionMappings.json"), new TypeReference<>() {
          });
    } catch (JsonProcessingException e) {
      LOG.error("Unable to extract file extension", e);
      return defaultValue;
    }

    return findFirstExtension(languageToExtensionMappings, language)
        .orElseGet(() -> extensionToLanguageMappings.stream()
            .filter(it -> it.getExtension().equalsIgnoreCase(language))
            .findFirst()
            .map(it -> findFirstExtension(languageToExtensionMappings, it.getValue())
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

  public static String convertFileSize(long fileSizeInBytes) {
    String[] units = {"B", "KB", "MB", "GB"};
    int unitIndex = 0;
    double fileSize = fileSizeInBytes;

    while (fileSize >= 1024 && unitIndex < units.length - 1) {
      fileSize /= 1024;
      unitIndex++;
    }

    return new DecimalFormat("#.##").format(fileSize) + " " + units[unitIndex];
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
