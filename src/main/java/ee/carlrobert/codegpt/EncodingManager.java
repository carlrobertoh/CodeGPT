package ee.carlrobert.codegpt;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionDetailedMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionStandardMessage;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIMessageTextContent;
import java.util.List;
import java.util.stream.Stream;

@Service
public final class EncodingManager {

  private static final Logger LOG = Logger.getInstance(EncodingManager.class);

  private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
  private final Encoding encoding = registry.getEncoding(EncodingType.CL100K_BASE);

  private EncodingManager() {
  }

  public static EncodingManager getInstance() {
    return ApplicationManager.getApplication().getService(EncodingManager.class);
  }

  public int countConversationTokens(Conversation conversation) {
    return (conversation == null ? Stream.<Message>empty() : conversation.getMessages().stream())
        .mapToInt(
            message -> countTokens(message.getPrompt()) + countTokens(message.getResponse()))
        .sum();
  }

  public int countMessageTokens(OpenAIChatCompletionMessage message) {
    if (message instanceof OpenAIChatCompletionStandardMessage standardMessage) {
      return countMessageTokens(standardMessage.getRole(), standardMessage.getContent());
    }

    return ((OpenAIChatCompletionDetailedMessage) message).getContent().stream()
        .filter(OpenAIMessageTextContent.class::isInstance)
        .mapToInt(it -> countMessageTokens(
            ((OpenAIChatCompletionDetailedMessage) message).getRole(),
            ((OpenAIMessageTextContent) it).getText()))
        .sum();
  }

  public int countMessageTokens(String role, String content) {
    var tokensPerMessage = 4; // every message follows <|start|>{role/name}\n{content}<|end|>\n
    return countTokens(role + content) + tokensPerMessage;
  }

  public int countTokens(String text) {
    try {
      // #444: Cl100kParser.split() throws AssertionError "Input is not UTF-8: "
      return encoding.countTokens(text);
    } catch (Exception | Error ex) {
      LOG.warn("Could not count tokens for: " + text, ex);
      return 0;
    }
  }

  /**
   * Truncates the given text to the given number of tokens.
   *
   * @param text      The text to truncate.
   * @param maxTokens The maximum number of tokens to keep.
   * @param fromStart Whether to truncate from the start or the end of the text.
   * @return The truncated text.
   */
  public String truncateText(String text, int maxTokens, boolean fromStart) {
    var tokens = encoding.encode(text);
    int tokensToRetrieve = Math.min(maxTokens, tokens.size());
    int startIndex = fromStart ? 0 : tokens.size() - tokensToRetrieve;
    var truncatedList =
        tokens.boxed().subList(startIndex, startIndex + tokensToRetrieve);
    return encoding.decode(convertToIntArrayList(truncatedList));
  }

  private IntArrayList convertToIntArrayList(List<Integer> tokens) {
    var result = new IntArrayList(tokens.size());
    tokens.forEach(result::add);
    return result;
  }
}
