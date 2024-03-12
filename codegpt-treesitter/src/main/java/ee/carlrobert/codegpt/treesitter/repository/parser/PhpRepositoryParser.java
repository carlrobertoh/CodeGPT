package ee.carlrobert.codegpt.treesitter.repository.parser;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.treesitter.repository.ProcessedTag;
import ee.carlrobert.codegpt.treesitter.repository.Tag;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.treesitter.TreeSitterPhp;

public class PhpRepositoryParser extends RepositoryParser {

  PhpRepositoryParser(Project project) {
    super(project, new TreeSitterPhp(), "php");
  }

  @Override
  protected List<String> getDefinitionQueries() {
    return List.of(
        "(namespace_definition\n"
            + "  name: (namespace_name) @name) @module",
        "(interface_declaration\n"
            + "  name: (name) @name) @definition.interface",
        "(trait_declaration\n"
            + "  name: (name) @name) @definition.interface",
        "(class_declaration\n"
            + "  name: (name) @name) @definition.class",
        "(class_interface_clause [(name) (qualified_name)] @name) @impl",
        "(property_declaration\n"
            + "  (property_element (variable_name (name) @name))) @definition.field",
        "(function_definition\n"
            + "  name: (name) @name) @definition.function",
        "(method_declaration\n"
            + "  name: (name) @name) @definition.function");
  }

  @Override
  protected List<String> getReferenceQueries() {
    return List.of(
        "(object_creation_expression\n"
            + "  [\n"
            + "    (qualified_name (name) @name)\n"
            + "    (variable_name (name) @name)\n"
            + "  ]) @reference.class",
        "(function_call_expression\n"
            + "  function: [\n"
            + "    (qualified_name (name) @name)\n"
            + "    (variable_name (name)) @name\n"
            + "  ]) @reference.call",
        "(scoped_call_expression\n"
            + "  name: (name) @name) @reference.call",
        "(member_call_expression\n"
            + "  name: (name) @name) @reference.call");
  }

  @Override
  protected Set<ProcessedTag> processMatchedTags(
      Map<String, List<Tag>> matchedTags,
      Set<Tag> definitionTags,
      String codeSnippetPath) {
    return Set.of();
  }
}
