package ee.carlrobert.codegpt.ide.conversations;

import ee.carlrobert.codegpt.client.ClientCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationsContainer {

  private Map<ClientCode, List<Conversation>> conversationsMapping = new HashMap<>();

  public Map<ClientCode, List<Conversation>> getConversationsMapping() {
    return conversationsMapping;
  }

  public void setConversationsMapping(Map<ClientCode, List<Conversation>> conversationsMapping) {
    this.conversationsMapping = conversationsMapping;
  }
}
