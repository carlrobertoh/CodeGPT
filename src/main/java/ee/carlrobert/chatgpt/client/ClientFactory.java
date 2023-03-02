package ee.carlrobert.chatgpt.client;

import ee.carlrobert.chatgpt.client.unofficial.UnofficialChatGPTClient;
import ee.carlrobert.chatgpt.client.official.chat.ChatCompletionClient;
import ee.carlrobert.chatgpt.client.official.text.TextCompletionClient;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;

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
