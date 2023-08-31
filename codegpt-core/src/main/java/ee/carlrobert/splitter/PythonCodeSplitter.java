package ee.carlrobert.splitter;

import grammar.PythonLexer;
import grammar.PythonParser;
import grammar.PythonParserBaseListener;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;

public class PythonCodeSplitter extends CodeSplitter {

  @Override
  protected ParseTree getParseTree(CodePointCharStream charStream) {
    return new PythonParser(new CommonTokenStream(new PythonLexer(charStream))).file_input();
  }

  @Override
  protected ParseTreeListener getParseTreeListener() {
    return new PythonParserBaseListener() {
      @Override
      public void enterClass_or_func_def_stmt(PythonParser.Class_or_func_def_stmtContext ctx) {
        chunks.add(parseContext(ctx));
      }
    };
  }
}
