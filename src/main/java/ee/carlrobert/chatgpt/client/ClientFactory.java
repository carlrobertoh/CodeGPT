package ee.carlrobert.chatgpt.client;

import ee.carlrobert.chatgpt.client.chatgpt.ChatGPTClient;
import ee.carlrobert.chatgpt.client.gpt.GPTClient;
import ee.carlrobert.chatgpt.ide.settings.SettingsState;

public class ClientFactory {

  public Client getClient() {
    if (SettingsState.getInstance().isGPTOptionSelected) {
      return GPTClient.getInstance();
    }
    return ChatGPTClient.getInstance();
  }
}
