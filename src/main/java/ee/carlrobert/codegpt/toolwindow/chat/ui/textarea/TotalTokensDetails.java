package ee.carlrobert.codegpt.toolwindow.chat.ui.textarea;

import ee.carlrobert.codegpt.EncodingManager;
import ee.carlrobert.codegpt.settings.persona.PersonaSettings;

public class TotalTokensDetails {

  private final int systemPromptTokens;
  private int conversationTokens;
  private int userPromptTokens;
  private int highlightedTokens;
  private int referencedFilesTokens;

  public TotalTokensDetails(EncodingManager encodingManager) {
    systemPromptTokens = encodingManager.countTokens(PersonaSettings.getSystemPrompt());
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

  public void setReferencedFilesTokens(int referencedFilesTokens) {
    this.referencedFilesTokens = referencedFilesTokens;
  }

  public int getReferencedFilesTokens() {
    return referencedFilesTokens;
  }

  public int getTotal() {
    return systemPromptTokens
        + conversationTokens
        + userPromptTokens
        + highlightedTokens
        + referencedFilesTokens;
  }
}
