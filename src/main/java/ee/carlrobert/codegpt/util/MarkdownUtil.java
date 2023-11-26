package ee.carlrobert.codegpt.util;

import static java.util.stream.Collectors.toList;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import ee.carlrobert.codegpt.toolwindow.chat.ResponseNodeRenderer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownUtil {

  /**
   *  Splits a given string into a list of strings where each element is either a code block
   *  surrounded by triple backticks or a non-code block text.
   *
   *  @param inputMarkdown The input markdown formatted string to be split.
   *  @return A list of strings where each element is a code block or a non-code block text from the
   *  input string.
   */
  public static List<String> splitCodeBlocks(String inputMarkdown) {
    List<String> result = new ArrayList<>();
    Pattern pattern = Pattern.compile("(?s)```.*?```");
    Matcher matcher = pattern.matcher(inputMarkdown);
    int start = 0;
    while (matcher.find()) {
      result.add(inputMarkdown.substring(start, matcher.start()));
      result.add(matcher.group());
      start = matcher.end();
    }
    result.add(inputMarkdown.substring(start));
    return result.stream().filter(item -> !item.isBlank()).collect(toList());
  }

  public static String convertMdToHtml(String message) {
    MutableDataSet options = new MutableDataSet();
    var document = Parser.builder(options).build().parse(message);
    return HtmlRenderer.builder(options)
        .nodeRendererFactory(new ResponseNodeRenderer.Factory())
        .build()
        .render(document);
  }
}
