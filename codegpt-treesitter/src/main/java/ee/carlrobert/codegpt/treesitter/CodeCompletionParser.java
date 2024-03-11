package ee.carlrobert.codegpt.treesitter;

import org.treesitter.TSLanguage;
import org.treesitter.TSNode;
import org.treesitter.TSParser;
import org.treesitter.TSTree;

public class CodeCompletionParser {

  protected final TSLanguage language;

  public CodeCompletionParser(TSLanguage language) {
    this.language = language;
  }

  public String parse(String prefix, String suffix, String output) {
    var result = new StringBuilder(output);
    while (result.length() > 0) {
      if (containsSyntaxErrors(prefix + result + suffix)) {
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

  private boolean containsSyntaxErrors(String input) {
    return containsSyntaxErrors(getTree(input).getRootNode());
  }

  private boolean containsSyntaxErrors(TSNode node) {
    if (node.isMissing() || node.hasError()) {
      return true;
    }

    for (int i = 0; i < node.getChildCount(); i++) {
      if (containsSyntaxErrors(node.getChild(i))) {
        return true;
      }
    }
    return false;
  }

  private TSTree getTree(String input) {
    var parser = new TSParser();
    parser.setLanguage(language);
    return parser.parseString(null, input);
  }
}
