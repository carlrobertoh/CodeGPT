package ee.carlrobert.codegpt;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import ee.carlrobert.llm.client.openai.completion.chat.request.OpenAIChatCompletionMessage;

@Service
public final class EncodingManager {

  private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
  private final Encoding encoding = registry.getEncoding(EncodingType.CL100K_BASE);

  private EncodingManager() {
  }

  public static EncodingManager getInstance() {
    return ApplicationManager.getApplication().getService(EncodingManager.class);
  }

  public int countMessageTokens(OpenAIChatCompletionMessage message) {
    var tokensPerMessage = 4; // every message follows <|start|>{role/name}\n{content}<|end|>\n
    return encoding.countTokens(message.getRole() + message.getContent()) + tokensPerMessage;
  }

  public int countTokens(String text) {
    return encoding.countTokens(text);
  }
}
