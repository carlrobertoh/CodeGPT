package ee.carlrobert.codegpt.toolwindow.chat;

import ee.carlrobert.codegpt.util.MarkdownUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamParser {

  public List<StreamParseResponse> parse(String message) {
    var blocks = MarkdownUtils.splitCodeBlocks(message);
    var ret = blocks.stream().map(block -> {
      if (block.startsWith("```")) {
        return new StreamParseResponse(StreamResponseType.CODE, block);
      } else {
        return new StreamParseResponse(StreamResponseType.TEXT, block);
      }
    }).toArray(StreamParseResponse[]::new);

    return List.of(ret);
  }

  public void clear() {
  }
}
