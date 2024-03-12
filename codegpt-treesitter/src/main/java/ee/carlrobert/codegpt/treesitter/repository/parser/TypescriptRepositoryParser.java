package ee.carlrobert.codegpt.treesitter.repository.parser;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.treesitter.repository.ProcessedTag;
import ee.carlrobert.codegpt.treesitter.repository.Tag;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.treesitter.TreeSitterTypescript;

public class TypescriptRepositoryParser extends RepositoryParser {

  TypescriptRepositoryParser(Project project) {
    super(project, new TreeSitterTypescript(), List.of("ts", "tsx"));
  }

  @Override
  protected List<String> getDefinitionQueries() {
    return List.of(
        "(function_signature\n"
            + "  name: (identifier) @name) @definition.function",
        "(method_signature\n"
            + "  name: (property_identifier) @name) @definition.method",
        "(abstract_method_signature\n"
            + "  name: (property_identifier) @name) @definition.method",
        "(abstract_class_declaration\n"
            + "  name: (type_identifier) @name) @definition.class",
        "(module\n"
            + "  name: (identifier) @name) @definition.module",
        "(interface_declaration\n"
            + "  name: (type_identifier) @name) @definition.interface");
  }

  @Override
  protected List<String> getReferenceQueries() {
    return List.of(
        "(type_annotation\n"
            + "  (type_identifier) @name) @reference.type",
        "(new_expression\n"
            + "  constructor: (identifier) @name) @reference.class");
  }

  @Override
  protected Set<ProcessedTag> processMatchedTags(
      Map<String, List<Tag>> matchedTags,
      Set<Tag> definitionTags,
      String codeSnippetPath) {
    return Set.of();
  }
}
