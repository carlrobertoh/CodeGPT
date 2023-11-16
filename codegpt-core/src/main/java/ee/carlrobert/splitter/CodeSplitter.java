package ee.carlrobert.splitter;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

abstract class CodeSplitter implements Splitter {

  protected List<String> chunks = new ArrayList<>();

  protected abstract ParseTree getParseTree(CodePointCharStream charStream);

  protected abstract ParseTreeListener getParseTreeListener();

  protected String parseContext(ParserRuleContext ctx) {
    return ctx.start.getInputStream().getText(
        new Interval(ctx.start.getStartIndex(), ctx.stop.getStopIndex()));
  }

  @Override
  public List<String> split(String fileName, String content) {
    chunks = new ArrayList<>();
    ParseTreeWalker.DEFAULT.walk(
        getParseTreeListener(),
        getParseTree(CharStreams.fromString(content)));
    return chunks;
  }
}
