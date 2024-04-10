package ee.carlrobert.codegpt.toolwindow.chat;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamParser {

  private static final String CODE_BLOCK_STARTING_REGEX = "```[a-zA-Z]*\n";
  private final StringBuilder messageBuilder = new StringBuilder();
  private boolean isProcessingCode;

  public List<StreamParseResponse> parse(String message) {
    message = message.replace("\r", "");
    messageBuilder.append(message);

    Pattern pattern = Pattern.compile(CODE_BLOCK_STARTING_REGEX);
    Matcher matcher = pattern.matcher(messageBuilder.toString());
    if (!isProcessingCode && matcher.find()) {
      isProcessingCode = true;

      var startingIndex = messageBuilder.indexOf(matcher.group());
      var prevMessage = messageBuilder.substring(0, startingIndex);
      messageBuilder.delete(0, messageBuilder.indexOf(matcher.group()));

      return List.of(
          new StreamParseResponse(StreamResponseType.TEXT, prevMessage),
          new StreamParseResponse(StreamResponseType.CODE, messageBuilder.toString()));
    }

    var endingIndex = messageBuilder.indexOf("```\n", 1);
    if (isProcessingCode && endingIndex > 0) {
      isProcessingCode = false;

      var codeResponse = messageBuilder.substring(0, endingIndex + 3);
      messageBuilder.delete(0, endingIndex + 3);

      return List.of(
          new StreamParseResponse(StreamResponseType.CODE, codeResponse),
          new StreamParseResponse(StreamResponseType.TEXT, messageBuilder.toString()));
    }

    return List.of(new StreamParseResponse(
        isProcessingCode
            ? StreamResponseType.CODE
            : StreamResponseType.TEXT,
        messageBuilder.toString()));
  }

  public void clear() {
    messageBuilder.setLength(0);
    isProcessingCode = false;
  }

  public record StreamParseResponse(StreamResponseType type, String response) {
  }
}
