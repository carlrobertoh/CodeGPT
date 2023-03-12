package ee.carlrobert.codegpt.client;

import ee.carlrobert.codegpt.client.unofficial.UnofficialChatGPTClient;
import ee.carlrobert.codegpt.client.official.chat.ChatCompletionClient;
import ee.carlrobert.codegpt.client.official.text.TextCompletionClient;
import ee.carlrobert.codegpt.ide.settings.SettingsState;

public class ClientFactory {

  public Client getClient() {
    if (SettingsState.getInstance().isGPTOptionSelected) {
      if (SettingsState.getInstance().isChatCompletionOptionSelected) {
        return ChatCompletionClient.getInstance();
      }
      return TextCompletionClient.getInstance();
    }
    return UnofficialChatGPTClient.getInstance();
  }
}
