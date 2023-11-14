package ee.carlrobert.codegpt.toolwindow.chat;

import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.settings.configuration.ConfigurationState;

public class TokenDetails {

  private final int systemPromptTokens;
  private int conversationTokens;
  private int userPromptTokens;
  private int highlightedTokens;

  public TokenDetails(EncodingManager encodingManager) {
    systemPromptTokens = encodingManager.countTokens(
        ConfigurationState.getInstance().getSystemPrompt());
  }

  public int getSystemPromptTokens() {
    return systemPromptTokens;
  }

  public void setConversationTokens(int conversationTokens) {
    this.conversationTokens = conversationTokens;
  }

  public int getConversationTokens() {
    return conversationTokens;
  }

  public void setUserPromptTokens(int userPromptTokens) {
    this.userPromptTokens = userPromptTokens;
  }

  public int getUserPromptTokens() {
    return userPromptTokens;
  }

  public void setHighlightedTokens(int highlightedTokens) {
    this.highlightedTokens = highlightedTokens;
  }

  public int getHighlightedTokens() {
    return highlightedTokens;
  }

  public int getTotal() {
    return systemPromptTokens + conversationTokens + userPromptTokens + highlightedTokens;
  }
}
