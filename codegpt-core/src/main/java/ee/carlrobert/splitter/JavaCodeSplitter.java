package ee.carlrobert.splitter;

import grammar.JavaLexer;
import grammar.JavaParser;
import grammar.JavaParserBaseListener;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;

public class JavaCodeSplitter extends CodeSplitter {

  @Override
  protected ParseTree getParseTree(CodePointCharStream charStream) {
    return new JavaParser(new CommonTokenStream(new JavaLexer(charStream))).compilationUnit();
  }

  @Override
  protected ParseTreeListener getParseTreeListener() {
    return new JavaParserBaseListener() {
      @Override
      public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        chunks.add(parseContext(ctx));
      }

      @Override
      public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        chunks.add(parseContext(ctx));
      }
    };
  }
}
