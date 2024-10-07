package ee.carlrobert.codegpt.completions;

import ee.carlrobert.codegpt.ReferencedFile;
import ee.carlrobert.codegpt.conversations.Conversation;
import ee.carlrobert.codegpt.conversations.message.Message;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class CallParameters {

  private final Conversation conversation;
  private final ConversationType conversationType;
  private final Message message;
  private final boolean retry;
  private final String highlightedText;
  private String imageMediaType;
  private byte[] imageData;
  private List<ReferencedFile> referencedFiles;

  public CallParameters(Conversation conversation, Message message) {
    this(conversation, ConversationType.DEFAULT, message, null, false);
  }

  public CallParameters(
      Conversation conversation,
      ConversationType conversationType,
      Message message,
      @Nullable String highlightedText,
      boolean retry) {
    this.conversation = conversation;
    this.conversationType = conversationType;
    this.message = message;
    this.highlightedText = highlightedText;
    this.retry = retry;
  }

  public Conversation getConversation() {
    return conversation;
  }

  public ConversationType getConversationType() {
    return conversationType;
  }

  public Message getMessage() {
    return message;
  }

  public boolean isRetry() {
    return retry;
  }

  public @Nullable String getImageMediaType() {
    return imageMediaType;
  }

  public void setImageMediaType(@Nullable String imageMediaType) {
    this.imageMediaType = imageMediaType;
  }

  public byte[] getImageData() {
    return imageData;
  }

  public void setImageData(byte[] imageData) {
    this.imageData = imageData;
  }

  public @Nullable String getHighlightedText() {
    return highlightedText;
  }

  public @Nullable List<ReferencedFile> getReferencedFiles() {
    return referencedFiles;
  }

  public void setReferencedFiles(List<ReferencedFile> referencedFiles) {
    this.referencedFiles = referencedFiles;
  }
}
