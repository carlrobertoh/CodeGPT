package ee.carlrobert.codegpt;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.llm.client.openai.completion.request.OpenAIChatCompletionMessage;

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
    if (conversation != null) {
      return conversation.getMessages().stream()
          .mapToInt(
              message -> countTokens(message.getPrompt()) + countTokens(message.getResponse()))
          .sum();
    }
    return 0;
  }

  public int countMessageTokens(OpenAIChatCompletionMessage message) {
    return countMessageTokens(message.getRole(), message.getContent());
  }

  public int countMessageTokens(String role, String content) {
    var tokensPerMessage = 4; // every message follows <|start|>{role/name}\n{content}<|end|>\n
    return countTokens(role + content) + tokensPerMessage;
  }

  public int countTokens(String text) {
    try {
      return encoding.countTokens(text);
    } catch (Exception ex) {
      LOG.error(ex);
      return 0;
    }
  }
}
