package ee.carlrobert.splitter;

import grammar.TypeScriptLexer;
import grammar.TypeScriptParser;
import grammar.TypeScriptParserBaseListener;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;

public class TypeScriptCodeSplitter extends CodeSplitter {

  @Override
  protected ParseTree getParseTree(CodePointCharStream charStream) {
    return new TypeScriptParser(new CommonTokenStream(new TypeScriptLexer(charStream))).program();
  }

  @Override
  protected ParseTreeListener getParseTreeListener() {
    return new TypeScriptParserBaseListener() {};
  }
}
