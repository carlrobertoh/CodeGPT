package ee.carlrobert.codegpt.treesitter;

import org.treesitter.TSLanguage;
import org.treesitter.TreeSitterCSharp;
import org.treesitter.TreeSitterCpp;
import org.treesitter.TreeSitterCss;
import org.treesitter.TreeSitterDart;
import org.treesitter.TreeSitterDockerfile;
import org.treesitter.TreeSitterElixir;
import org.treesitter.TreeSitterErlang;
import org.treesitter.TreeSitterFortran;
import org.treesitter.TreeSitterGitattributes;
import org.treesitter.TreeSitterGo;
import org.treesitter.TreeSitterGraphql;
import org.treesitter.TreeSitterHtml;
import org.treesitter.TreeSitterJava;
import org.treesitter.TreeSitterJavascript;
import org.treesitter.TreeSitterJson;
import org.treesitter.TreeSitterKotlin;
import org.treesitter.TreeSitterLatex;
import org.treesitter.TreeSitterLua;
import org.treesitter.TreeSitterM68k;
import org.treesitter.TreeSitterMarkdown;
import org.treesitter.TreeSitterObjc;
import org.treesitter.TreeSitterPerl;
import org.treesitter.TreeSitterPhp;
import org.treesitter.TreeSitterPython;
import org.treesitter.TreeSitterRuby;
import org.treesitter.TreeSitterRust;
import org.treesitter.TreeSitterScala;
import org.treesitter.TreeSitterScss;
import org.treesitter.TreeSitterSvelte;
import org.treesitter.TreeSitterSwift;
import org.treesitter.TreeSitterTypescript;
import org.treesitter.TreeSitterYaml;

public class CodeCompletionParserFactory {

  public static CodeCompletionParser getParserForFileExtension(String extension)
      throws IllegalArgumentException {
    return new CodeCompletionParser(getLanguageForExtension(extension));
  }

  private static TSLanguage getLanguageForExtension(String extension) {
    switch (extension) {
      case "java":
        return new TreeSitterJava();
      case "php":
        return new TreeSitterPhp();
      case "py":
        return new TreeSitterPython();
      case "ts":
      case "tsx":
        return new TreeSitterTypescript();
      case "js":
      case "jsx":
        return new TreeSitterJavascript();
      case "c":
      case "h":
      case "cpp":
      case "cxx":
      case "cc":
      case "c++":
      case "hpp":
      case "hxx":
      case "hh":
      case "h++":
        return new TreeSitterCpp();
      case "cs":
        return new TreeSitterCSharp();
      case "css":
        return new TreeSitterCss();
      case "dart":
        return new TreeSitterDart();
      case "dockerfile":
        return new TreeSitterDockerfile();
      case "elixir":
      case "ex":
      case "exs":
        return new TreeSitterElixir();
      case "erl":
      case "hrl":
        return new TreeSitterErlang();
      case "f90":
      case "f95":
      case "f03":
      case "f08":
        return new TreeSitterFortran();
      case "gitattributes":
        return new TreeSitterGitattributes();
      case "go":
        return new TreeSitterGo();
      case "graphql":
      case "gql":
        return new TreeSitterGraphql();
      case "html":
      case "htm":
        return new TreeSitterHtml();
      case "json":
        return new TreeSitterJson();
      case "kotlin":
      case "kt":
      case "kts":
        return new TreeSitterKotlin();
      case "latex":
      case "tex":
        return new TreeSitterLatex();
      case "lua":
        return new TreeSitterLua();
      case "m68k":
        return new TreeSitterM68k();
      case "markdown":
      case "md":
        return new TreeSitterMarkdown();
      case "objc":
      case "m":
      case "mm":
        return new TreeSitterObjc();
      case "perl":
      case "pl":
      case "pm":
        return new TreeSitterPerl();
      case "ruby":
      case "rb":
        return new TreeSitterRuby();
      case "rust":
      case "rs":
        return new TreeSitterRust();
      case "scala":
      case "sc":
        return new TreeSitterScala();
      case "scss":
        return new TreeSitterScss();
      case "svelte":
        return new TreeSitterSvelte();
      case "swift":
        return new TreeSitterSwift();
      case "yml":
      case "yaml":
        return new TreeSitterYaml();
      default:
        throw new IllegalArgumentException("Unsupported file extension: " + extension);
    }
  }
}
