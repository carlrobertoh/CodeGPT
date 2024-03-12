package ee.carlrobert.codegpt.treesitter.repository.parser;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.VcsIgnoreManager;
import com.intellij.openapi.vfs.VirtualFileManager;
import ee.carlrobert.codegpt.treesitter.repository.ProcessedTag;
import ee.carlrobert.codegpt.treesitter.repository.QueryUtil;
import ee.carlrobert.codegpt.treesitter.repository.QueryUtil.QueryResult;
import ee.carlrobert.codegpt.treesitter.repository.Tag;
import ee.carlrobert.codegpt.treesitter.repository.TagType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.treesitter.TSLanguage;

public abstract class RepositoryParser {

  private final TSLanguage language;
  private final List<String> fileExtensions;
  private final VcsIgnoreManager ignoreManager;
  private final ChangeListManager changeListManager;

  public RepositoryParser(Project project, TSLanguage language, String fileExtensions) {
    this(project, language, List.of(fileExtensions));
  }

  public RepositoryParser(Project project, TSLanguage language, List<String> fileExtensions) {
    this.language = language;
    this.fileExtensions = fileExtensions;
    this.ignoreManager = VcsIgnoreManager.getInstance(project);
    this.changeListManager = ChangeListManager.getInstance(project);
  }

  protected abstract List<String> getDefinitionQueries();

  protected abstract List<String> getReferenceQueries();

  protected abstract Set<ProcessedTag> processMatchedTags(
      Map<String, List<Tag>> matchedTags,
      Set<Tag> definitionTags,
      String codeSnippetPath);

  public boolean isDefinitionsFileExists(String projectName) {
    return getExistingDefinitionsFilePath(projectName).toFile().exists();
  }

  public Set<Tag> createRepositoryDefinitionTags(String projectName, String projectPath) {
    var tags = getTags(
        getFileMappings(projectPath),
        getDefinitionQueries(),
        TagType.DEFINITION);
    saveRepositoryDefinitionTags(tags, getExistingDefinitionsFilePath(projectName));
    return tags;
  }

  public Set<ProcessedTag> processCode(
      String projectName,
      String projectPath,
      String codeSnippetPath,
      String codeSnippet) {
    var definitionTags = getRepositoryDefinitionTags(projectName, projectPath);
    var referenceTags = getCodeSnippetReferenceTags(codeSnippetPath, codeSnippet);
    var matchedTags = getMatchedTags(referenceTags, definitionTags);
    return processMatchedTags(matchedTags, definitionTags, codeSnippetPath);
  }

  protected Set<Tag> getTags(List<FileMapping> fileMappings, List<String> queries, TagType type) {
    return fileMappings.parallelStream()
        .map(mapping -> QueryUtil.extractTagsFromFile(language, mapping, queries, type))
        .flatMap(Set::stream)
        .collect(toSet());
  }

  protected List<QueryResult> query(String code, String queryString) {
    return QueryUtil.query(language, code, queryString);
  }

  private Set<Tag> getCodeSnippetReferenceTags(String codeSnippetPath, String codeSnippet) {
    return getTags(
        List.of(new FileMapping(codeSnippetPath, codeSnippet)),
        getReferenceQueries(),
        TagType.REFERENCE);
  }

  private Path getExistingDefinitionsFilePath(String projectName) {
    return Paths.get(
        System.getProperty("user.home"),
        // TODO
        String.format(".codegpt/%s/%s/definition-tags.json", projectName, fileExtensions.get(0)));
  }

  private List<FileMapping> getFileMappings(String pathString) {
    try (var pathStream = Files.walk(Path.of(pathString))) {
      return pathStream.filter(Files::isRegularFile)
          .filter(path -> {
            var virtualFile = VirtualFileManager.getInstance().findFileByNioPath(path);
            var endsWithExtension = fileExtensions.stream()
                .anyMatch(extension -> path.toString().endsWith(extension));
            return endsWithExtension
                && virtualFile != null
                && !changeListManager.isIgnoredFile(virtualFile)
                && !ignoreManager.isPotentiallyIgnoredFile(virtualFile)
                && virtualFile.getLength() < Math.pow(1024, 2);
          })
          .map(FileMapping::new)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Couldn't retrieve files.", e);
    }
  }

  private Set<Tag> getRepositoryDefinitionTags(String projectName, String projectPath) {
    if (projectPath == null || !isDefinitionsFileExists(projectName)) {
      return new HashSet<>();
    }

    try {
      var content = Files.readString(getExistingDefinitionsFilePath(projectName));
      return new ObjectMapper().readValue(content, new TypeReference<>() {
      });
    } catch (IOException e) {
      return new HashSet<>();
    }
  }

  private void saveRepositoryDefinitionTags(Set<Tag> tags, Path path) {
    try {
      Files.createDirectories(path.getParent());
      Files.writeString(path, new ObjectMapper().writeValueAsString(tags));
    } catch (IOException e) {
      throw new RuntimeException("Unable to save repository definition tags");
    }
  }

  private Map<String, List<Tag>> getMatchedTags(
      Set<Tag> referenceTags,
      Set<Tag> definitionTags) {
    return referenceTags.stream()
        .filter(refTag -> List.of("identifier", "type_identifier").contains(refTag.getSymbolName()))
        .flatMap(refTag -> definitionTags.stream()
            .filter(defTag -> isMatchingIdentifierTag(defTag, refTag)))
        .collect(Collectors.groupingBy(Tag::getFilePath, toList()));
  }

  private boolean isMatchingIdentifierTag(Tag defTag, Tag refTag) {
    return defTag != null && defTag.getName().equals(refTag.getName());
  }

  private String readFileContent(Path path) {
    try {
      return Files.readString(path);
    } catch (IOException e) {
      return "";
    }
  }

  public class FileMapping {

    private final String path;
    private final String content;

    public FileMapping(String path, String content) {
      this.path = path;
      this.content = content;
    }

    public FileMapping(Path path) {
      this.path = path.toAbsolutePath().toString();
      this.content = readFileContent(path);
    }

    public String getPath() {
      return path;
    }

    public String getContent() {
      return content;
    }
  }
}
