package ee.carlrobert.codegpt.toolwindow.chat;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;

public class EncodingManager {

  private static final EncodingManager instance = new EncodingManager();
  private final EncodingRegistry registry;
  private Encoding encoding;

  private EncodingManager() {
    this.registry = Encodings.newDefaultEncodingRegistry();
    this.encoding = registry.getEncoding(EncodingType.CL100K_BASE);
  }

  public static EncodingManager getInstance() {
    return instance;
  }

  public void setEncoding(String modelName) {
    this.encoding = registry.getEncodingForModel(modelName).orElseThrow();
  }

  public int countTokens(String message) {
    return encoding.countTokens(message);
  }
}
