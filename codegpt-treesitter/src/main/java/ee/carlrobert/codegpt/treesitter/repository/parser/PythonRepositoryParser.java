package ee.carlrobert.codegpt.treesitter.repository.parser;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.treesitter.repository.ProcessedTag;
import ee.carlrobert.codegpt.treesitter.repository.Tag;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.treesitter.TreeSitterPython;

public class PythonRepositoryParser extends RepositoryParser {

  PythonRepositoryParser(Project project) {
    super(project, new TreeSitterPython(), "py");
  }

  @Override
  protected List<String> getDefinitionQueries() {
    return List.of(
        "(module (expression_statement (assignment left: (identifier) @name) @definition.constant))",
        "(class_definition\n"
            + "  name: (identifier) @name) @definition.class",
        "(function_definition\n"
            + "  name: (identifier) @name) @definition.function");
  }

  @Override
  protected List<String> getReferenceQueries() {
    return List.of(
        "(call\n"
            + "  function: [\n"
            + "      (identifier) @name\n"
            + "      (attribute\n"
            + "        attribute: (identifier) @name)\n"
            + "  ]) @reference.call");
  }

  @Override
  protected Set<ProcessedTag> processMatchedTags(
      Map<String, List<Tag>> matchedTags,
      Set<Tag> definitionTags,
      String codeSnippetPath) {
    return Set.of();
  }
}
