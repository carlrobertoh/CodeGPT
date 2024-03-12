package ee.carlrobert.codegpt.treesitter.repository.parser;

import com.intellij.openapi.project.Project;

public class RepositoryParserFactory {

  public static RepositoryParser getParserForFileExtension(Project project, String extension)
      throws IllegalArgumentException {
    switch (extension) {
      case "java":
        return new JavaRepositoryParser(project);
      // TODO: Add support for more languages
      default:
        throw new IllegalArgumentException("Unsupported file extension: " + extension);
    }
  }
}
