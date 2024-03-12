package ee.carlrobert.codegpt.treesitter.repository.parser;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.intellij.openapi.project.Project;
import ee.carlrobert.codegpt.treesitter.repository.ProcessedTag;
import ee.carlrobert.codegpt.treesitter.repository.Tag;
import ee.carlrobert.codegpt.treesitter.repository.TagType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.treesitter.TreeSitterJava;

public class JavaRepositoryParser extends RepositoryParser {

  JavaRepositoryParser(Project project) {
    super(project, new TreeSitterJava(), "java");
  }

  @Override
  protected List<String> getDefinitionQueries() {
    return List.of(
        "(class_declaration name: (identifier) @name.definition.class) @definition.class",
        "(method_declaration name: (identifier) @name.definition.method) @definition.method",
        "(interface_declaration name: (identifier) @name.definition.interface) @definition.interface",
        "(package_declaration) @definition.package");
  }

  @Override
  protected List<String> getReferenceQueries() {
    return List.of(
        "(method_invocation name: (identifier) @name.reference.call arguments: (argument_list) @reference.call)",
        "(type_list (type_identifier) @name.reference.implementation) @reference.implementation",
        "(object_creation_expression type: (type_identifier) @name.reference.class) @reference.class",
        "(superclass (type_identifier) @name.reference.class) @reference.class");
  }

  @Override
  protected Set<ProcessedTag> processMatchedTags(
      Map<String, List<Tag>> matchedTags,
      Set<Tag> definitionTags,
      String codeSnippetPath) {
    var dependencies = findDependencies(codeSnippetPath, definitionTags);
    return matchedTags.entrySet().stream()
        .filter(entry -> {
          var className = Path.of(entry.getKey()).getFileName().toString().replace(".java", "");
          return dependencies.stream().anyMatch(dep -> dep.endsWith(className));
        })
        .map(this::processMatchEntry)
        .limit(10)
        .collect(toSet());
  }

  private @Nullable String getPackageDeclaration(String code) {
    var result = query(code, "(package_declaration) @definition.package");
    if (result.isEmpty()) {
      return null;
    }
    return result.get(0).getName();
  }

  private Set<String> getImports(String code) {
    return query(code, "(import_declaration) @definition.import").stream()
        .map(it -> extractPackageName(it.getName()))
        .collect(toSet());
  }

  private Set<String> findDependencies(String codeSnippetPath, Set<Tag> definitionTags) {
    var fileContent = readFileContent(Path.of(codeSnippetPath));
    var packageDeclaration = getPackageDeclaration(fileContent);
    var packageLevelDependencies = definitionTags.stream()
        .filter(
            defTag -> "package_declaration".equals(defTag.getSymbolName())
                && packageDeclaration != null
                && defTag.getName().contentEquals(packageDeclaration))
        .map(declaration -> {
          var fileName = Path.of(declaration.getFilePath()).getFileName().toString();
          return String.format(
              "%s.%s",
              extractPackageName(declaration.getName()),
              fileName.replace(".java", ""));
        })
        .collect(toSet());
    packageLevelDependencies.addAll(getImports(fileContent));
    return packageLevelDependencies;
  }

  private ProcessedTag processMatchEntry(Entry<String, List<Tag>> entry) {
    var fileContent = readFileContent(Path.of(entry.getKey()));
    var tags = getTags(
        List.of(new FileMapping(entry.getKey(), fileContent)),
        getDefinitionQueries(),
        TagType.DEFINITION);
    var methodDeclarations = tags
        .stream()
        .filter(
            tag -> "method_declaration".equals(tag.getSymbolName())
                && entry.getValue().stream().anyMatch(target ->
                tag.getName().contains(target.getName())))
        .map(Tag::getName)
        .collect(toList());
    return new ProcessedTag(entry.getKey(), String.join("\n\n", methodDeclarations));
  }

  private static String extractPackageName(String line) {
    var regex = "(?:import|package)\\s+([\\w.]+)\\.*";
    var pattern = Pattern.compile(regex);
    var matcher = pattern.matcher(line);
    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  private static String readFileContent(Path path) {
    try {
      return Files.readString(path);
    } catch (IOException e) {
      return "";
    }
  }
}
