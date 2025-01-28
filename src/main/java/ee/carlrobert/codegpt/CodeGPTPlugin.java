package ee.carlrobert.codegpt;

import static java.io.File.separator;
import static java.util.Objects.requireNonNull;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

public final class CodeGPTPlugin {

  public static final PluginId CODEGPT_ID = PluginId.getId("ee.carlrobert.chatgpt");

  private CodeGPTPlugin() {
  }

  public static @NotNull String getVersion() {
    return requireNonNull(PluginManagerCore.getPlugin(CODEGPT_ID)).getVersion();
  }

  public static @NotNull Path getPluginBasePath() {
    return requireNonNull(PluginManagerCore.getPlugin(CODEGPT_ID)).getPluginPath();
  }

  public static @NotNull String getPluginOptionsPath() {
    return PathManager.getOptionsPath() + separator + "CodeGPT";
  }

  public static @NotNull String getIndexStorePath() {
    return getPluginOptionsPath() + separator + "indexes";
  }

  public static @NotNull String getLlamaSourcePath() {
    return getPluginBasePath() + separator + "llama.cpp";
  }

  public static @NotNull String getProjectIndexStorePath(@NotNull Project project) {
    return getIndexStorePath() + separator + project.getName();
  }
}
