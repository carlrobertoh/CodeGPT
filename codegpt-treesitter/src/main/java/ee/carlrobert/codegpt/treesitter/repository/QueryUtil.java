package ee.carlrobert.codegpt.treesitter.repository;

import static java.util.stream.Collectors.toSet;

import ee.carlrobert.codegpt.treesitter.repository.parser.RepositoryParser.FileMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.treesitter.TSLanguage;
import org.treesitter.TSNode;
import org.treesitter.TSParser;
import org.treesitter.TSQuery;
import org.treesitter.TSQueryCursor;
import org.treesitter.TSQueryMatch;
import org.treesitter.TSTree;

public class QueryUtil {

  public static Set<Tag> extractTagsFromFile(
      TSLanguage language,
      FileMapping fileMapping,
      List<String> queries,
      TagType tagType) {
    var fileContent = fileMapping.getContent();
    var rootNode = QueryUtil.getTree(language, fileContent).getRootNode();
    return queries.stream()
        .map(query -> new TSQuery(language, query))
        .flatMap(query ->
            query(query, rootNode, node -> new Tag(
                fileMapping.getPath(),
                fileMapping.getContent().substring(node.getStartByte(), node.getEndByte()),
                language.symbolName(node.getSymbol()),
                tagType)).stream())
        .collect(toSet());
  }

  public static List<QueryResult> query(TSLanguage language, String code, String query) {
    return query(
        new TSQuery(language, query),
        getTree(language, code).getRootNode(),
        node -> new QueryResult(
            code.substring(node.getStartByte(), node.getEndByte()),
            language.symbolName(node.getSymbol())));
  }

  private static TSTree getTree(TSLanguage language, String input) {
    var parser = new TSParser();
    parser.setLanguage(language);
    return parser.parseString(null, input);
  }

  private static <T> List<T> query(TSQuery query, TSNode rootNode, Function<TSNode, T> onCapture) {
    var cursor = new TSQueryCursor();
    cursor.exec(query, rootNode);
    var matches = new ArrayList<T>();
    var match = new TSQueryMatch();
    while (cursor.nextMatch(match)) {
      for (var capture : match.getCaptures()) {
        try {
          matches.add(onCapture.apply(capture.getNode()));
        } catch (Throwable t) {
          // todo: log
        }
      }
    }
    return matches;
  }

  public static class QueryResult {

    private final String name;
    private final String symbolName;

    public QueryResult(String name, String symbolName) {
      this.name = name;
      this.symbolName = symbolName;
    }

    public String getName() {
      return name;
    }

    public String getSymbolName() {
      return symbolName;
    }
  }
}
