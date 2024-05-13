package ee.carlrobert.codegpt.treesitter;

import org.treesitter.TSLanguage;
import org.treesitter.TSParser;
import org.treesitter.TSTree;

public class CodeCompletionParser {

  protected final TSLanguage language;

  public CodeCompletionParser(TSLanguage language) {
    this.language = language;
  }

  public String parse(String prefix, String suffix, String output) {
    var result = new StringBuilder(output);
    while (!result.isEmpty()) {
      if (containsError(prefix + result + suffix)) {
        result.deleteCharAt(result.length() - 1);
      } else {
        return result.toString();
      }
    }

    if (output.contains("\n")) {
      return parse(prefix, suffix, output.substring(0, output.indexOf("\n")));
    }

    return output;
  }

  private boolean containsError(String input) {
    var treeString = getTree(input).getRootNode().toString();
    return treeString.contains("ERROR")
        || treeString.contains("MISSING \"}\"")
        || treeString.contains("MISSING \")\"");
  }

  private TSTree getTree(String input) {
    var parser = new TSParser();
    parser.setLanguage(language);
    return parser.parseString(null, input);
  }
}
